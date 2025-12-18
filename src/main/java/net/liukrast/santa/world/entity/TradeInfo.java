package net.liukrast.santa.world.entity;

import com.mojang.serialization.Codec;
import net.createmod.catnip.codecs.stream.CatnipLargerStreamCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public class TradeInfo {
    private final ItemStack[] ingredients;
    private final ItemStack result;
    private final int trustGain;
    private final int energy;
    private final int processTime;

    public TradeInfo(ItemStack a, ItemStack result, int trustGain, int energy, int processTime) {
        this(a, ItemStack.EMPTY, result, trustGain, energy, processTime);
    }

    public TradeInfo(ItemStack a, ItemStack b, ItemStack result, int trustGain, int energy, int processTime) {
        this(a, b, ItemStack.EMPTY, result, trustGain, energy, processTime);
    }

    public TradeInfo(ItemStack a, ItemStack b, ItemStack c, ItemStack result, int trustGain, int energy, int processTime) {
        ingredients = new ItemStack[]{a,b,c};
        this.result = result;
        this.trustGain = trustGain;
        this.energy = energy;
        this.processTime = processTime;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, TradeInfo> STREAM_CODEC = CatnipLargerStreamCodecs.composite(
            ItemStack.STREAM_CODEC, i -> i.ingredients[0],
            ItemStack.OPTIONAL_STREAM_CODEC, i -> i.ingredients[1],
            ItemStack.OPTIONAL_STREAM_CODEC, i -> i.ingredients[2],
            ItemStack.STREAM_CODEC, TradeInfo::getResult,
            ByteBufCodecs.INT, TradeInfo::getTrustGain,
            ByteBufCodecs.INT, TradeInfo::getEnergy,
            ByteBufCodecs.INT, TradeInfo::getProcessTime,
            TradeInfo::new
    );

    public ItemStack[] getIngredients() {
        return ingredients;
    }

    public int getTrustGain() {
        return trustGain;
    }

    public int getEnergy() {
        return energy;
    }

    public ItemStack getResult() {
        return result;
    }

    public int getProcessTime() {
        return processTime;
    }
}
