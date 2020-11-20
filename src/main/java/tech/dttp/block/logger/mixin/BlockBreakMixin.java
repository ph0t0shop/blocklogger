package tech.dttp.block.logger.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tech.dttp.block.logger.save.sql.DbConn;
import tech.dttp.block.logger.util.LoggedEventType;

@Mixin(Block.class)
public abstract class BlockBreakMixin implements ItemConvertible {
  @Inject(at = @At("HEAD"), method = "onBreak")
  public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo info) {
    DbConn.writeInteractions(pos, state, player, LoggedEventType.broken, true);
  }
}