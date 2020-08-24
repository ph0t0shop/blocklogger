package net.block.logger;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;

import net.minecraft.util.ActionResult;
import net.block.logger.save.txt.ReadConfig;
import net.block.logger.save.txt.Write;
import java.io.IOException;

public class BlockLogger implements ModInitializer {
    @Override
    public void onInitialize() {
        int writeType = 0;
        String path = ReadConfig.configContents();
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            if(writeType==0){
                String txtWriteDataBreak = "*Block break* (Block "+state+") detected at x="+pos.getX()+"; y="+pos.getY()+"; z="+pos.getZ()+". Block was broken by "+player;
                try{
                    Write.writeToFile(txtWriteDataBreak, path);
                }
                catch(IOException e){
                    e.printStackTrace();
                }
                
                
            }
        });
    }
}
