package net.liukrast.santa.registry;

import net.liukrast.santa.SantaLogisticsConstants;
import net.liukrast.santa.world.inventory.SantaDockMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SantaMenuTypes {
    private SantaMenuTypes() {}

    private static final DeferredRegister<MenuType<?>> REGISTER = SantaLogisticsConstants.createDeferred(BuiltInRegistries.MENU);

    public static final DeferredHolder<MenuType<?>, MenuType<SantaDockMenu>> SANTA_DOCK = REGISTER.register("santa_dock", () -> IMenuTypeExtension.create(SantaDockMenu::new));

    public static void init(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
