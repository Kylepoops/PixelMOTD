package dev.mruniverse.pixelmotd.database;

public class dataSetup {
    public final String HOST,USERNAME,PASSWORD,DATABASE,TABLE;

    public final int PORT;

    public final boolean USE_SSL,CACHE;


    //public dataSetup(String host, String username, String password, int port, String database, String pTablePrefix, boolean pUseSSL, boolean pCache) {
    //    this.HOST = host;
    //    this.USERNAME = username;
    //    this.PASSWORD = password;
    //    this.PORT = port;
    //    this.DATABASE = database;
    //    this.TABLE_PREFIX = pTablePrefix;
    //    this.USE_SSL = pUseSSL;
    //    this.CACHE = pCache;
    //    this.EXPIRATION_ACTIVATED = false;
    //    this.EXPIRATION_TIME = 0;
    //}

    public dataSetup(String host, String user,String password,int port,String db,String table,boolean useSSL,boolean useCache) {
        this.HOST = host;
        this.USERNAME = user;
        this.PASSWORD = password;
        this.PORT = port;
        this.DATABASE = db;
        this.TABLE = table;
        this.USE_SSL = useSSL;
        this.CACHE = useCache;
    }
}
