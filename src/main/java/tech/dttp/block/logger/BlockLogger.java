package tech.dttp.block.logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

import tech.dttp.block.logger.save.sql.DbConn;
import tech.dttp.block.logger.command.Commands;

public class BlockLogger implements ModInitializer {
    @Override
    public void onInitialize() {
        //Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            Commands.register(dispatcher);
        });
        DbConn.connect();
        System.out.println("[BL] Connected to database");
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            //SQL
            //Write to database every time a block is broken
            DbConn.writeBreakPlace(pos.getX(), pos.getY(), pos.getZ(), true, state, player);
        });
        //todo: Block placement pos via hitresult
    }
}

