package tech.dttp.block.logger.save.sql;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;

public class Save {
    public void save(int x, int y, int z, boolean broken, BlockState state, PlayerEntity player){
        //Save to database
        DbConn.writeBreakPlace(x, y, z, broken, state, player);
    }

}
