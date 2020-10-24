package tech.dttp.block.logger.save.sql;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.World;
import tech.dttp.block.logger.LoggedEventType;
import tech.dttp.block.logger.util.PlayerUtils;
import tech.dttp.block.logger.util.PrintToChat;

import java.io.File;
import java.sql.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class DbConn {
    private static Connection con = null;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")
            .withZone(ZoneId.systemDefault()); // year-month-day hour:minute:second timezone

    public void connect(MinecraftServer server) {
        try {
            Class.forName("org.sqlite.JDBC");
            File databaseFile;
            // Database file
            databaseFile = new File(server.getSavePath(WorldSavePath.ROOT).toFile(), "blocklogger.bl");
            con = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getPath().replace('\\', '/'));
            // Check if table exists
            ensureTable("interactions",
                    "(type STRING, x INT NOT NULL, y INT NOT NULL, z INT NOT NULL, dimension STRING NOT NULL, state STRING, player STRING, time INT, rolledbackat INT DEFAULT -1)");
            System.out.println("[BL] Connected to database");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void ensureTable(String name, String description) {
        // Create table if it doesn't exist
        String sql = "CREATE TABLE IF NOT EXISTS " + name + " " + description + ";";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.execute();

            System.out.println("[BL] prepared table");
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public void writeBreak(int x, int y, int z, BlockState state, PlayerEntity player, World world) {
        if (con == null) {
            // Check if database isn't connected
            throw new IllegalStateException("Database connection not initialized");
        }
        try {
            // Save data
            String sql = "INSERT INTO interactions(type, x, y, z, dimension, state, player, time) VALUES(?,?,?,?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);
            // set values to insert
            ps.setString(1, LoggedEventType.broken.name());
            ps.setInt(2, x);
            ps.setInt(3, y);
            ps.setInt(4, z);
            ps.setString(5, PlayerUtils.getPlayerDimension(player));
            ps.setString(6, state.toString());
            ps.setString(7, getPlayerName(player));
            ps.setLong(8, Instant.now().getEpochSecond());
            ps.execute();
            System.out.println("[BL] Saved data");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void readEvents(int x, int y, int z, String dimension, LoggedEventType eventType, ServerCommandSource scs) {
        // Check if database is connected
        if (con == null) {
            throw new IllegalStateException("Database connection not initialized");
        }
        PreparedStatement ps;
        ResultSet rs;
        try {
            //Print initial read to chat - Blocklogger data for X, Y, Z
            String message = "Blocklogger data for "+x+", "+y+", "+z+" in "+dimension;
            try {
                PrintToChat.print(scs.getPlayer(), message);
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
            //Read data
            String sql = "SELECT type,x,y,z,dimension,state,player,time,rolledbackat FROM interactions WHERE x=? AND y=? AND z=? AND dimension=?";
            if (eventType != null) {
                sql += " AND type=?";
            }
            ps = con.prepareStatement(sql);
            ps.setInt(1, x);
            ps.setInt(2, y);
            ps.setInt(3, z);
            ps.setString(4, dimension);
            if (eventType != null) {
                ps.setString(5, eventType.name());
            }
            rs = ps.executeQuery();
            // Repeat for every entry
            while (rs.next()) {
                //Get the info from the database and return
                //For all integers, create a String with the correct values
                int time = rs.getInt("time");
                String timeString = Integer.toString(time);
                x = rs.getInt("x");
                String xString = Integer.toString(x);
                y = rs.getInt("y");
                String yString = Integer.toString(y);
                z = rs.getInt("z");
                String zString = Integer.toString(z);
                String state = rs.getString("state");
                String dimensionString = rs.getString("dimension");
                String type = rs.getString("type");
                String player = rs.getString("player");
                String valuesArray[] = {type, xString, yString, zString, dimensionString, state, player};
                PrintToChat.prepareInteractionsPrint(valuesArray, scs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getPlayerName(PlayerEntity player) {
        // return the player's UUID
        Text playerText = player.getDisplayName();
        return playerText.getString();
    }

    public void close() {
        // Closes connection to database
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
