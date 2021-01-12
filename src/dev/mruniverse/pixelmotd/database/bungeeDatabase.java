package dev.mruniverse.pixelmotd.database;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dev.mruniverse.pixelmotd.init.bungeePixelMOTD;
import dev.mruniverse.pixelmotd.utils.bungeeUtils;


public abstract class bungeeDatabase {
    Connection connection;
    // The name of the table we created back in SQLite class.
    public String table = "data";

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void init(){
        connection = getSQLConnection();
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE address = ?");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
        } catch (SQLException ex) {
            bungeePixelMOTD.sendConsole("&9[Database Issue] &fCan't initialize database.");
        }
    }

    // These are the methods you can use to get things out of your database. You of course can make new ones to return different things in the database.
    // This returns the number of people the player killed.
    public String getPlayer(String string) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE address = '"+string+"';");

            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("address").equalsIgnoreCase(string.toLowerCase())){
                    return rs.getString("latestName");
                }
            }
        } catch (SQLException ex) {
            bungeePixelMOTD.sendConsole("&9[Database Issue] &fCan't get information from the database.");
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                bungeePixelMOTD.sendConsole("&9[Database Issue] &fDatabase connection closed.");
            }
        }
        return bungeeUtils.getPlayer();
    }
    // Exact same method here, Except as mentioned above i am looking for total!
    public Integer getAccounts(String string) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE address = '"+string+"';");

            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("address").equalsIgnoreCase(string.toLowerCase())){
                    return rs.getInt("accounts");
                }
            }
        } catch (SQLException ex) {
            bungeePixelMOTD.sendConsole("&9[Database Issue] &fCan't get or modify information from the database.");
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                bungeePixelMOTD.sendConsole("&9[Database Issue] &fDatabase connection closed.");
            }
        }
        return 0;
    }

    public void setAddress(InetAddress address, String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO " + table + " (address,latestName,accounts) VALUES(?,?,?)");
            ps.setString(1, address.toString());

            ps.setString(2, name);
            ps.setInt(3, 1);
            ps.executeUpdate();
        } catch (SQLException ex) {
            bungeePixelMOTD.sendConsole("&9[Database Issue] &fCan't get or modify information from the database.");
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                bungeePixelMOTD.sendConsole("&9[Database Issue] &fDatabase connection closed.");
            }
        }
    }


    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            bungeePixelMOTD.sendConsole("&9[Database Issue] &fCan't connect to Database.");
        }
    }
}
