package net.liukrast.santa.registry;

import com.google.common.base.Stopwatch;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.liukrast.santa.SantaConstants;
import net.liukrast.santa.world.SantaContainer;
import net.liukrast.santa.world.inventory.SantaMenu;
import net.liukrast.santa.world.level.block.SantaDocks;
import net.liukrast.santa.world.level.levelgen.SantaBase;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

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
        Stopwatch stopwatch = Stopwatch.createStarted(Util.TICKER);
        BlockPos pos1 = SantaBase.getPos(level);
        if(pos1 != null) {
            stopwatch.stop();
            return showLocateResult("commands.santa.try_spawn_santa_base.already_placed", sourceStack, BlockPos.containing(sourceStack.getPosition()), pos1, stopwatch.elapsed());
        }
        BlockPos result = SantaBase.generate(level, pos);
        if(result == null) {
            sourceStack.sendFailure(Component.translatable("commands.santa.try_spawn_santa_base.failed"));
            stopwatch.stop();
            return 0;
        }
        stopwatch.stop();
        return showLocateResult("commands.santa.try_spawn_santa_base.success", sourceStack, BlockPos.containing(sourceStack.getPosition()), result, stopwatch.elapsed());
    }

    private static int showLocateResult(
            String key,
            CommandSourceStack source,
            BlockPos sourcePosition,
            BlockPos result,
            Duration duration
    ) {
        int i = Mth.floor(dist(sourcePosition.getX(), sourcePosition.getZ(), result.getX(), result.getZ()));
        Component component = ComponentUtils.wrapInSquareBrackets(Component.translatable("chat.coordinates", result.getX(), result.getY(), result.getZ()))
                .withStyle(
                        p_214489_ -> p_214489_.withColor(ChatFormatting.GREEN)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + result.getX() + " " + result.getY() + " " + result.getZ()))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip")))
                );
        source.sendSuccess(() -> Component.translatable(key, component, i), false);
        SantaConstants.LOGGER.info("Placing Santa's base took {} ms", duration.toMillis());
        return i;
    }

    private static float dist(int x1, int z1, int x2, int z2) {
        int i = x2 - x1;
        int j = z2 - z1;
        return Mth.sqrt((float)(i * i + j * j));
    }
}
