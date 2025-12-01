package net.liukrast.santa.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.liukrast.santa.world.SantaContainer;
import net.liukrast.santa.world.inventory.SantaMenu;
import net.liukrast.santa.world.level.block.SantaDocks;
import net.liukrast.santa.world.level.levelgen.SantaBase;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SantaCommands {
    private SantaCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("santa").requires(s -> s.hasPermission(2))
                .then(Commands.literal("list_docs")
                        .executes(ctx -> listDocs(ctx.getSource()))
                )
                .then(Commands.literal("container")
                        .executes(ctx -> openContainer(ctx.getSource()))
                )
                .then(Commands.literal("try_spawn_santa_base")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(ctx -> trySpawnSantaBase(ctx.getSource(), BlockPosArgument.getBlockPos(ctx, "pos")))
                        )
                        .executes(ctx -> trySpawnSantaBase(ctx.getSource(), null))
                )
        );
    }

    private static int listDocs(CommandSourceStack sourceStack) {
        sourceStack.sendSuccess(() -> SantaDocks.get(sourceStack.getLevel()).component(), true);
        return 1;
    }

    private static int openContainer(CommandSourceStack sourceStack) throws CommandSyntaxException {
        ServerPlayer player = sourceStack.getPlayerOrException();
        player.openMenu(new SimpleMenuProvider(
                new MenuConstructor() {
                    @Override
                    public @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
                        return new SantaMenu(containerId, playerInventory, SantaContainer.get((ServerLevel) player.level()));
                    }
                },
                Component.translatable("container.santa_logistics.santa")
        ));
        return 1;
    }

    private static int trySpawnSantaBase(CommandSourceStack sourceStack, @Nullable BlockPos pos) {
        ServerLevel level = sourceStack.getServer().getLevel(ServerLevel.OVERWORLD);
        if(level == null) return 0;
        BlockPos santa = SantaBase.getPos(level);
        if(santa != null) {
            sourceStack.sendFailure(Component.literal("Santa's base has already been generated at " + santa) /*TODO: Translation*/);
            return 0;
        }
        BlockPos result = SantaBase.generate(level, pos);
        if(result == null) {
            sourceStack.sendFailure(Component.literal("Unable to automatically generate santa's base. Please manually set it via command" /*TODO: TRANSLATION*/));
            return 0;
        }
        sourceStack.sendSuccess(() -> Component.literal("Santa's base successfully placed at " + result), true);
        return 1;
    }
}
