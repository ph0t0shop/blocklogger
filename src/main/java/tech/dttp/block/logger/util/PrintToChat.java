package tech.dttp.block.logger.util;

import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;

public class PrintToChat {
    public static void print(PlayerEntity player, String message){
        player.sendSystemMessage(new LiteralText(message), Util.NIL_UUID);
    }
}
