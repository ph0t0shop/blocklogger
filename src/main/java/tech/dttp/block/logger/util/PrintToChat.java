package tech.dttp.block.logger.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

public class PrintToChat {
    public static void print(PlayerEntity player, String message, Formatting formatting) {
        LiteralText textMessage = new LiteralText(message);
        textMessage.formatted(formatting);
        player.sendMessage(textMessage, false);
    }

    public static void prepareInteractionsPrint(String[] valuesArray, ServerPlayerEntity sender) {
        //Prepare the block's state to make the messages shorter and more readable
        String trimmed = valuesArray[5];
        String state = valuesArray[5];
        int index = state.indexOf("[");
        if(index > 0){
            trimmed =  state.substring(0, index);
        }
        String message = trimmed+" was "+valuesArray[0]+" by "+valuesArray[6]+" at "+valuesArray[7]+" "+valuesArray[8];
        //Print message
        PrintToChat.print(sender, message, Formatting.DARK_AQUA);
    }
}
