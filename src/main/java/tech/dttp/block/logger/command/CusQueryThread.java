package tech.dttp.block.logger.command;

import net.minecraft.entity.player.PlayerEntity;
import tech.dttp.block.logger.save.sql.DbConn;

public class CusQueryThread implements Runnable{
    public PlayerEntity player;
    public String query;
    @Override
	public void run() {
		DbConn.cusQuery(this.query, this.player);
	}
}