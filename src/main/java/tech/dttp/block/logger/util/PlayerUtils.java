package tech.dttp.block.logger.util;

import net.minecraft.entity.player.PlayerEntity;

public class PlayerUtils {
    public static String getPlayerDimension(PlayerEntity player) {
        return player.getEntityWorld().getRegistryKey().getValue().toString();
    }
}
