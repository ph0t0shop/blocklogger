package tech.dttp.block.logger.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;

public interface IScreenHandlerMixin {
    void setLoggingInfo(BlockEntity pos);
}
