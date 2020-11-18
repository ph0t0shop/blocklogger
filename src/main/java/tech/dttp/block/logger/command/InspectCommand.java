package tech.dttp.block.logger.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.SingleRedirectModifier;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import tech.dttp.block.logger.InspectModeHandler;
import tech.dttp.block.logger.save.sql.DbConn;
import tech.dttp.block.logger.util.PlayerUtils;

import java.util.UUID;

public class InspectCommand {
    boolean defaultDim;

    public InspectCommand(boolean defaultDim) {
        this.defaultDim = defaultDim;
    }

    public static void register(LiteralCommandNode root) {
        LiteralCommandNode<ServerCommandSource> inspectNode = CommandManager
                .literal("inspect")
                .executes(context -> setInspectMode(context))
                .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                        .executes(context -> inspect(context, null))
                        .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                                .executes(context -> inspect(context, DimensionArgumentType.getDimensionArgument(context, "dimension")))))
                .build();

        root.addChild(inspectNode);
    }

    public static int inspect(CommandContext<ServerCommandSource> context, ServerWorld dim) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");

        String stringDim;
        if (dim == null) {
            stringDim = PlayerUtils.getPlayerDimension(context.getSource().getPlayer());
        } else {
            stringDim = dim.getRegistryKey().getValue().toString();
        }

        DbConn.readEvents(pos.getX(), pos.getY(), pos.getZ(), stringDim, null, context.getSource().getPlayer());
        return 1;
    }

    public static int setInspectMode(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        InspectModeHandler.toggleMode(context.getSource().getPlayer());
        return 1;
    }
}
