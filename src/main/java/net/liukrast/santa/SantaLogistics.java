package net.liukrast.santa;

import net.liukrast.santa.registry.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(SantaLogisticsConstants.MOD_ID)
public class SantaLogistics {

    public SantaLogistics(IEventBus eventBus) {
        SantaBlockEntityTypes.init(eventBus);
        SantaMenuTypes.init(eventBus);
        SantaCreativeModeTabs.init(eventBus);
        SantaBlocks.init(eventBus);
        SantaEntityTypes.init(eventBus);
        eventBus.addListener(SantaEntityTypes::entityAttributeCreation);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        eventBus.register(this);
    }

    public void registerCommands(RegisterCommandsEvent event) {
        SantaCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void registerPayloadHandlersEvent(RegisterPayloadHandlersEvent event) {
        SantaPackets.register(event);
    }
}
