package net.liukrast.santa.registry;

import net.liukrast.santa.SantaLogisticsConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SantaCreativeModeTabs {
    private SantaCreativeModeTabs() {}
    private static final DeferredRegister<CreativeModeTab> REGISTER = SantaLogisticsConstants.createDeferred(BuiltInRegistries.CREATIVE_MODE_TAB);

    static {
        REGISTER.register("main_tab", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.santa_logistics"))
                .icon(SantaBlocks.SANTA_DOCK.asItem()::getDefaultInstance)
                .displayItems((pars, out) -> out.accept(SantaBlocks.SANTA_DOCK))
                .build());
    }

    public static void init(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
