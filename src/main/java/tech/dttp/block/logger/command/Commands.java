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
                                                searchArea(scs.getSource(), BlockPosArgumentType.getBlockPos(scs, "from"), BlockPosArgumentType.getBlockPos(scs, "to"))
                                                )))));
        }
        
        private int searchArea(ServerCommandSource scs, BlockPos pos1, BlockPos pos2) {
                int x1=pos1.getX();
                int y1=pos1.getY();
                int z1=pos1.getZ();
                int x2=pos2.getX();
                int y2=pos2.getY();
                int z2=pos2.getZ();
                int xDifference = x2-x1;
                if (xDifference>0){
                        for(int l=xDifference;l>x2;l++){
                        //Perform checks for y and z
                        System.out.println("Positive X");
                        }
                }
                else if(xDifference<0){
                        for(int l=x2-x1;l>x2;l++){
                        //Perform checks for y and z
                        System.out.println("Negative X");
                        }
                }
                else{
                        //Perform checks for y and z
                        System.out.println("X=0");
        
                }
                //Prints stating that this feature isn't ready
                try {
                        //Print message
                        PrintToChat.print(scs.getPlayer(), "This feature has not been implemented yet, please ask your server admin to check for blocklogger v0.2.4", "§4");
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                return 0;
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
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                DbConn.readEvents(x, y, z, dimension, null, scs);
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
