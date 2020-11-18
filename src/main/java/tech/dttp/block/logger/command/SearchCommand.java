package tech.dttp.block.logger.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import tech.dttp.block.logger.save.sql.DbConn;
import tech.dttp.block.logger.util.LoggedEventType;
import tech.dttp.block.logger.util.PlayerUtils;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SearchCommand {
    public static void register(LiteralCommandNode root) {
        LiteralCommandNode<ServerCommandSource> searchNode =
                literal("search")
                        .then(argument("action", StringArgumentType.string())
                                .suggests(new ActionSuggestionProvider())
                                .then(argument("targets", EntityArgumentType.players())
                                        .executes(context -> search(context, null, -1))
                                        .then(argument("range", IntegerArgumentType.integer(-1, 100))
                                                .executes(context -> search(context, null, IntegerArgumentType.getInteger(context, "range")))
                                                .then(argument("block", BlockStateArgumentType.blockState())
                                                        .executes(context -> search(context, BlockStateArgumentType.getBlockState(context, "block").getBlockState(), IntegerArgumentType.getInteger(context, "range"))))
                                        )
                                ))
                        .build();

        root.addChild(searchNode);
    }

    private static int search(CommandContext<ServerCommandSource> context, @Nullable BlockState block, int range) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        LoggedEventType eventType;
        String actionString = StringArgumentType.getString(context, "action");
        if (actionString.equalsIgnoreCase("everything")) {
            eventType = null;
        } else {
            eventType = LoggedEventType.valueOf(actionString.toLowerCase());
        }

        String dimension = PlayerUtils.getPlayerDimension(player);
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getOptionalPlayers(context, "targets");

        DbConn.readAdvanced(source, eventType, dimension, targets, block, range);
        return 1;
    }
}
