package net.liukrast.santa.network.protocol.game;

import net.liukrast.santa.SantaLogisticsConstants;
import net.liukrast.santa.registry.SantaBlocks;
import net.liukrast.santa.world.level.block.SantaDockBlock;
import net.liukrast.santa.world.level.block.SantaDocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SantaDockConfirmInfoPacket(String address, BlockPos pos) implements CustomPacketPayload {
    public static final Type<SantaDockConfirmInfoPacket> PACKET_TYPE = new Type<>(SantaLogisticsConstants.id("santa_dock_confirm_info"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SantaDockConfirmInfoPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SantaDockConfirmInfoPacket::address,
            BlockPos.STREAM_CODEC, SantaDockConfirmInfoPacket::pos,
            SantaDockConfirmInfoPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }

    public void handle(IPayloadContext context) {
        var player = context.player();
        if(address.isEmpty()) {
            SantaDocks.removeDock((ServerLevel) player.level(), pos);
            player.level().setBlock(pos, SantaBlocks.SANTA_DOCK.get().defaultBlockState().setValue(SantaDockBlock.STATE, SantaDockBlock.State.IDLE), 3);
            return;
        }
        boolean result = SantaDocks.addDock((ServerLevel) player.level(), address, pos);
        if(result) {
            //TODO: Display particles
        }
        player.level().setBlock(pos, SantaBlocks.SANTA_DOCK.get().defaultBlockState().setValue(SantaDockBlock.STATE, result ? SantaDockBlock.State.CONNECTED : SantaDockBlock.State.ADDRESS_TAKEN), 3);

    }
}
