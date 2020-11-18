package tech.dttp.block.logger.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class PlayerUtils {
    public static String getPlayerDimension(PlayerEntity player) {
        return player.getEntityWorld().getRegistryKey().getValue().toString();
    }
}
