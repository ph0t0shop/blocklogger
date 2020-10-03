package tech.dttp.block.logger.save.sql;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.minecraft.block.BlockState;

public class DbConn {
    public static Connection connect() {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:interactions.db");
            System.out.println("[BL] Connected to database");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e + "");
        }
        return con;
    }
    public static void writeDestroyPlace (int x, int y, int z, Boolean broken, BlockState state2){
        Connection con = DbConn.connect();
        PreparedStatement ps = null;
        try{
            String sql = "INSERT INTO breakPlace(x, y, z, broken, state) VALUES(?,?,?,?,?)";
            ps = con.prepareStatement(sql);
            x = 123;
            ps.setInt(1, x);
            y = 123;
            ps.setInt(2, y);
            z = 123;
            ps.setInt(3, z);
            ps.setBoolean(4, true);
            ps.setString(5, "Hello");
            ps.execute();
            System.out.println("Saved data");

        }
        catch(SQLException e) {
            String sql = "CREATE TABLE breakPlace (x double,y double,z double,broken boolean,state varchar);";
            try {
                ps = con.prepareStatement(sql);
                ps.execute();

                System.out.println("prepared table");
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }
    public static Connection createDestroyPlace(){
        return null;

    }
	 
}
