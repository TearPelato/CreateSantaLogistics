package net.liukrast.santa.datagen;

import net.liukrast.santa.SantaConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class SantaLanguageProvider extends LanguageProvider {
    public SantaLanguageProvider(PackOutput output) {
        super(output, SantaConstants.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        SantaConstants.getElementEntries(BuiltInRegistries.ITEM)
                .forEach(e -> add(e.getValue().getDescriptionId(), autoName(e.getKey())));
        SantaConstants.getElementEntries(BuiltInRegistries.ENTITY_TYPE)
                .forEach(e -> add(e.getValue(), autoName(e.getKey())));

        add("itemGroup.santa_logistics", "Create: Santa Logistics");
        add("commands.santa.empty", "§cNo santa dock registered");
        add("commands.santa.title", "Santa docks:");
        add("commands.santa.try_spawn_santa_base.success", "Successfully placed Santa's base at %s (%s blocks away)");
        add("commands.santa.try_spawn_santa_base.already_placed", "Santa's base was already located at %s (%s blocks away)");
        add("commands.santa.try_spawn_santa_base.failed", "Unable to automatically generate santa's base. Please use the manual command");
        add("container.santa_logistics.santa", "Santa's Sleigh");
        add("chat.santa_dock.available", "Santa dock is connected and active");
        add("chat.santa_dock.to_be_removed", "Santa dock will be deleted at the end of the night");

        add("block.santa_logistics.santa_dock.tooltip_0", "Status: %s");
        add("block.santa_logistics.santa_dock.status.connected", "§aConnected");
        add("block.santa_logistics.santa_dock.status.idle", "Idle");
        add("block.santa_logistics.santa_dock.status.error", "§cError");
        add("block.santa_logistics.santa_dock.status.error.tooltip_0", "Reason:");
        add("block.santa_logistics.santa_dock.status.error.night", "Santa is already flying");
        add("block.santa_logistics.santa_dock.status.error.already_taken", "Address taken");
        add("block.santa_logistics.santa_dock.status.error.out_of_bound", "Max docks per world reached");
        add("block.santa_logistics.santa_dock.status.error.wrong_dimension", "Dock outside of the overworld");
        add("block.santa_logistics.santa_dock.status.connected.tooltip_0", "Santa will pass by this dock tonight");

        addPrefixed("gui.robo_elf.info_header", "Robo Elf Information:");
        addPrefixed("gui.robo_elf.title", "Activity Stress");
        addPrefixed("gui.robo_elf.capacity", "Remaining Charge");
    }

    private void addPrefixed(String key, String value) {
        add("santa_logistics." + key, value);
    }

    public String autoName(String id) {
        String[] words = id.split("_");
        for(int i = 0; i < words.length; i++) {
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
        }
        return String.join(" ", words);
    }
}
