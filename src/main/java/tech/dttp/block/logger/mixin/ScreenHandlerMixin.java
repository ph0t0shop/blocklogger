package tech.dttp.block.logger.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tech.dttp.block.logger.save.sql.DbConn;
import tech.dttp.block.logger.util.IScreenHandlerMixin;
import tech.dttp.block.logger.util.LoggedEventType;

import java.util.List;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin implements IScreenHandlerMixin {
    @Shadow @Final public List<Slot> slots;
    private BlockEntity loggingBE;
    private boolean shouldLogUpdates = false;

    @Override
    public void setLoggingInfo(BlockEntity loggingBE) {
        if (this.slots.size() == 0) { // Improvement: Check possible race condition where slots aren't yet initialized here
            System.out.println("[BL] Empty inventory opened for logging. Skipping.");
            return;
        }
        this.loggingBE = loggingBE;
        this.shouldLogUpdates = true;
    }
//
//    @Inject(method="method_30010", at=@At("HEAD"), remap=false)
//    public void slotClickInject(int i, int j, SlotActionType slotActionType, PlayerEntity playerEntity, CallbackInfoReturnable<ItemStack> cir) {
//        System.out.println(i + ", " + j + ", " + slotActionType.toString());
//    }

    @Redirect(method ="method_30010", at=@At(value="INVOKE", target="Lnet/minecraft/screen/slot/Slot;takeStack(I)Lnet/minecraft/item/ItemStack;", remap=true), remap = false)
    public ItemStack takeStackRedirect(Slot slot, int amount, int i, int j, SlotActionType slotActionType, PlayerEntity player) {
        ItemStack takenStack = slot.takeStack(amount);
        if (shouldLogUpdates && !(slot.inventory instanceof PlayerInventory)) { // could use an accessor mixin -> slot index -> constraint check instead
            logChange(player, takenStack, ItemStack.EMPTY);
        }
        return takenStack;
    }

    @Redirect(method ="method_30010", at=@At(value="INVOKE", target="Lnet/minecraft/screen/slot/Slot;setStack(Lnet/minecraft/item/ItemStack;)V", remap=true), remap = false)
    public void setStackRedirect(Slot slot, ItemStack stack, int i, int j, SlotActionType slotActionType, PlayerEntity player) {
        if (shouldLogUpdates && !(slot.inventory instanceof PlayerInventory)) {
            logChange(player, slot.getStack(), stack);
        }
        slot.setStack(stack);
    }

    private void logChange(PlayerEntity player, ItemStack oldStack, ItemStack newStack) {
        if (oldStack.isEmpty() && newStack.isEmpty()) return; // nothing to do

        if (!oldStack.isEmpty() && !newStack.isEmpty()) { // 2 non-empty stacks
            if (oldStack.getItem() == newStack.getItem()) { // add or remove to stack of same type
                int newCount = newStack.getCount();
                int oldCount = oldStack.getCount();
                if (newCount > oldCount) { // add items
                    logChange(player, ItemStack.EMPTY, new ItemStack(newStack.getItem(), newCount - oldCount));
                } else { // remove items
                    logChange(player, new ItemStack(newStack.getItem(), oldCount - newCount), ItemStack.EMPTY);
                }
            } else { // split up the actions
                logChange(player, oldStack, ItemStack.EMPTY); // log taking out the old stack
                logChange(player, ItemStack.EMPTY, newStack); // log putting in the new stack
            }
            return;
        }

        boolean oldEmpty = oldStack.isEmpty(); // we know only one is empty
        ItemStack significantStack = oldEmpty ? newStack : oldStack;
        LoggedEventType eventType = oldEmpty ? LoggedEventType.added : LoggedEventType.removed;
        BlockPos pos = loggingBE.getPos();
        System.out.println(pos.toString() + ": " + player.getName().getString() + " " + eventType.toString() + " " + significantStack.toString());
        DbConn.writeContainerTransaction(pos, significantStack, player, eventType);
    }
}
