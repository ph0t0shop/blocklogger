package net.block.logger;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.block.logger.save.txt.readConfig;
import net.block.logger.save.txt.write;
import java.io.IOException;

public class BlockLogger implements ModInitializer {
    @Override
    public void onInitialize() {
        int writeType = 0;
        String path = readConfig.configContents();
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            if(writeType==0){
                String txtWriteData = "World = "+world+" & Player = "+player+" & Block position = X:"+pos.getX()+" Y:"+pos.getY()+" Z:"+pos.getZ()+" & Block replaced = "+state+" & Entity = "+entity;
                try{
                    write.writeToFile(txtWriteData, path);
                }
                catch(IOException e){
                    e.printStackTrace();
                }
                
                
            }
        });
    }
}
