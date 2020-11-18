package tech.dttp.block.logger.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import tech.dttp.block.logger.save.sql.DbConn;
import tech.dttp.block.logger.util.InspectorUtils;
import tech.dttp.block.logger.util.PlayerUtils;

public class InspectCommand implements Command<ServerCommandSource> {
    boolean defaultDim;

    public InspectCommand(boolean defaultDim) {
        this.defaultDim = defaultDim;
    }

    public static void register(LiteralCommandNode root) {
        LiteralCommandNode<ServerCommandSource> inspectNode = CommandManager
                .literal("inspect")
                .executes(setInspectMode())
                .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                        .executes(new InspectCommand(true))
                        .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
                                .executes(new InspectCommand(false))))
                .build();

        root.addChild(inspectNode);
    }

    private static Command<ServerCommandSource> setInspectMode() {
        InspectorUtils.toggle();
        return null;
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
        String dimension = defaultDim ? PlayerUtils.getPlayerDimension(context.getSource().getPlayer()) : DimensionArgumentType.getDimensionArgument(context, "dimension").getRegistryKey().getValue().toString();

        DbConn.readEvents(pos, dimension, null, context.getSource().getPlayer());
        return 0;
    }
}
