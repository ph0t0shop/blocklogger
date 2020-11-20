package tech.dttp.block.logger.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import tech.dttp.block.logger.save.sql.DbConn;
import tech.dttp.block.logger.util.LoggedEventType;
import tech.dttp.block.logger.util.PlayerUtils;

import java.util.Collection;
import java.util.HashMap;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SearchCommand {
    public static void register(LiteralCommandNode root) {
        LiteralCommandNode<ServerCommandSource> searchNode =
                literal("search").then(argument("criteria", StringArgumentType.greedyString()).suggests(CriteriumParser.getInstance())
                        .executes(context -> search(context, StringArgumentType.getString(context, "criteria"))))
                        .build();

        root.addChild(searchNode);
    }

    private static int search(CommandContext<ServerCommandSource> context, String criteria) throws CommandSyntaxException {
        HashMap<String, Object> propertyMap = CriteriumParser.getInstance().rawProperties(criteria);
        DbConn.readAdvanced(context.getSource(), propertyMap);
        return 1;
    }

//    private static int search(CommandContext<ServerCommandSource> context, @Nullable BlockState block, int range) throws CommandSyntaxException {
//        ServerCommandSource source = context.getSource();
//        ServerPlayerEntity player = source.getPlayer();
//
//        LoggedEventType eventType;
//        String actionString = StringArgumentType.getString(context, "action");
//        if (actionString.equalsIgnoreCase("everything")) {
//            eventType = null;
//        } else {
//            eventType = LoggedEventType.valueOf(actionString.toLowerCase());
//        }
//
//        String dimension = PlayerUtils.getPlayerDimension(player);
//        Collection<ServerPlayerEntity> targets = EntityArgumentType.getOptionalPlayers(context, "targets");
//
//        DbConn.readAdvanced(source, eventType, dimension, targets, block, range);
//        return 1;
//    }
}
