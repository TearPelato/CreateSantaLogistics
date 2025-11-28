package net.liukrast.santa.registry;

import com.mojang.brigadier.CommandDispatcher;
import net.liukrast.santa.world.level.block.SantaDocks;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class SantaCommands {
    private SantaCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("santa").requires(s -> s.hasPermission(2))
                .then(Commands.literal("listDocs")
                        .executes(ctx -> listDocs(ctx.getSource()))
                )
        );
    }

    private static int listDocs(CommandSourceStack sourceStack) {
        sourceStack.sendSuccess(() -> SantaDocks.get(sourceStack.getLevel()).component(), true);
        return 1;
    }
}
