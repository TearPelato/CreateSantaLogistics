package net.liukrast.santa.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.logistics.packagerLink.WiFiEffectPacket;
import net.liukrast.santa.world.level.block.entity.SantaDockBlockEntity;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WiFiEffectPacket.class)
public class WifiEffectPacketMixin {
    @Inject(method = "handle", at = @At("TAIL"))
    private void handle(LocalPlayer player, CallbackInfo ci, @Local(name = "blockEntity") BlockEntity blockEntity) {
        if(blockEntity instanceof SantaDockBlockEntity plbe)
            plbe.playEffect();
    }
}
