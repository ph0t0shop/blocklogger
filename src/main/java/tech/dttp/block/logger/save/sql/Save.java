package tech.dttp.block.logger.save.sql;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;

public class Save {
    public void saveBreak(int x, int y, int z, BlockState state, PlayerEntity player){
        //Save to database
        DbConn.writeBreak(x, y, z, state, player);
    }

}
