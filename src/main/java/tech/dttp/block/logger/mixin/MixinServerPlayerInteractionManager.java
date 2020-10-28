package tech.dttp.block.logger.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.dttp.block.logger.BlockLogger;
import tech.dttp.block.logger.util.LoggedEventType;

@Mixin(ServerPlayerInteractionManager.class)
public class MixinServerPlayerInteractionManager {

    @Shadow public ServerWorld world;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;", ordinal = 1, shift = At.Shift.AFTER), method = "interactBlock")
    private void addBlockPlace(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir){
        if (stack.getItem() instanceof BlockItem){
            BlockItem item = (BlockItem)stack.getItem();
            BlockState blockState = item.getBlock().getDefaultState();
            HitResult result = player.raycast(5, 0.0F, true);
            if (result.getType().equals(HitResult.Type.BLOCK)) {
                BlockHitResult blockResult = (BlockHitResult)result;
                BlockLogger.db.writeInteractions(blockResult.getBlockPos().getX(), blockResult.getBlockPos().getY(), blockResult.getBlockPos().getZ(), blockState, player, world, LoggedEventType.placed);
            }
        }
    }
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;", ordinal = 0, shift = At.Shift.AFTER), method = "interactBlock")
    private void addBlockPlace2(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (stack.getItem() instanceof BlockItem) {
            BlockItem item = (BlockItem) stack.getItem();
            BlockState blockState = item.getBlock().getDefaultState();
            HitResult result = player.raycast(5, 0.0F, true);
            if (result.getType().equals(HitResult.Type.BLOCK)) {
                BlockHitResult blockResult = (BlockHitResult) result;
                BlockLogger.db.writeInteractions(blockResult.getBlockPos().getX(), blockResult.getBlockPos().getY(), blockResult.getBlockPos().getZ(), blockState, player, world, LoggedEventType.placed);
            }
        }
    }
}
