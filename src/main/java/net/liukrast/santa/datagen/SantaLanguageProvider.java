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
    }

    public String autoName(String id) {
        String[] words = id.split("_");
        for(int i = 0; i < words.length; i++) {
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
        }
        return String.join(" ", words);
    }
}
