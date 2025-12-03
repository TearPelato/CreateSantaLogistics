package net.liukrast.santa.network.protocol.game;

import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.packagerLink.WiFiEffectPacket;
import com.simibubi.create.content.logistics.packagerLink.WiFiParticle;
import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.registry.SantaBlocks;
import net.liukrast.santa.world.level.block.SantaDockBlock;
import net.liukrast.santa.world.level.block.SantaDocks;
import net.liukrast.santa.world.level.block.entity.SantaDockBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SantaDockConfirmInfoPacket(String address, BlockPos pos) implements CustomPacketPayload {
    public static final Type<SantaDockConfirmInfoPacket> PACKET_TYPE = new Type<>(SantaConstants.id("santa_dock_confirm_info"));
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
        ServerLevel level = (ServerLevel) player.level();
        if(!(level.getBlockEntity(pos) instanceof SantaDockBlockEntity be)) return;
        if(level.dimension() != ServerLevel.OVERWORLD) {
            level.setBlock(pos, SantaBlocks.SANTA_DOCK.get().defaultBlockState().setValue(SantaDockBlock.STATE, SantaDockBlock.State.ERROR), 3);
            be.setStatus(SantaDocks.AddStatus.WRONG_DIMENSION);
            return;
        }
        if(address.isEmpty()) {
            SantaDocks.removeDock(level, pos);
            level.setBlock(pos, SantaBlocks.SANTA_DOCK.get().defaultBlockState().setValue(SantaDockBlock.STATE, SantaDockBlock.State.IDLE), 3);
            return;
        }
        var result = SantaDocks.addDock(level, address, pos);
        if(result.isSuccessful()) context.reply(new WiFiEffectPacket(pos));
        level.setBlock(pos, SantaBlocks.SANTA_DOCK.get().defaultBlockState().setValue(SantaDockBlock.STATE, result.isSuccessful() ? SantaDockBlock.State.CONNECTED : SantaDockBlock.State.ERROR), 3);
        be.setStatus(result);
    }
}
