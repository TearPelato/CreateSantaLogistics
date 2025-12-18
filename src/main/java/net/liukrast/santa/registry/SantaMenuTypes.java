package net.liukrast.santa.registry;

import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.client.gui.screens.RoboElfScreen;
import net.liukrast.santa.client.gui.screens.SantaDockScreen;
import net.liukrast.santa.client.gui.screens.SantaScreen;
import net.liukrast.santa.world.inventory.RoboElfMenu;
import net.liukrast.santa.world.inventory.SantaDockMenu;
import net.liukrast.santa.world.inventory.SantaMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SantaMenuTypes {
    private SantaMenuTypes() {}

    private static final DeferredRegister<MenuType<?>> REGISTER = SantaConstants.createDeferred(BuiltInRegistries.MENU);

    public static final DeferredHolder<MenuType<?>, MenuType<SantaDockMenu>> SANTA_DOCK = REGISTER.register("santa_dock", () -> IMenuTypeExtension.create(SantaDockMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<SantaMenu>> SANTA_MENU = REGISTER.register("santa_menu", () -> IMenuTypeExtension.create(SantaMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<RoboElfMenu>> ROBO_ELF_MENU = REGISTER.register("robo_elf_menu", () -> IMenuTypeExtension.create(RoboElfMenu::new));

    public static void init(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(SantaMenuTypes.SANTA_DOCK.get(), SantaDockScreen::new);
        event.register(SantaMenuTypes.SANTA_MENU.get(), SantaScreen::new);
        event.register(SantaMenuTypes.ROBO_ELF_MENU.get(), RoboElfScreen::new);
    }
}
