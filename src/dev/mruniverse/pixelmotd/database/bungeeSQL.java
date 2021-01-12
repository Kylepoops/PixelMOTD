package dev.mruniverse.pixelmotd.database;

import dev.mruniverse.pixelmotd.init.bungeePixelMOTD;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class bungeeSQL extends bungeeDatabase{
    String dbname;
    public bungeeSQL() {
        dbname = "data";
    }

    public String SQLiteCreateTables = "CREATE TABLE IF NOT EXISTS data (" +
            "`address` varchar(32) NOT NULL," +
            "`latestName` varchar(32) NOT NULL," +
            "`accounts` int(11) NOT NULL," +
            "PRIMARY KEY (`address`)" +
            ");";


    public Connection getSQLConnection() {
        File dataFolder = new File(bungeePixelMOTD.getInstance().getDataFolder(), dbname+".db");

        if (!dataFolder.exists()){
            boolean created;
            try {
                created = dataFolder.createNewFile();
                if(created) {
                    bungeePixelMOTD.sendConsole("&9[Database Info] &fDatabase Created correctly!");
                } else {
                    bungeePixelMOTD.sendConsole("&9[Database Info] &fDatabase verificated.");
                }
            } catch (IOException e) {
                bungeePixelMOTD.sendConsole("&9[Database Issue] &fCan't create database :(");
            }
        } else {
            bungeePixelMOTD.sendConsole("&9[Database Info] &fDatabase verificated.");
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            bungeePixelMOTD.sendConsole("&9[Database Issue] &fCan't initialize database :(");
        } catch (ClassNotFoundException ex) {
            bungeePixelMOTD.sendConsole("&9[Database Issue] &fThe plugin don't found SQLite JBDC Lib :(");
        }
        return null;
    }

    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(SQLiteCreateTables);
            s.close();
        } catch (Throwable e) {
            bungeePixelMOTD.sendConsole("&9[Database Issue] &fCan't load database :(");
        }
        init();
    }
}
