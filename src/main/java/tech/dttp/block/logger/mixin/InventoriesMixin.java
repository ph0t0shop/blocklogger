package tech.dttp.block.logger.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;


@Mixin(Inventory.class)
public class InventoriesMixin {
    @Inject(at = @At(value = "HEAD"), method = "onClose")
    public void close(PlayerEntity player, CallbackInfo info){
        System.out.println("HALLO");
    }

}
