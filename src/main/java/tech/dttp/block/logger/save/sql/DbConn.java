package tech.dttp.block.logger.save.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;

public class DbConn {
    private static Connection con = null;
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z").withZone(ZoneId.systemDefault()); // year-month-day hour:minute:second timezone

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:interactions.bl");
            ensureTable("breakPlace", "(x INT NOT NULL,y INT NOT NULL,z INT NOT NULL,broken BOOLEAN,state VARCHAR, player STRING, time INT)");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e + "");
        }
    }

    private static void ensureTable(String name, String description) {
        String sql = "CREATE TABLE IF NOT EXISTS " + name + " " + description + ";";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.execute();

            System.out.println("[BL] prepared table");
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public static void writeBreakPlace(int x, int y, int z, Boolean broken, BlockState state, PlayerEntity player) {
        if (con == null) {
            throw new IllegalStateException("Database connection not initialized");
        }
        try {
            String sql = "INSERT INTO breakPlace(x, y, z, broken, state, player, time) VALUES(?,?,?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);
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
            ps.setLong(7, Instant.now().getEpochSecond());
            //
            ps.execute();
            System.out.println("[BL] Saved data");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void readDataBreakPlace(int x,int y, int z) {
        if (con == null) {
            throw new IllegalStateException("Database connection not initialized");
        }
        PreparedStatement ps;
        ResultSet rs;
        try {
            System.out.println("Attempting to read data");
            String sql = "SELECT x,y,z,broken,state,player,time FROM breakPlace WHERE x="+x+" AND y="+y+" AND z="+z+";";
            System.out.println(sql);
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            // Repeat for every entry
            while (rs.next()) {
                // print values
                // Broken?
                boolean broken = rs.getBoolean("broken");
                System.out.println("Broken=" + broken);
                // State
                String state = rs.getString("state");
                System.out.println("Block interacted with=" + state);
                // Player
                String player = rs.getString("player");
                System.out.println(player);
                long time = rs.getLong("time");
                System.out.println(timeFormatter.format(Instant.ofEpochSecond(time)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
