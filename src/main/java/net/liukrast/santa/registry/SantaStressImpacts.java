package net.liukrast.santa.registry;

import com.simibubi.create.api.stress.BlockStressValues;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

public class SantaStressImpacts {
    private SantaStressImpacts() {}

    public static <T extends Block> Consumer<T> init(DoubleSupplier sup) {
        return block -> BlockStressValues.IMPACTS.register(block, sup);
    }
}
