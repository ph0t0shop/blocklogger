package tech.dttp.block.logger;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import tech.dttp.block.logger.command.InspectCommand;
import tech.dttp.block.logger.save.sql.DbConn;
import tech.dttp.block.logger.util.PlayerUtils;
import tech.dttp.block.logger.util.PrintToChat;


import java.util.HashSet;

public class InspectModeHandler {
    private static HashSet<ServerPlayerEntity> inspectingPlayers = new HashSet<>();

    public static void init() {
        AttackBlockCallback.EVENT.register(((playerEntity, world, hand, blockPos, direction) -> {
            if (inspectingPlayers.contains(playerEntity)) {
                String dim = PlayerUtils.getPlayerDimension(playerEntity);

                DbConn.readEvents(blockPos.getX(), blockPos.getY(), blockPos.getZ(), dim, null, (ServerPlayerEntity) playerEntity);
                return ActionResult.FAIL;
            }

            return ActionResult.PASS;
        }));
    }

    public static void toggleMode(ServerPlayerEntity playerEntity) {
        if (inspectingPlayers.contains(playerEntity)) {
            inspectingPlayers.remove(playerEntity);
        } else {
            inspectingPlayers.add(playerEntity);
        }

        PrintToChat.print(playerEntity, "Toggled Inspect Mode", Formatting.GOLD);
    }
}
