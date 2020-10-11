package tech.dttp.block.logger.save.sql;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import tech.dttp.block.logger.LoggedEventType;
import tech.dttp.block.logger.util.PlayerUtils;

import java.io.File;
import java.sql.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DbConn {
    private static Connection con = null;
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z").withZone(ZoneId.systemDefault()); // year-month-day hour:minute:second timezone

    public static void connect(MinecraftServer server) {
        try {
            Class.forName("org.sqlite.JDBC");
            File databaseFile;
            databaseFile = new File(server.getSavePath(WorldSavePath.ROOT).toFile(), "interactions.bl");
            con = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getPath().replace('\\', '/'));
            ensureTable("events", "(type STRING, x INT NOT NULL, y INT NOT NULL, z INT NOT NULL, dimension STRING NOT NULL, oldstate STRING, newstate STRING, player STRING, time INT, rolledbackat INT DEFAULT -1)");
            System.out.println("[BL] Connected to database");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
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

    public static void writeBreak(int x, int y, int z, BlockState state, PlayerEntity player) {
        if (con == null) {
            throw new IllegalStateException("Database connection not initialized");
        }
        try {
            String sql = "INSERT INTO events(type, x, y, z, dimension, oldstate, newstate, player, time) VALUES(?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, LoggedEventType.BREAK.name());
            ps.setInt(2, x);
            ps.setInt(3, y);
            ps.setInt(4, z);
            ps.setString(5, PlayerUtils.getPlayerDimension(player));
            ps.setString(6, state.toString());
            ps.setString(7, null);
            ps.setString(8, getPlayerUuid(player));
            ps.setLong(9, Instant.now().getEpochSecond());
            ps.execute();
            System.out.println("[BL] Saved data");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void readEvents(int x, int y, int z, String dimension, LoggedEventType eventType) {
        if (con == null) {
            throw new IllegalStateException("Database connection not initialized");
        }
        PreparedStatement ps;
        ResultSet rs;
        try {
            System.out.println("Attempting to read data");
            String sql = "SELECT type,x,y,z,dimension,oldstate,newstate,player,time,rolledbackat FROM events WHERE x=? AND y=? AND z=? AND dimension=?";
            if (eventType != null) {
                sql += " AND type=?";
            }
            System.out.println(sql);
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
            StringBuilder sb = new StringBuilder();
            sb.append("----- BlockLogger -----\n");
            while (rs.next()) {
                sb.append(rs.getString("type"));
                sb.append(" Old: ").append(rs.getString("oldstate"));
                sb.append(" New: ").append(rs.getString("newstate"));
                sb.append(" Player: ").append(rs.getString("player"));
                sb.append(" At: ").append(timeFormatter.format(Instant.ofEpochSecond(rs.getLong("time"))));
                sb.append(" Rolled Back? ").append(rs.getLong("rolledbackat") >= 0);
                sb.append("\n");
            }
            System.out.println(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getPlayerUuid(PlayerEntity player) {
        return player.getUuidAsString();
    }
}
