package net.block.logger;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.util.ActionResult;
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
                String txtWriteDataBreak = "*Block break* (Block "+state+") detected at x="+pos.getX()+"; y="+pos.getY()+"; z="+pos.getZ()+". Block was broken by "+player;
                try{
                    write.writeToFile(txtWriteDataBreak, path);
                }
                catch(IOException e){
                    e.printStackTrace();
                }
                
                
            }
        });
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            String txtWriteDataPlace = "*Block place* (Block "+hitResult+") detected at x="+hand+"; y="+hand+"; z="+hand;
            try{
                write.writeToFile(txtWriteDataPlace, path);
            }
            catch(IOException e){
                e.printStackTrace();
            }
            return ActionResult.PASS;

        });
    }
}
