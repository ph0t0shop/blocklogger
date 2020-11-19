package tech.dttp.block.logger.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import tech.dttp.block.logger.save.sql.DbConn;
import tech.dttp.block.logger.util.IScreenHandlerMixin;
import tech.dttp.block.logger.util.LoggedEventType;

import java.util.List;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin implements IScreenHandlerMixin {
    @Shadow @Final public List<Slot> slots;
    private BlockEntity loggingBE;
    private PlayerEntity loggingPlayer;
    private boolean shouldLogUpdates = false;
    int containerInvSize = 0;

    @Override
    public void setLoggingInfo(BlockEntity loggingBE, PlayerEntity loggingPlayer) {
        if (this.slots.size() == 0) { // Improvement: Check possible race condition where slots aren't yet initialized here
            System.out.println("[BL] Empty inventory opened for logging. Skipping.");
            return;
        }
        this.loggingBE = loggingBE;
        this.loggingPlayer = loggingPlayer;
        this.shouldLogUpdates = true;
//        Slot lastSlot = this.slots.get(this.slots.size() - 1); // retrieve the last slot, which will be a player inv slot
//        int playerInvSize = lastSlot.inventory.size();
        int playerInvSize = 36;
        this.containerInvSize = this.slots.size() - playerInvSize;
    }

    @Inject(method = "sendContentUpdates", at=@At(value="INVOKE", target="Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;"), locals= LocalCapture.CAPTURE_FAILEXCEPTION)
    public void contentUpdateInject(CallbackInfo ci, int slot, ItemStack newStack, ItemStack oldStack) {
        if (shouldLogUpdates && slot < containerInvSize) {
            logChange(oldStack, newStack);
        }
    }

    private void logChange(ItemStack oldStack, ItemStack newStack) {
        if (oldStack.isEmpty() && newStack.isEmpty()) return; // nothing to do

        if (!oldStack.isEmpty() && !newStack.isEmpty()) { // 2 actions at the same time
            logChange(oldStack, ItemStack.EMPTY); // log taking out the old stack
            logChange(ItemStack.EMPTY, newStack); // log putting in the new stack
            return;
        }

        boolean oldEmpty = oldStack.isEmpty(); // we know only one is empty
        ItemStack significantStack = oldEmpty ? newStack : oldStack;
        LoggedEventType eventType = oldEmpty ? LoggedEventType.added : LoggedEventType.removed;
        BlockPos pos = loggingBE.getPos();
        DbConn.writeContainerTransaction(pos, significantStack, loggingPlayer, eventType);
    }
}
