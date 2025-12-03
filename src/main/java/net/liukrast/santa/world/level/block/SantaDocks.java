package net.liukrast.santa.world.level.block;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import net.liukrast.santa.SantaConfig;
import net.liukrast.santa.SantaConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NonnullDefault
public class SantaDocks extends SavedData {
    private static final Codec<Map<String, BlockPos>> DOCKS_CODEC = Codec.unboundedMap(Codec.STRING, BlockPos.CODEC);
    private final BiMap<String, BlockPos> docks;
    private final List<BlockPos> queuedForRemoval;

    public SantaDocks() {
        this(HashBiMap.create(), new ArrayList<>());
    }

    private SantaDocks(BiMap<String, BlockPos> docks, List<BlockPos> queuedForRemoval) {
        this.docks = docks;
        this.queuedForRemoval = queuedForRemoval;
    }

    private AddStatus addDock(String name, BlockPos pos) {
        if(pos.equals(docks.get(name))) return AddStatus.SUCCESS;
        if(docks.containsKey(name)) return AddStatus.ALREADY_TAKEN;
        if(docks.size() >= SantaConfig.MAX_DOCK_AMOUNT.get()) return AddStatus.OUT_OF_BOUND;
        var str = docks.inverse().get(pos);
        docks.remove(str);
        docks.put(name, pos);
        setDirty();
        return AddStatus.SUCCESS;
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
                .fieldOf("docks").codec()
                .parse(ops, tag)
                .resultOrPartial(SantaConstants.LOGGER::error)
                .orElseGet(HashMap::new);
        List<BlockPos> queuedForRemoval = BlockPos.CODEC.listOf()
                .fieldOf("queuedForRemoval").codec()
                .parse(ops, tag)
                .resultOrPartial()
                .orElseGet(ArrayList::new);
        return new SantaDocks(HashBiMap.create(map), new ArrayList<>(queuedForRemoval));
    }

    @Nullable
    public static String getDock(ServerLevel level, BlockPos pos) {
        return get(level).get(pos);
    }

    public static AddStatus addDock(ServerLevel level, String name, BlockPos pos) {
        long time = level.getDayTime()%24000;
        if(time >= SantaConstants.NIGHT_START && time <= SantaConstants.NIGHT_END) return AddStatus.NIGHT;
        return get(level).addDock(name, pos);
    }

    public static void removeDock(ServerLevel level, BlockPos pos) {
        long dayTime = level.getDayTime()%24000;
        var docks = get(level);
        if(dayTime > SantaConstants.NIGHT_START && dayTime < SantaConstants.NIGHT_END) {
            if(!docks.queuedForRemoval.contains(pos)) {
                docks.queuedForRemoval.add(pos);
                docks.setDirty();
            }
        } else docks.removeDock(pos);
    }

    public static SantaDocks get(ServerLevel level) {
        return level
                .getDataStorage()
                .computeIfAbsent(new SavedData.Factory<>(SantaDocks::new, SantaDocks::load), SantaConstants.MOD_ID + "_santa_docks");
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        var ops = registries.createSerializationContext(NbtOps.INSTANCE);
        DOCKS_CODEC.fieldOf("docks").codec().encodeStart(ops, docks)
                .resultOrPartial(SantaConstants.LOGGER::error)
                .ifPresent(encoded -> {
                    if(encoded instanceof CompoundTag compoundTag)
                        tag.merge(compoundTag);
                });
        BlockPos.CODEC.listOf().fieldOf("queuedForRemoval").codec().encodeStart(ops, queuedForRemoval)
                .resultOrPartial(SantaConstants.LOGGER::error)
                .ifPresent(encoded -> {
                    if(encoded instanceof CompoundTag ct)
                        tag.merge(ct);
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
            boolean flag = queuedForRemoval.contains(pos);
            Component line = Component.literal("• ").withStyle(ChatFormatting.WHITE)
                    .append(Component.literal(entry.getKey())
                            .withStyle(flag?ChatFormatting.RED:ChatFormatting.GREEN).withStyle(style -> style
                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.santa_dock." + (flag ? "to_be_removed" : "available"))))))
                    .append(Component.literal("  →  ")).withStyle(ChatFormatting.GOLD)
                    .append(Component.literal(
                            "[" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]"
                    ).withStyle(ChatFormatting.GREEN).withStyle(style -> style
                            .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/tp @s %s %s %s", pos.getX(), pos.getY(), pos.getZ())))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip")))));
            root.append(line);
            if(++i < size) root.append("\n");
        }
        return root;
    }

    @Override
    public String toString() {
        return "SantaDocks{docks=" + docks + '}';
    }

    public int size() {
        return docks.size();
    }

    public Map<BlockPos, String> blockPosMap() {
        return docks.inverse();
    }

    public void updateQueuedDocks() {
        for(BlockPos pos : queuedForRemoval) {
            docks.inverse().remove(pos);
        }
        queuedForRemoval.clear();
        setDirty();
    }

    public enum AddStatus {
        SUCCESS, ALREADY_TAKEN, OUT_OF_BOUND, NIGHT, WRONG_DIMENSION;

        public boolean isSuccessful() {
            return this == SUCCESS;
        }
    }
}
