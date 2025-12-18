package net.liukrast.santa.registry;

import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.network.protocol.game.RoboElfCraftPacket;
import net.liukrast.santa.network.protocol.game.SantaDockConfirmInfoPacket;
import net.liukrast.santa.network.protocol.game.SantaPositionUpdatePacket;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class SantaPackets {
    private SantaPackets() {}

    public static void register(RegisterPayloadHandlersEvent event) {
        final var registry = event.registrar(SantaConstants.MOD_ID).versioned(SantaConstants.PROTOCOL_VERSION);
        registry.playToServer(SantaDockConfirmInfoPacket.PACKET_TYPE, SantaDockConfirmInfoPacket.STREAM_CODEC, SantaDockConfirmInfoPacket::handle);
        registry.playToServer(RoboElfCraftPacket.PACKET_TYPE, RoboElfCraftPacket.STREAM_CODEC, RoboElfCraftPacket::handle);
        registry.playToClient(SantaPositionUpdatePacket.PACKET_TYPE, SantaPositionUpdatePacket.STREAM_CODEC, SantaPositionUpdatePacket::handle);
    }

}
