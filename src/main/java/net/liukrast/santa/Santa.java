package net.liukrast.santa;

import com.simibubi.create.content.logistics.box.PackageItem;
import net.liukrast.santa.datagen.SantaBlockLootSubProvider;
import net.liukrast.santa.datagen.SantaItemModelProvider;
import net.liukrast.santa.datagen.SantaLanguageProvider;
import net.liukrast.santa.datagen.tags.SantaBlockTagsProvider;
import net.liukrast.santa.network.protocol.game.SantaPositionUpdatePacket;
import net.liukrast.santa.registry.*;
import net.liukrast.santa.world.SantaContainer;
import net.liukrast.santa.world.level.block.SantaDocks;
import net.liukrast.santa.world.level.block.entity.SantaDockBlockEntity;
import net.liukrast.santa.world.level.levelgen.SantaBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Mod(SantaConstants.MOD_ID)
public class Santa {

    public Santa(IEventBus eventBus, ModContainer container) {
        SantaBlockEntityTypes.init(eventBus);
        SantaMenuTypes.init(eventBus);
        SantaCreativeModeTabs.init(eventBus);
        SantaBlocks.init(eventBus);
        SantaEntityTypes.init(eventBus);
        SantaItems.init(eventBus);
        eventBus.addListener(SantaEntityTypes::entityAttributeCreation);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        NeoForge.EVENT_BUS.addListener(this::levelTickPost);
        NeoForge.EVENT_BUS.addListener(this::loadPlayer);
        NeoForge.EVENT_BUS.addListener(this::loadLevel);
        eventBus.register(this);
        container.registerConfig(ModConfig.Type.COMMON, SantaConfig.SPEC);
    }

    public void registerCommands(RegisterCommandsEvent event) {
        SantaCommands.register(event.getDispatcher());
    }

    private static boolean firedNightStart = false;
    private static boolean firedNightEnd = false;

    public void levelTickPost(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide || event.getLevel().dimension() != ServerLevel.OVERWORLD) return;

        ServerLevel level = (ServerLevel) event.getLevel();
        SantaDocks docks = SantaDocks.get(level);
        Map<BlockPos, String> map = docks.blockPosMap();

        BlockPos origin = SantaBase.getPos(level);
        if(origin == null) return;
        long dayTimeRaw = level.getDayTime();
        int time = (int) (dayTimeRaw % 24000);
        if(time > SantaConstants.NIGHT_START-SantaConstants.LEAVE_DURATION && time < SantaConstants.NIGHT_END+SantaConstants.LEAVE_DURATION && !firedNightStart) {
            firedNightStart = true;
            List<Map.Entry<BlockPos, String>> ordered = map.entrySet().stream()
                    .sorted(Comparator.comparingDouble(e -> e.getKey().distSqr(origin)))
                    .toList();
            PacketDistributor.sendToAllPlayers(new SantaPositionUpdatePacket(origin, ordered.stream().map(Map.Entry::getKey).toList()));
        } else firedNightStart = false;
        if(time > SantaConstants.NIGHT_END+SantaConstants.LEAVE_DURATION || time < SantaConstants.NIGHT_START-SantaConstants.LEAVE_DURATION && !firedNightEnd) {
            firedNightEnd = true;
            SantaDocks.get(level).updateQueuedDocks();
        } else firedNightEnd = false;
        if (time < SantaConstants.NIGHT_START || time > SantaConstants.NIGHT_END) return;
        int size = map.size();
        if (size == 0) return;
        int step = (SantaConstants.NIGHT_END - SantaConstants.NIGHT_START) / (size + 1);
        int t = time - SantaConstants.NIGHT_START;
        if (t <= 0 || t % step != 0) return;
        int k = (t / step) - 1;
        List<Map.Entry<BlockPos, String>> ordered = map.entrySet().stream()
                .sorted(Comparator.comparingDouble(e -> e.getKey().distSqr(origin)))
                .toList();
        if (k < 0 || k >= ordered.size()) return;
        Map.Entry<BlockPos, String> target = ordered.get(k);
        BlockPos pos = target.getKey();
        String address = target.getValue();
        SantaDockBlockEntity blockEntity = (SantaDockBlockEntity) level.getBlockEntity(pos);
        if (blockEntity == null) return;
        var container = SantaContainer.get(level);
        int jNotEmpty = 0;
        int itemsMoved = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            if (itemsMoved == SantaConfig.MAX_PACKAGES_PER_DELIVERY.get()) break;

            ItemStack stack = container.getItem(i);
            if (!PackageItem.getAddress(stack).equals(address)) continue;

            container.removeItemNoUpdate(i);

            for (int j = jNotEmpty; j < blockEntity.getContainerSize(); j++) {
                if (!blockEntity.getItem(j).isEmpty()) {
                    jNotEmpty = j;
                    continue;
                }
                blockEntity.setItem(j, stack.copy());
                itemsMoved++;
                break;
            }
        }
        container.setChanged();
        itemsMoved = 0;
        jNotEmpty = 0;
        for (int i = 0; i < blockEntity.getContainerSize(); i++) {
            if (itemsMoved == SantaConfig.MAX_PACKAGES_PER_DELIVERY.get()) break;

            ItemStack stack = blockEntity.getItem(i);
            if (PackageItem.getAddress(stack).equals(address)) continue;

            blockEntity.removeItemNoUpdate(i);

            for (int j = jNotEmpty; j < container.getContainerSize(); j++) {
                if (!container.getItem(j).isEmpty()) {
                    jNotEmpty = j;
                    continue;
                }
                container.setItem(j, stack.copy());
                itemsMoved++;
                break;
            }
        }
        blockEntity.setChanged();
    }

    public void loadPlayer(PlayerEvent.PlayerLoggedInEvent event) {
        ServerLevel level = (ServerLevel) event.getEntity().level();
        SantaDocks docks = SantaDocks.get(level);
        Map<BlockPos, String> map = docks.blockPosMap();

        BlockPos origin = SantaBase.getPos(level);
        if(origin == null) return;
        List<Map.Entry<BlockPos, String>> ordered = map.entrySet().stream()
                .sorted(Comparator.comparingDouble(e -> e.getKey().distSqr(origin)))
                .toList();
        PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new SantaPositionUpdatePacket(origin, ordered.stream().map(Map.Entry::getKey).toList()));
        //TODO: Send player santa base error message
    }

    public void loadLevel(LevelEvent.Load event) {
        if(event.getLevel().isClientSide()) return;
        ServerLevel level = (ServerLevel) event.getLevel();
        if(!level.getServer().getWorldData().worldGenOptions().generateStructures()) return;
        if(level.dimension() != ServerLevel.OVERWORLD) return;
        if(SantaBase.getPos(level) != null) return;
        if(SantaBase.getFlag(level)) return;
        SantaBase.generate(level, null);
    }

    @SubscribeEvent
    public void registerPayloadHandlersEvent(RegisterPayloadHandlersEvent event) {
        SantaPackets.register(event);
    }

    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(event.includeClient(), new SantaLanguageProvider(packOutput));
        generator.addProvider(event.includeClient(), new SantaItemModelProvider(packOutput, helper));
        var blockTagProvider = new SantaBlockTagsProvider(packOutput, lookupProvider, helper);
        generator.addProvider(event.includeServer(), blockTagProvider);
        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(
                        new LootTableProvider.SubProviderEntry(SantaBlockLootSubProvider::new, LootContextParamSets.BLOCK)
                ), lookupProvider));
    }
}
