package tech.dttp.block.logger.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import tech.dttp.block.logger.save.sql.DbConn;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class Commands implements Command<Object> {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
    LiteralArgumentBuilder<ServerCommandSource> literalargumentbuilder = literal("bl").
    then(literal("i").
        then(argument("pos", BlockPosArgumentType.blockPos()).
                executes( (c) -> (int)getInteractionsPos(BlockPosArgumentType.getBlockPos(c, "pos")))));

        dispatcher.register(literalargumentbuilder);
    }

    private static Object getInteractionsPos(BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        DbConn.readDataBreakPlace(x, y, z);
        return 1;
    }

    @Override
    public int run(CommandContext<Object> context) throws CommandSyntaxException {
        return 0;
    }

}
