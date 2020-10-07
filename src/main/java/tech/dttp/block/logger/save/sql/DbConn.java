package tech.dttp.block.logger.save.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;

public class DbConn {
    public static Connection connect() {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("hallo");
            con = DriverManager.getConnection("jdbc:sqlite:interactions.bl");
            System.out.println("Genau");
            System.out.println("[BL] Connected to database");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e + "");
        }
        return con;
    }

    public static void writeDestroyPlace(int x, int y, int z, Boolean broken, BlockState state, PlayerEntity player) {
        Connection con = DbConn.connect();
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO breakPlace(x, y, z, broken, state, player, time) VALUES(?,?,?,?,?,?,?)";
            ps = con.prepareStatement(sql);
            //x = 123;
            ps.setInt(1, x);
            //y = 67;
            ps.setInt(2, y);
            //z = 124;
            ps.setInt(3, z);
            //Was block broken?
            ps.setBoolean(4, broken); 
            //Blockstate
            ps.setString(5, state.toString());
            //player
            ps.setString(6, generatePlayer(player));
            // time
            ps.setString(7, "17:31 2020-10-07");
            //
            ps.execute();
            System.out.println("[BL] Saved data");

        } catch (SQLException e) {
            String sql = "CREATE TABLE breakPlace (x INT NOT NULL,y INT NOT NULL,z INT NOT NULL,broken BOOLEAN,state VARCHAR, player STRING, time STRING);";
            try {
                ps = con.prepareStatement(sql);
                ps.execute();

                System.out.println("[BL] prepared table");
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void readDataBreakPlace() {
        Connection con = DbConn.connect();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT x,y,z,broken,state,player,time FROM breakPlace;";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            // Repeat for every entry
            while (rs.next()) {
                // print values
                // Broken?
                boolean broken = rs.getBoolean("broken");
                System.out.println("Broken=" + broken);
                // Get coords
                // x
                int x = rs.getInt("x");
                System.out.println("X=" + x);
                // y
                int y = rs.getInt("y");
                System.out.println("Y=" + y);
                // z
                int z = rs.getInt("z");
                System.out.println("Z=" + z);
                // State
                String state = rs.getString("state");
                System.out.println("Block interacted with=" + state);
                // Player
                String player = rs.getString("player");
                System.out.println(player);
                String time = rs.getString("time");
                System.out.println(time);
            }
        } catch (SQLException e) {
            System.out.println(e + "");
        }
    }

    public static String generatePlayer(PlayerEntity player) {
        String playerStringManipulate = player.toString();
        //get rid of start part
        playerStringManipulate = playerStringManipulate.replace("ServerPlayerEntity['", "");
        //delete all after /
        playerStringManipulate = playerStringManipulate.split("/")[0];
        //delete quotes
        playerStringManipulate = playerStringManipulate.replace("'", "");
        //trim
        playerStringManipulate = playerStringManipulate.trim();
        String playerString = playerStringManipulate;
        System.out.println(playerString);
        return playerString;
    }
	 
}
