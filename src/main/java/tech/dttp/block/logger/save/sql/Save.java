package tech.dttp.block.logger.save.sql;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class Save {
    public void saveBreak(int x, int y, int z, BlockState state, PlayerEntity player, World world){
        //Save to database
        DbConn.writeBreak(x, y, z, state, player, world);
    }

}
