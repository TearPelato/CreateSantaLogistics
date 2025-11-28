package net.liukrast.santa.world.level.block;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import net.liukrast.santa.SantaLogisticsConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.HashMap;
import java.util.Map;

@NonnullDefault
public class SantaDocks extends SavedData {
    private static final Codec<Map<String, BlockPos>> DOCKS_CODEC = Codec.unboundedMap(Codec.STRING, BlockPos.CODEC);
    private final BiMap<String, BlockPos> docks;

    public SantaDocks() {
        this(HashBiMap.create());
    }

    private SantaDocks(BiMap<String, BlockPos> docks) {
        this.docks = docks;
    }

    public boolean addDock(String name, BlockPos pos) {
        if(docks.containsKey(name)) return false;
        docks.put(name, pos);
        setDirty();
        return true;
    }

    public void removeDock(BlockPos pos) {
        docks.inverse().remove(pos);
        setDirty();
    }

    @Nullable
    public String get(BlockPos pos) {
        return docks.inverse().get(pos);
    }

    public static SantaDocks load(CompoundTag tag, HolderLookup.Provider registries) {
        var ops = registries.createSerializationContext(NbtOps.INSTANCE);
        Map<String, BlockPos> map = DOCKS_CODEC
                .parse(ops, tag)
                .resultOrPartial(SantaLogisticsConstants.LOGGER::error)
                .orElseGet(HashMap::new);
        return new SantaDocks(HashBiMap.create(map));
    }

    @Nullable
    public static String getDock(ServerLevel level, BlockPos pos) {
        return get(level).get(pos);
    }

    public static boolean addDock(ServerLevel level, String name, BlockPos pos) {
        return get(level).addDock(name, pos);
    }

    public static void removeDock(ServerLevel level, BlockPos pos) {
        get(level).removeDock(pos);
    }

    public static SantaDocks get(ServerLevel level) {
        return level
                .getDataStorage()
                .computeIfAbsent(new SavedData.Factory<>(SantaDocks::new, SantaDocks::load), SantaLogisticsConstants.MOD_ID + "_santa_docks");
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        var ops = registries.createSerializationContext(NbtOps.INSTANCE);
        DOCKS_CODEC.encodeStart(ops, docks)
                .resultOrPartial(SantaLogisticsConstants.LOGGER::error)
                .ifPresent(encoded -> {
                    if(encoded instanceof CompoundTag compoundTag)
                        tag.merge(compoundTag);
                });
        return tag;
    }

    public Component component() {
        if(docks.isEmpty()) return Component.translatable("commands.santa.empty");
        MutableComponent root = Component.translatable("commands.santa.title").append("\n");
        int size = docks.size();
        int i = 0;
        for(var entry : docks.entrySet()) {
            if(entry.getKey() == null) continue;
            if(entry.getValue() == null) continue;
            var pos = entry.getValue();
            Component line = Component.literal("• ").withStyle(ChatFormatting.WHITE)
                    .append(Component.literal(entry.getKey()).withStyle(ChatFormatting.RED))
                    .append(Component.literal("  →  ")).withStyle(ChatFormatting.GOLD)
                    .append(Component.literal(
                            "(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")"
                    ).withStyle(ChatFormatting.WHITE));
            root.append(line);
            if(++i < size) root.append("\n");
        }
        return root;
    }

    @Override
    public String toString() {
        return "SantaDocks{docks=" + docks + '}';
    }
}
