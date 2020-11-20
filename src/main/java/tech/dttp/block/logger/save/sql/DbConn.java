package tech.dttp.block.logger.save.sql;

import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.WorldSavePath;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.math.BlockPos;
import tech.dttp.block.logger.util.LoggedEventType;
import tech.dttp.block.logger.util.PlayerUtils;
import tech.dttp.block.logger.util.PrintToChat;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class DbConn {
    private static Connection con = null;
    public static MinecraftServer server = null;

    private static PSBuilder searchQuery;

    public static void connect(MinecraftServer server) {
        try {
            Class.forName("org.sqlite.JDBC");
            File databaseFile;
            // Database file
            databaseFile = new File(server.getSavePath(WorldSavePath.ROOT).toFile(), "blocklogger.bl");
            con = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getPath().replace('\\', '/'));
            // Check if table exists
            ensureTable("interactions",
                    "(type STRING, x INT NOT NULL, y INT NOT NULL, z INT NOT NULL, dimension STRING NOT NULL, state STRING, player STRING, time STRING, rolledbackat INT DEFAULT -1)");
            System.out.println("[BL] Connected to database");
            onConnection();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }


    private static void ensureTable(String name, String description) {
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

    public static void writeInteractions(BlockPos pos, BlockState state, PlayerEntity player, LoggedEventType type) {
        if (con == null) {
            // Check if database isn't connected
            throw new IllegalStateException("Database connection not initialized");
        }
        //Get date and time
        LocalDateTime dateTime = LocalDateTime.now();
        String time = dateTime.format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm"));
        try {
            // Save data
            String sql = "INSERT INTO interactions(type, x, y, z, dimension, state, player, time) VALUES(?,?,?,?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);
            // set values to insert
            ps.setString(1, type.name());
            ps.setInt(2, pos.getX());
            ps.setInt(3, pos.getY());
            ps.setInt(4, pos.getZ());
            ps.setString(5, PlayerUtils.getPlayerDimension(player));
            //Remove { and } from the block entry
            String stateString = state.toString();
            stateString = stateString.replace("Block{", "");
            stateString = stateString.replace("}", "");
            ps.setString(6, stateString);
            ps.setString(7, getPlayerUuid(player));
            ps.setString(8, time);
            ps.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void readEvents(BlockPos pos, String dimension, LoggedEventType eventType, PlayerEntity sourcePlayer) {
        // Check if database is connected
        if (con == null) {
            throw new IllegalStateException("Database connection not initialized");
        }
        PreparedStatement ps;
        ResultSet rs;
        String message = "Blocklogger data for " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + " in "+ dimension;
        PrintToChat.print(sourcePlayer, message, Formatting.GOLD);
        try {
            //Read data
            String sql = "SELECT type,x,y,z,dimension,state,player,time,rolledbackat FROM interactions WHERE x=? AND y=? AND z=? AND dimension=? ORDER BY time DESC LIMIT 10";
            if (eventType != null) {
                sql += " AND type=?";
            }
            ps = con.prepareStatement(sql);
            ps.setInt(1, pos.getX());
            ps.setInt(2, pos.getY());
            ps.setInt(3, pos.getZ());
            ps.setString(4, dimension);
            if (eventType != null) {
                ps.setString(5, eventType.name());
            }
            rs = ps.executeQuery();
            // Repeat for every entry
            while (rs.next()) {
                //Get the info from the database and return
                //For all integers, create a String with the correct values
                int x = rs.getInt("x");
                String xString = Integer.toString(x);
                int y = rs.getInt("y");
                String yString = Integer.toString(y);
                int z = rs.getInt("z");
                String zString = Integer.toString(z);
                String state = rs.getString("state");
                String dimensionString = rs.getString("dimension");
                String type = rs.getString("type");
                String player = rs.getString("player");
                String time = rs.getString("time");
                String valuesArray[] = {type, xString, yString, zString, dimensionString, state, player, time};
                PrintToChat.prepareInteractionsPrint(valuesArray, (ServerPlayerEntity) sourcePlayer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getDisplayName(UUID uuid) {
        MinecraftServer server = DbConn.server;
        String name = server.getUserCache().getByUuid(uuid).getName();
        return name;
    }

    public static String getPlayerUuid(PlayerEntity player) {
        // return the player's UUID as a String
        return player.getUuidAsString();
    }

    public static UUID getUuid(String uuid) {
        return UUID.fromString(uuid);
    }
    public static void close() {
        // Closes connection to database
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	public static void readFromState(String state, ServerCommandSource scs, String dimension) throws CommandSyntaxException {
        if (con == null) {
            // Check if database isn't connected
            throw new IllegalStateException("Database connection not initialized");
        }
        PrintToChat.print(scs.getPlayer(), "Showing 10 most recent entries for "+state, Formatting.GOLD);
        try{
            PreparedStatement ps = con.prepareStatement("SELECT type,x,y,z,time,player FROM interactions WHERE state=? AND dimension=? LIMIT 10");
            ps.setString(1, state);
            ps.setString(2, dimension);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String type = rs.getString(1);
                int x = rs.getInt(2);
                int y = rs.getInt(3);
                int z = rs.getInt(4);
                String date = rs.getString(5);
                String time = rs.getString(6);
                String player = rs.getString(7);
                String message = state+" was "+type+" at "+x+" "+y+" "+z+" in "+PlayerUtils.getPlayerDimension(scs.getPlayer())+" by "+player+" at "+time+" on "+date;
                PrintToChat.print(scs.getPlayer(),message, Formatting.DARK_AQUA);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
	}

	public static void readFromPlayer(ServerCommandSource scs, String player, String dimension)
            throws CommandSyntaxException {
        if (con == null) {
            // Check if database isn't connected
            throw new IllegalStateException("Database connection not initialized");
        }
        PrintToChat.print(scs.getPlayer(), "Showing 10 most recent entries for "+player+" in "+dimension, Formatting.GOLD);
        try{
            PreparedStatement ps = con.prepareStatement("SELECT type,x,y,z,time,state FROM interactions WHERE player=? AND dimension=? ORDER BY time DESC LIMIT 10");
            ps.setString(1, player);
            ps.setString(2, dimension);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String type = rs.getString(1);
                int x = rs.getInt(2);
                int y = rs.getInt(3);
                int z = rs.getInt(4);
                String date = rs.getString(5);
                String state = rs.getString(6);
                String message = state+" was "+type+" at "+x+" "+y+" "+z+" in "+PlayerUtils.getPlayerDimension(scs.getPlayer())+" by "+player+" at "+date;
                PrintToChat.print(scs.getPlayer(),message, Formatting.DARK_AQUA);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
	}

    public static void readAdvanced(ServerCommandSource scs, HashMap<String, Object> propertyMap) throws CommandSyntaxException {
        ServerPlayerEntity sourcePlayer = scs.getPlayer();
        // Check if database is connected
        if (con == null) {
            throw new IllegalStateException("Database connection not initialized");
        }

        PSBuilder.Runner queryRunner = searchQuery.createRunner();
        if (propertyMap.containsKey("action")) {
            queryRunner.fillParameter("action", (String) propertyMap.get("action"));
        }
        if (propertyMap.containsKey("targets")) {
            GameProfileArgumentType.GameProfileArgument targets = (GameProfileArgumentType.GameProfileArgument)propertyMap.get("targets");
            queryRunner.fillParameter("targets", targets.getNames(scs).stream().findFirst().get().getId().toString());
        }
        if (propertyMap.containsKey("block")) {
            BlockStateArgument block = (BlockStateArgument)propertyMap.get("block");
            queryRunner.fillParameter("block", Registry.BLOCK.getId(block.getBlockState().getBlock()).toString() + "%");
        }
        if (propertyMap.containsKey("range")) {
            int range = (Integer)propertyMap.get("range");
            range *= range;
            BlockPos playerPos = sourcePlayer.getBlockPos();
            int x = playerPos.getX();
            int y = playerPos.getY();
            int z = playerPos.getZ();
            queryRunner.fillParameter("range", x, x, y, y, z, z, range);
        }
        String message = "Blocklogger data for query";
        PrintToChat.print(sourcePlayer, message, Formatting.GOLD);
        try {
            //Read data
            ResultSet rs = queryRunner.execute();
            // Repeat for every entry
            while (rs.next()) {
                //Get the info from the database and return
                //For all integers, create a String with the correct values
                int x = rs.getInt("x");
                String xString = Integer.toString(x);
                int y = rs.getInt("y");
                String yString = Integer.toString(y);
                int z = rs.getInt("z");
                String zString = Integer.toString(z);
                String state = rs.getString("state");
                String dimensionString = rs.getString("dimension");
                String type = rs.getString("type");
                String player = rs.getString("player");
                String time = rs.getString("time");
                String date = rs.getString("date");
                String valuesArray[] = {type, xString, yString, zString, dimensionString, state, player, time, date};
                PrintToChat.prepareInteractionsPrint(valuesArray, sourcePlayer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	public static void writeContainerTransaction(BlockPos pos, ItemStack stack, PlayerEntity player, LoggedEventType type) {
        String itemName = stack.toString();
        if (con == null) {
            // Check if database isn't connected
            throw new IllegalStateException("Database connection not initialized");
        }
        //Get date
        LocalDateTime dateTime = LocalDateTime.now();
        String time = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        try {
            // Save data
            String sql = "INSERT INTO interactions(type, x, y, z, dimension, state, player, time) VALUES(?,?,?,?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);
            // set values to insert
            ps.setString(1, type.name());
            ps.setInt(2, pos.getX());
            ps.setInt(3, pos.getY());
            ps.setInt(4, pos.getZ());
            ps.setString(5, PlayerUtils.getPlayerDimension(player));
            ps.setString(6, itemName);
            ps.setString(7, getPlayerUuid(player));
            ps.setString(8, time);
            ps.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
	}


    private static void onConnection () throws SQLException {
        searchQuery = new PSBuilder(con, "SELECT type,x,y,z,dimension,state,player,time,rolledbackat FROM interactions WHERE");
        searchQuery.addPredicate(JDBCType.VARCHAR, "action", "type = ?");
        searchQuery.addPredicate(JDBCType.VARCHAR, "targets", "player = ?");
        searchQuery.addPredicate(JDBCType.VARCHAR, "block", "state LIKE ?");
        searchQuery.addPredicate(JDBCType.VARCHAR, "range",
                "((x - ?)*(x - ?) + (y - ?)*(y - ?) + (z - ?)*(z - ?)) <= ?");
        searchQuery.prepare();
    }
}       
