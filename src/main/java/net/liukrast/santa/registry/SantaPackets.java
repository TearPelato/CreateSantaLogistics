package net.liukrast.santa.registry;

import net.liukrast.santa.SantaLogisticsConstants;
import net.liukrast.santa.network.protocol.game.SantaDockConfirmInfoPacket;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class SantaPackets {
    private SantaPackets() {}

    public static void register(RegisterPayloadHandlersEvent event) {
        final var registry = event.registrar(SantaLogisticsConstants.MOD_ID).versioned(SantaLogisticsConstants.PROTOCOL_VERSION);
        registry.playToServer(SantaDockConfirmInfoPacket.PACKET_TYPE, SantaDockConfirmInfoPacket.CODEC, SantaDockConfirmInfoPacket::handle);
    }

}
