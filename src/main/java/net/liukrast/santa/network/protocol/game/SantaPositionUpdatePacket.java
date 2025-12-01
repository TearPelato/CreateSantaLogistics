package net.liukrast.santa.network.protocol.game;

import net.liukrast.santa.Node;
import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.client.renderer.entity.SleighRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record SantaPositionUpdatePacket(BlockPos origin, List<BlockPos> docks) implements CustomPacketPayload {
    public static final Type<SantaPositionUpdatePacket> PACKET_TYPE = new Type<>(SantaConstants.id("santa_position_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SantaPositionUpdatePacket> CODEC = StreamCodec.of(
            SantaPositionUpdatePacket::write, SantaPositionUpdatePacket::new
    );

    private SantaPositionUpdatePacket(RegistryFriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readList(BlockPos.STREAM_CODEC));
    }

    private static void write(RegistryFriendlyByteBuf buf, SantaPositionUpdatePacket packet) {
        buf.writeBlockPos(packet.origin);
        buf.writeCollection(packet.docks, BlockPos.STREAM_CODEC);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }

    public void handle(IPayloadContext context) {
        SleighRenderer.ORIGIN = this.origin;
        SleighRenderer.DOCKS = new ArrayList<>();
        SleighRenderer.DOCKS.add(new Node(
                origin.getX(), origin.getZ()+SantaConstants.EXIT_LENGTH, 0, 10
        ));
        for(int i = 0; i < docks.size(); i++) {
            BlockPos curr = docks.get(i);
            int end = docks.size()-1;
            BlockPos next = i == end ? origin : docks.get(i+1);
            float medX = (curr.getX()+next.getX())/2f - curr.getX();
            float medZ = (curr.getZ()+next.getZ())/2f - curr.getZ();
            SleighRenderer.DOCKS.add(new Node(
                    curr.getX() + 0.5f, curr.getZ() + 0.5f,
                    -medX, medZ
            ));
        }
        SleighRenderer.DOCKS.add(new Node(
                origin.getX(), origin.getZ()-SantaConstants.EXIT_LENGTH, 0, 10
        ));
    }
}
