package tech.dttp.block.logger.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import tech.dttp.block.logger.save.sql.DbConn;
import tech.dttp.block.logger.util.LoggedEventType;

@Mixin(BlockItem.class)
public abstract class BlockPlaceMixin extends Item {
    public BlockPlaceMixin(Settings settings) {
    super(settings);
  }

  /**
   * Log item placement hook
   */
  @Inject(
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/item/ItemPlacementContext;getBlockPos()Lnet/minecraft/util/math/BlockPos;"
    ),
    method = "place"
  )
  public void place(ItemPlacementContext context, CallbackInfoReturnable<Boolean> info) {
      BlockPos pos = context.getBlockPos();
      PlayerEntity player = context.getPlayer();
      World world = context.getWorld();
      BlockState state = world.getBlockState(pos);
      DbConn.writeInteractions(pos.getX(), pos.getY(), pos.getZ(), state, player, LoggedEventType.placed);
  }
}
