package net.liukrast.santa.datagen;

import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.SantaLang;
import net.liukrast.santa.registry.SantaBlocks;
import net.liukrast.santa.registry.SantaItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class SantaLanguageProvider extends LanguageProvider {
    public SantaLanguageProvider(PackOutput output) {
        super(output, SantaConstants.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {

        /* AUTO-GENERATED */
        SantaConstants.getElementEntries(BuiltInRegistries.ITEM)
                .filter(e -> e.getValue() != SantaBlocks.CRYOLITE_BLOCK.get().asItem())
                .forEach(e -> add(e.getValue().getDescriptionId(), autoName(e.getKey())));
        SantaConstants.getElementEntries(BuiltInRegistries.ENTITY_TYPE)
                .forEach(e -> add(e.getValue(), autoName(e.getKey())));
        /* NON-AUTO REGISTRY ENTRIES */
        addReplaced("fluid.%s.cryolite", "Cryolite");
        addReplaced("fluid.%s.molten_sugar", "Molten Sugar");
        add(SantaBlocks.CRYOLITE_BLOCK.get(), "Block of Cryolite");
        addReplaced("itemGroup.%s", "Create: Santa Logistics");

        /* ITEM DESCRIPTIONS */
        add(SantaBlocks.PRIME_CRYOLITE_BLOCK.get(), "Creative-only infinite cryolite generator");
        add(SantaItems.FROSTBURN_CORE.get(), "The power of the sun, in the palm of my hand...");
        add(SantaBlocks.SCHEDULE_CLOCK.get(), "Displays santa claus's schedule time");

        /* CONTAINERS */
        addReplaced("container.%s.robo_elf.trust_gain", "How much trust you will gain");
        addReplaced("container.%s.robo_elf.energy_usage", "How much energy the elf will consume");
        addReplaced("container.%s.robo_elf.process_time", "How many seconds it will take to craft this item");
        addReplaced("container.%s.robo_elf.trust_level", "Your current trust level");
        addReplaced("container.%s.santa", "Santa's Sleigh");

        /* GOGGLE TOOLTIPS */
        addPrefixed("gui.santa_claus.info_header", "Santa Claus Information:");
        addPrefixed("gui.santa_claus.satisfaction", "Satisfaction level:");
        addPrefixed("gui.santa_claus.satisfaction_a", "Reward A:");
        addPrefixed("gui.santa_claus.satisfaction_b", "Reward B:");

        addPrefixed("tooltip.overclock", "Overclock Amount:");
        addPrefixed("tooltip.temperature", "Temperature:");
        addPrefixed("tooltip.temperature_per_tick", "per tick");

        addPrefixed("gui.robo_elf.info_header", "Robo Elf Information:");
        addPrefixed("gui.robo_elf.title", "Activity Stress:");
        addPrefixed("gui.robo_elf.capacity", "Remaining Charge:");
        addPrefixed("gui.robo_elf.oxidation", "Oxidation level:");
        addPrefixed("gui.robo_elf.oxidation.clean", "Clean");
        addPrefixed("gui.robo_elf.oxidation.exposed", "Exposed");
        addPrefixed("gui.robo_elf.oxidation.weathered", "Weathered");
        addPrefixed("gui.robo_elf.oxidation.oxidized", "Oxidized");

        addPrefixed("gui.santa_dock.info_header", "Santa Dock Information:");
        addPrefixed("gui.santa_dock.status", "Status:");
        addPrefixed("gui.santa_dock.status.connected", "Connected");
        addPrefixed("gui.santa_dock.status.idle", "Idle");
        addPrefixed("gui.santa_dock.status.error", "Error");
        addPrefixed("gui.santa_dock.status.error.info_header", "Reason:");
        addPrefixed("gui.santa_dock.status.error.night", "Santa is already flying");
        addPrefixed("gui.santa_dock.status.error.already_taken", "Address taken");
        addPrefixed("gui.santa_dock.status.error.out_of_bound", "Max docks per world reached");
        addPrefixed("gui.santa_dock.status.error.wrong_dimension", "Dock outside of the overworld");
        addPrefixed("gui.santa_dock.status.connected.info_header", "Santa will pass by this dock tonight");

        addPrefixed("gui.schedule_clock.info_header", "Schedule Clock Information:");
        addPrefixed("gui.schedule_clock.current_time", "Right now it's %s");
        addPrefixed("gui.schedule_clock.schedule", "Schedule:");
        addPrefixed("gui.schedule_clock.wake_up_time", "Wake up at %s");
        addPrefixed("gui.schedule_clock.leave_time", "Leave at %s");
        addPrefixed("gui.schedule_clock.get_back_time", "Get back at %s");


        /* COMMANDS */
        add("commands.santa.get_target", "%s's trust is %s");
        add("commands.santa.set_target", "%s's trust is set to %s");
        add("commands.santa.empty", "§cNo santa dock registered");
        add("commands.santa.title", "Santa docks:");
        add("commands.santa.try_spawn_santa_base.success", "Successfully placed Santa's base at %s (%s blocks away)");
        add("commands.santa.try_spawn_santa_base.already_placed", "Santa's base was already located at %s (%s blocks away)");
        add("commands.santa.try_spawn_santa_base.failed", "Unable to automatically generate santa's base. Please use the manual command");

        add("chat.santa_dock.available", "Santa dock is connected and active");
        add("chat.santa_dock.to_be_removed", "Santa dock will be deleted at the end of the night");
    }

    private void addReplaced(String key, String value) {
        add(String.format(key, SantaConstants.MOD_ID), value);
    }

    private void addPrefixed(String key, String value) {
        addReplaced("%s."+key, value);
    }

    public String autoName(String id) {
        String[] words = id.split("_");
        for(int i = 0; i < words.length; i++) {
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
        }
        return String.join(" ", words);
    }
}
