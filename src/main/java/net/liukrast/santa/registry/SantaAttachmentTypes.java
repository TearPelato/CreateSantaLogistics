package net.liukrast.santa.registry;

import com.mojang.serialization.Codec;
import net.liukrast.santa.SantaConstants;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class SantaAttachmentTypes {
    private SantaAttachmentTypes() {}

    private static final DeferredRegister<AttachmentType<?>> REGISTER = SantaConstants.createDeferred(NeoForgeRegistries.ATTACHMENT_TYPES);

    public static final Supplier<AttachmentType<Integer>> TRUST = REGISTER.register(
            "trust", () -> AttachmentType.builder(() -> 0).serialize(Codec.intRange(-1_000_000_000, 1_000_000_000)).sync(ByteBufCodecs.INT).build()
    );

    public static void init(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    public static void setTrust(Player player, int trust) {
        player.setData(TRUST, Mth.clamp(trust, -1_000_000_000, 1_000_000_000));
    }

    public static void trust(Player player, int amount) {
        setTrust(player, player.getData(TRUST) + amount);
    }
}
