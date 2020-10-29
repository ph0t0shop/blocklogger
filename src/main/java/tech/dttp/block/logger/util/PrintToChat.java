package tech.dttp.block.logger.util;

import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

public class PrintToChat {
    public static void print(PlayerEntity player, String message, String color) {
        String formtattedMessage = String.format("%s %s", color, message);
        player.sendSystemMessage(new LiteralText(formtattedMessage), Util.NIL_UUID);
        //player.sendSystemMessage(new LiteralText(message), Util.NIL_UUID);
    }

    public static void prepareInteractionsPrint(String[] valuesArray, ServerCommandSource scs) {
        //Actual message
        String message = valuesArray[5]+" was "+valuesArray[0]+" by "+valuesArray[6]+" at "+valuesArray[7]+" "+valuesArray[8];
        try {
            //Print message
            PrintToChat.print(scs.getPlayer(), message, "§3");
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
    }
}
