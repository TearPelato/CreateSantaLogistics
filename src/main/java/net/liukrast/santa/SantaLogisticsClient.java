package net.liukrast.santa;

import net.liukrast.santa.client.gui.screens.SantaDockScreen;
import net.liukrast.santa.registry.SantaEntityTypes;
import net.liukrast.santa.registry.SantaMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@OnlyIn(Dist.CLIENT)
@Mod(value = SantaLogisticsConstants.MOD_ID, dist = Dist.CLIENT)
public class SantaLogisticsClient {
    public SantaLogisticsClient(IEventBus eventBus) {
        eventBus.register(this);
        eventBus.addListener(SantaEntityTypes::registerLayerDefinitions);
        eventBus.addListener(SantaEntityTypes::registerRenderers);
    }

    @SubscribeEvent
    public void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(SantaMenuTypes.SANTA_DOCK.get(), SantaDockScreen::new);
    }


}
