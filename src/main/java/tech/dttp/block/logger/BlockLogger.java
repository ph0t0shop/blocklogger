package tech.dttp.block.logger;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

import tech.dttp.block.logger.save.sql.DbConn;
import tech.dttp.block.logger.save.txt.ReadConfig;
import tech.dttp.block.logger.save.txt.TxtWrite;

import java.io.IOException;

public class BlockLogger implements ModInitializer {
    @Override
    public void onInitialize() {
        DbConn.connect();
        int writeType = 0;
        String path = ReadConfig.configContents();
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            //SQL
            DbConn.writeDestroyPlace(pos.getX(), pos.getY(), pos.getZ(), true, state, player);
            DbConn.readDataBreakPlace();
            //TXT 
            //"Remove when SQL done" - yitzy299, 2020
            if(writeType==0){
                String txtWriteDataBreak = "*Block break* (Block "+state+") detected at x="+pos.getX()+"; y="+pos.getY()+"; z="+pos.getZ()+". Block was broken by "+player;
                try{
                    TxtWrite.writeToFile(txtWriteDataBreak, path);
                }
                catch(IOException e){
                    e.printStackTrace();
                }
                
                
            }
        });
        //todo: Block placement pos via hitresult
    }
}

