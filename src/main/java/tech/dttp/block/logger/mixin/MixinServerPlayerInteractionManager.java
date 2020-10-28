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


    /**
     * Add a mixin that will be called after an item is used. It will do these steps in order
     *    - Check if the item is a BlockItem
     *    - If so, get the blockstate and blockitem of that block
     *    - Then get the block that the player is looking at {@link net.minecraft.entity.Entity#raycast(double, float, boolean)}
     *    - Check if what the player is looking at is a block
     *    - if it is a block, then cast to a {@link BlockHitResult}
     *    - Then write to the database the pos of the block result, the state we gathered earlier,
     *      the player, the world the player used an item in, and the {@link LoggedEventType#PLACED}
     *
     *      In the rare condition of the player looking at the top part of the falling block, the player position
     *      is logged instead of the block pos with {@link LoggedEventType#PLACED_PLAYER_POS}
     *
     *      The 2nd mixin just ensures that the interaction is written for creative and survival players
     */

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;", ordinal = 1, shift = At.Shift.AFTER), method = "interactBlock")
    private void addBlockPlace(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir){
        if (stack.getItem() instanceof BlockItem){
            BlockItem item = (BlockItem)stack.getItem();
            BlockState blockState = item.getBlock().getDefaultState();
            HitResult result = player.raycast(5, 0.0F, true);
            if (result.getType().equals(HitResult.Type.BLOCK)) {
                BlockHitResult blockResult = (BlockHitResult)result;
                BlockLogger.db.writeInteractions(blockResult.getBlockPos().getX(), blockResult.getBlockPos().getY(), blockResult.getBlockPos().getZ(), blockState, player, world, LoggedEventType.PLACED);
            } else if (result.getType().equals(HitResult.Type.MISS)){
                BlockLogger.db.writeInteractions((int)Math.round(player.getX()), (int)Math.round(player.getY()), (int) Math.round(player.getZ()), blockState, player, world, LoggedEventType.PLACED_PLAYER_POS);
            }
        }
    }
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;", ordinal = 0, shift = At.Shift.AFTER), method = "interactBlock")
    private void addBlockPlace2(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (stack.getItem() instanceof BlockItem){
            BlockItem item = (BlockItem)stack.getItem();
            BlockState blockState = item.getBlock().getDefaultState();
            HitResult result = player.raycast(5, 0.0F, true);
            if (result.getType().equals(HitResult.Type.BLOCK)) {
                BlockHitResult blockResult = (BlockHitResult)result;
                BlockLogger.db.writeInteractions(blockResult.getBlockPos().getX(), blockResult.getBlockPos().getY(), blockResult.getBlockPos().getZ(), blockState, player, world, LoggedEventType.PLACED);
            } else if (result.getType().equals(HitResult.Type.MISS)){
                BlockLogger.db.writeInteractions((int)Math.round(player.getX()), (int)Math.round(player.getY()), (int) Math.round(player.getZ()), blockState, player, world, LoggedEventType.PLACED_PLAYER_POS);
            }
        }
    }
}
