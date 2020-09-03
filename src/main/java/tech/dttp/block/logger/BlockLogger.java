package tech.dttp.block.logger;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import tech.dttp.block.logger.events.PlaceBlockCallback;

import tech.dttp.block.logger.save.sql.Write;
import tech.dttp.block.logger.save.txt.ReadConfig;
import tech.dttp.block.logger.save.txt.TxtWrite;

import net.minecraft.util.ActionResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.BlockEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.io.IOException;

public class BlockLogger implements ModInitializer {
    @Override
    public void onInitialize() {
        
        int writeType = 0;
        String path = ReadConfig.configContents();
        Write.main(null);
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
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
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            return ActionResult.PASS;
        });
        PlaceBlockCallback.EVENT.register((player, world, hand, hitResult, pos) -> {
            System.out.println("HALLO");
            return ActionResult.FAIL;
        });
    }
}

