package tech.dttp.block.logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

import tech.dttp.block.logger.save.sql.DbConn;
import tech.dttp.block.logger.command.Commands;
import tech.dttp.block.logger.util.LoggedEventType;

public class BlockLogger implements ModInitializer {
    public static DbConn db;
    @Override
    public void onInitialize() {

        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            Commands commands = new Commands();
            commands.register(dispatcher);
        });
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            db = new DbConn();
            db.connect(server);
        });
        // Block break
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            // SQL
            // Write to database every time a block is broken
            db.writeInteractions(pos.getX(), pos.getY(), pos.getZ(), state, player, world, LoggedEventType.broken);
        });
        // Close DB connection when world is closed
        ServerLifecycleEvents.SERVER_STOPPED.register((server) -> {
            db.close();
        });

        // todo: Block placement pos via hitresult
        //When completed
        System.out.println("[BL] Initialisation completed");
    }

}

