package tech.dttp.block.logger.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandler;


@Mixin(ScreenHandler.class)
public class InventoryMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;"), method = "close")
    public void close(PlayerEntity player, CallbackInfo info){
        //System.out.println("HALLO");
    }
    /*
    TODO:
    Add transactions by creating two arrays: container before and container after
    The compare each slot and log differences
    
    */
    
    
}
