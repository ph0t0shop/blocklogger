package tech.dttp.block.logger.util;

import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;

import net.minecraft.entity.player.PlayerEntity;

public class PrintToChat {
    public static void print(PlayerEntity player, String message, String color) {
        String formtattedMessage = String.format("%s %s", color, message);
        player.sendSystemMessage(new LiteralText(formtattedMessage), Util.NIL_UUID);
    }

    public static void prepareInteractionsPrint(String[] valuesArray, PlayerEntity player) {
        //Prepare the block's state to make the messages shorter and more readable
        String trimmed = valuesArray[5];
        String state = valuesArray[5];
        int index = state.indexOf("[");
        if(index > 0){
            trimmed =  state.substring(0, index);
        }
        String message = trimmed+" was "+valuesArray[0]+" by "+valuesArray[6]+" at "+valuesArray[7]+" "+valuesArray[8];
        // Print message
        PrintToChat.print(player, message, "§3");
    }
}
