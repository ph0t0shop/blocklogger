package tech.dttp.block.logger.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import tech.dttp.block.logger.save.sql.DbConn;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class Commands {
        public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
                dispatcher.register(
                        literal("bl")
                            .requires(scs -> scs.hasPermissionLevel(3))
                                .then(literal("i")
                                        .then(argument("pos", BlockPosArgumentType.blockPos())
                                                .then(argument("dimension", DimensionArgumentType.dimension())
                                                        .executes(scs -> getEventsAt(
                                                                scs.getSource(),
                                                                BlockPosArgumentType
                                                                 .getBlockPos(scs,"pos"),
                                                                        DimensionArgumentType.getDimensionArgument(scs,"dimension"))))
                                                                                .executes(scs -> getEventsAt(
                                                                                        scs.getSource(),
                                                                                        BlockPosArgumentType.getBlockPos(scs,"pos"),null)))));
        }

        private int getEventsAt(ServerCommandSource scs, BlockPos pos, ServerWorld world) throws CommandSyntaxException {
                if (world == null) {
                world = scs.getPlayer().getServerWorld();
                }         
                print(scs, pos, world); 
                return 1;
        }
        private void print(ServerCommandSource scs, BlockPos pos, ServerWorld world){
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                RegistryKey<World> key = world.getRegistryKey();
                String[] data = DbConn.readEvents(x, y, z, key.getValue().getNamespace() + ":" + key.getValue().getPath(), null);
                //PlayerManager.broadcastChatMessage
        }
        
}