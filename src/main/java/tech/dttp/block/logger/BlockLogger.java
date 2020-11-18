package tech.dttp.block.logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.util.ActionResult;
import tech.dttp.block.logger.save.sql.DbConn;
import tech.dttp.block.logger.util.InspectorUtils;
import tech.dttp.block.logger.util.PlayerUtils;
import tech.dttp.block.logger.command.Commands;

public class BlockLogger implements ModInitializer {
    public static DbConn db;
    @Override
    public void onInitialize() {

        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            Commands commands = new Commands();
            commands.register(dispatcher);
        });
        // Connect to database when server is started
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            db = new DbConn();
            db.connect(server);
        });
        // Close DB connection when server is closed
        ServerLifecycleEvents.SERVER_STOPPED.register((server) -> {
            db.close();
        });
        // Checks if player is in inspect mode
        InspectorUtils inspectorUtils = new InspectorUtils();
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if(player.hasPermissionLevel(3)&&inspectorUtils.isInspected()){
                DbConn.readEvents(pos,PlayerUtils.getPlayerDimension(player), null, null);
                return ActionResult.SUCCESS;
            }
            else{
            return ActionResult.PASS;
            }
        });
        // When completed
        System.out.println("[BL] Initialisation completed");
    }

}