package tech.dttp.block.logger.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import tech.dttp.block.logger.save.sql.DbConn;
import tech.dttp.block.logger.util.LoggedEventType;

@Mixin(BlockItem.class)
public abstract class BlockPlaceMixin extends Item {
    public BlockPlaceMixin(Settings settings) {
    super(settings);
  }

  @Inject(at = @At(value = "INVOKE",target = "Lnet/minecraft/item/ItemPlacementContext;getBlockPos()Lnet/minecraft/util/math/BlockPos;"),method = "place")
  public void place(ItemPlacementContext c, CallbackInfoReturnable<Boolean> info) {
        DbConn.writeInteractions(c.getBlockPos(), c.getWorld().getBlockState(c.getBlockPos()), c.getPlayer(), LoggedEventType.placed);
  }
}
