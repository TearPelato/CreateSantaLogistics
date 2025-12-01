package net.liukrast.santa.world.level.levelgen;

import com.mojang.serialization.Codec;
import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.network.protocol.game.SantaPositionUpdatePacket;
import net.liukrast.santa.world.level.block.SantaDocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@NonnullDefault
public class SantaBase extends SavedData {
    private @Nullable BlockPos pos;
    private boolean flag = false;

    public static @Nullable BlockPos getPos(ServerLevel level) {
        return get(level).pos;
    }

    public static boolean getFlag(ServerLevel level) {
        return get(level).flag;
    }

    private static SantaBase get(ServerLevel level) {
        return level.getDataStorage()
                .computeIfAbsent(new Factory<>(SantaBase::new, SantaBase::load), SantaConstants.MOD_ID + "_santa_base");
    }

    public SantaBase() {}

    public void setPos(BlockPos pos) {
        this.pos = pos;
        setDirty();
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
        setDirty();
    }

    private SantaBase(@Nullable BlockPos pos, boolean flag) {
        this.pos = pos;
        this.flag = flag;
    }

    public static SantaBase load(CompoundTag tag, HolderLookup.Provider registries) {
        var ops = registries.createSerializationContext(NbtOps.INSTANCE);
        var opt = BlockPos.CODEC.optionalFieldOf("pos").codec().parse(ops, tag).resultOrPartial(SantaConstants.LOGGER::error).orElse(Optional.empty());
        var flag = Codec.BOOL.fieldOf("flag").codec().parse(ops, tag).resultOrPartial(SantaConstants.LOGGER::error).orElse(false);
        return new SantaBase(opt.orElse(null), flag);
    }

    @Nullable
    public static BlockPos generate(ServerLevel level, @Nullable BlockPos user) {
        SantaConstants.LOGGER.info("Santa Logistics is looking for a valid position to place santa's base. It may take a little longer, but it is only required once per world");
        SantaBase base = get(level);
        if(user != null) {
            base.setPos(user);
            SantaConstants.LOGGER.info("Santa base placed manually at {}", user);
            sendUpdate(level, user);
            return user;
        } else {
            BlockPos spawn = level.getSharedSpawnPos();
            ChunkPos pos = SantaBase.findBiomeChunk(level, level.getChunk(spawn).getPos());
            if(pos == null) {
                base.setFlag(true);
                SantaConstants.LOGGER.error("Unable to spawn chunk automatically. Please choose a position to place santa's base via command");
                return null;
            }
            int x = pos.getBlockX(8);
            int z = pos.getBlockZ(19);
            //TODO: Must flag the chunks to generate here, else we cannot access the world height and will return -64
            //int h = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
            level.getChunkSource().getChunk(pos.x, pos.z, ChunkStatus.FEATURES, true);
            int h = level.getChunk(pos.x, pos.z).getHeight(Heightmap.Types.WORLD_SURFACE_WG, x & 15, z & 15);
            BlockPos pos1 = new BlockPos(x, h, z);
            base.setPos(pos1);
            sendUpdate(level, pos1);
            //TODO: Place structure
            SantaConstants.LOGGER.info("Santa Logistics has placed correctly the structure");
            return pos1;
        }
    }

    public static void sendUpdate(ServerLevel level, BlockPos origin) {
        SantaDocks docks = SantaDocks.get(level);
        Map<BlockPos, String> map = docks.blockPosMap();
        List<Map.Entry<BlockPos, String>> ordered = map.entrySet().stream()
                .sorted(Comparator.comparingDouble(e -> e.getKey().distSqr(origin)))
                .toList();
        PacketDistributor.sendToAllPlayers(new SantaPositionUpdatePacket(origin, ordered.stream().map(Map.Entry::getKey).toList()));
    }
    @Nullable
    public static ChunkPos findBiomeChunk(
            ServerLevel level,
            ChunkPos center) {
        SantaConstants.LOGGER.info("Santa base finding started...");
        int minRadius = 32;
        int maxRadius = 512;
        int step = 4;
        ChunkPos backup = null;
        for (int r = minRadius; r <= maxRadius; r += step) {
            for (int dx = -r; dx <= r; dx += step) {
                for (int dz = -r; dz <= r; dz += step) {
                    if (Math.abs(dx) != r && Math.abs(dz) != r) continue;
                    ChunkPos chunkPos = new ChunkPos(center.x + dx, center.z + dz);
                    BlockPos pos = chunkPos.getMiddleBlockPosition(0);
                    Holder<Biome> biome = level.getBiome(pos);
                    if (!areChunksNotGenerated(level, chunkPos)) continue;
                    backup = chunkPos;
                    if (!isSnowy(biome)) continue;
                    SantaConstants.LOGGER.info("Found potential chunk {}", pos);
                    return chunkPos;
                }
            }
        }
        SantaConstants.LOGGER.info("No potential snowy chunk found, skipping to backup");
        return backup;
    }

    private static boolean areChunksNotGenerated(ServerLevel level, ChunkPos pos) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                ChunkPos cp = new ChunkPos(pos.x + x, pos.z + z);
                if (level.getChunkSource().hasChunk(cp.x, cp.z)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isSnowy(Holder<Biome> biome) {
        return biome.is(Biomes.SNOWY_PLAINS)
                || biome.is(Biomes.SNOWY_TAIGA)
                || biome.is(Biomes.ICE_SPIKES)
                || biome.is(Biomes.GROVE);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        var ops = registries.createSerializationContext(NbtOps.INSTANCE);
        BlockPos.CODEC.optionalFieldOf("pos").codec().encodeStart(ops, Optional.ofNullable(pos))
                .resultOrPartial(SantaConstants.LOGGER::error)
                .ifPresent(encoded -> {
                    if(encoded instanceof CompoundTag compoundTag)
                        tag.merge(compoundTag);
                });
        Codec.BOOL.fieldOf("flag").codec().encodeStart(ops, flag)
                .resultOrPartial(SantaConstants.LOGGER::error)
                .ifPresent(encoded -> {
                    if(encoded instanceof CompoundTag compoundTag)
                        tag.merge(compoundTag);
                });
        return tag;
    }
}
