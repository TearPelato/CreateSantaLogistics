package net.liukrast.santa.network.protocol.game;

import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.client.gui.screens.RoboElfScreen;
import net.liukrast.santa.world.entity.RoboElf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record RoboElfCraftPacket(int entityId, int craftIndex, int amount) implements CustomPacketPayload {
    public static final Type<RoboElfCraftPacket> PACKET_TYPE = new Type<>(SantaConstants.id("robo_elf_craft"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RoboElfCraftPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, RoboElfCraftPacket::entityId,
            ByteBufCodecs.INT, RoboElfCraftPacket::craftIndex,
            ByteBufCodecs.INT, RoboElfCraftPacket::amount,
            RoboElfCraftPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }

    public void handle(IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
        ServerLevel level = player.serverLevel();
        Entity entity = level.getEntity(entityId);
        if(entity == null || !entity.isAlive() || !(entity instanceof RoboElf roboElf)) return;
        roboElf.enqueueWork(craftIndex, amount, player);
    }
}
