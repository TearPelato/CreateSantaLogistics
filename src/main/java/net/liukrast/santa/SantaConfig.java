package net.liukrast.santa;

import net.neoforged.neoforge.common.ModConfigSpec;

public class SantaConfig {
    private SantaConfig() {}

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue MAX_DOCK_AMOUNT = BUILDER
            .comment("Defines the maximum amount of docks that can be placed direction the world")
            .defineInRange("maxDockAmount", 128, 0, 10000);

    public static final ModConfigSpec.IntValue MAX_PACKAGES_PER_DELIVERY = BUILDER
            .comment("Defines how many packages santa can extract/deliver when passing by a dock")
            .defineInRange("maxPackagesPerDelivery", 4, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue ELF_RECHARGE_PERCENTAGE = BUILDER
            .comment("Defines at what percentage of charge an elf starts looking for potential charge stations")
            .defineInRange("elfRechargePercentage", 50, 0, 100);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
