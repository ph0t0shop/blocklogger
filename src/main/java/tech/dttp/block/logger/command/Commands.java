package tech.dttp.block.logger.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import tech.dttp.block.logger.save.sql.DbConn;
import tech.dttp.block.logger.util.PlayerUtils;
import tech.dttp.block.logger.util.PrintToChat;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class Commands {

        public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
                dispatcher.register(literal("bl").requires(scs -> scs.hasPermissionLevel(3))
                                .then(literal("i").then(argument("pos", BlockPosArgumentType.blockPos())
                                        .executes(scs -> getEventsAt(scs.getSource(),BlockPosArgumentType.getBlockPos(scs,"pos"), PlayerUtils.getPlayerDimension(scs.getSource().getPlayer())))
                                        .then(argument("dimension", DimensionArgumentType.dimension())
                                                .executes(scs -> getEventsAt(scs.getSource(),BlockPosArgumentType.getBlockPos(scs,"pos"),DimensionArgumentType.getDimensionArgument(scs,"dimension").toString())))))
                                .then(literal("search")
                                        .then(argument("Player", EntityArgumentType.player())
                                                .executes(scs -> searchPlayer(scs.getSource(), EntityArgumentType.getPlayer(scs, "Player"), PlayerUtils.getPlayerDimension(scs.getSource().getPlayer()).toString()))
                                                .then(argument("Dimension", DimensionArgumentType.dimension())
                                                .executes(scs -> searchPlayer(scs.getSource(), EntityArgumentType.getPlayer(scs, "Player"), DimensionArgumentType.getDimensionArgument(scs, "Dimension").toString()))))
                                        .then(argument("Block", BlockStateArgumentType.blockState())
                                                .executes(scs -> search(scs.getSource(), BlockStateArgumentType.getBlockState(scs, "Block"), PlayerUtils.getPlayerDimension(scs.getSource().getPlayer()).toString()))
                                                .then(argument("Dimension", DimensionArgumentType.dimension())
                                                        .executes(scs -> search(scs.getSource(), BlockStateArgumentType.getBlockState(scs, "Block"), DimensionArgumentType.getDimensionArgument(scs, "Dimension").toString())))))
                                .then(literal("scan")
                                        .then(argument("from", BlockPosArgumentType.blockPos()).then(argument("to", BlockPosArgumentType.blockPos()).executes(scs ->
                                                searchArea(scs.getSource(), BlockPosArgumentType.getBlockPos(scs, "from"), BlockPosArgumentType.getBlockPos(scs, "to"), PlayerUtils.getPlayerDimension(scs.getSource().getPlayer()))
                                                ).then(argument("dimension", DimensionArgumentType.dimension())
                                                .executes(scs -> searchArea(scs.getSource(), BlockPosArgumentType.getBlockPos(scs, "from"), BlockPosArgumentType.getBlockPos(scs, "to"), DimensionArgumentType.getDimensionArgument(scs,"dimension").toString())))))));
        }
        
        private int searchArea(ServerCommandSource scs, BlockPos pos1, BlockPos pos2, String dimension)
                throws CommandSyntaxException {
                if (dimension == null) {
                        dimension = PlayerUtils.getPlayerDimension(scs.getPlayer());
                }
                printArea(scs, pos1, pos2, dimension);
                return 1;
        }

        private int searchPlayer(ServerCommandSource scs, ServerPlayerEntity player, String dimension) {
                try {
                        DbConn.readFromPlayer(scs, DbConn.getPlayerName(player), dimension);
                        System.out.println(DbConn.getPlayerName(player));
                } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                }
                return 1;
        }
        private int getEventsAt(ServerCommandSource scs, BlockPos pos, String dimension) throws CommandSyntaxException {
                if (dimension == null) {
                        dimension = PlayerUtils.getPlayerDimension(scs.getPlayer());
                }         
                print(scs, pos, dimension); 
                return 1;
        }
        public void print(ServerCommandSource scs, BlockPos pos, String dimension) throws CommandSyntaxException{
                printArea(scs, pos, pos, dimension);
        }
        public void printArea(ServerCommandSource scs, BlockPos pos1, BlockPos pos2, String dimension) {
                DbConn.readEvents(pos1, pos2, dimension, null, scs);
        }
        private int search(ServerCommandSource source, BlockStateArgument blockState, String dimension) throws CommandSyntaxException {
                String state = blockState.getBlockState().toString();
                String stateString = state.toString();
                stateString = stateString.replace("Block{", "");
                stateString = stateString.replace("}", "");
                DbConn.readFromState(stateString, source, dimension);
                return 1;
        }
        
        
}
