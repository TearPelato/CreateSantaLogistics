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
    public static final ModConfigSpec.IntValue ELF_UNSTRESS_COOLDOWN = BUILDER
            .comment("Defines how many ticks it takes for an elf to lower his stress by 1")
            .defineInRange("elfUnstressCooldown", 400, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue TYPE_A_TRUST = BUILDER
            .comment("How much trust is required to be able to give santa all type A items")
            .defineInRange("typeATrust", 1000, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue TYPE_B_TRUST = BUILDER
            .comment("How much trust is required to be able to give santa all type A items")
            .defineInRange("typeBTrust", 10000, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
