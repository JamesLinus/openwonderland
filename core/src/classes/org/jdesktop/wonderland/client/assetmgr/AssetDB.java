/**
 * Project Wonderland
 *
 * $RCSfile: AssetDB.java,v $
 *
 * Copyright (c) 2004-2007, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * $Revision: 1.15 $
 * $Date: 2007/08/07 17:01:12 $
 * $State: Exp $
 */
package org.jdesktop.wonderland.client.assetmgr;


import java.io.File;
import java.net.URISyntaxException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.config.WonderlandConfigUtil;
import org.jdesktop.wonderland.common.AssetType;

/**
 * The AssetDB class represents the client-side cache of assets. The database
 * itself simply stores the entries found in the cache; the cached assets are
 * actually stored in a corresponding directorly.
 * <p>
 * Each entry in the database is uniquely identified by a variable-length string
 * that includes the repository from which it came and the relative path of the
 * resource within the repository.
 * <p>
 * The resource may be specified in one of several ways, which dictate how it is
 * uniquely identified in the asset database. Typically resources are specified
 * within the configuration of a cell in WFS.
 * <p>
 * (1) An absolute path of the resource is specified within the cell configuration.
 * Here, that resource, and only that resource is used. This is used very rarely.
 * In this case, the full URL uniquely identifies the resource.
 * <p>
 * (2) A relative path of the resource is specified with respect to the default
 * repository for the instance of Wonderland. This case is the legacy case from
 * v0.4 and earlier versions of Wonderland. The resource is uniquely identified
 * by the relative path.
 * <p>
 * (3) A relative path of the resource within some module is specified. This
 * mechanism is new to v0.5 Wonderland and later. The resource is uniquely
 * identified by the relative path and the unique name of the module.
 * <p>
 * The RESOURCE_PATH field gives the unique identification of the resource and
 * takes the form of a URL:
 * <p>
 * Case 1: http://<repository>/<relative path>
 * Case 2: wlr://<relative path>
 * Case 3: wlm://<module name>/<relative path>
 * <p>
 * The FILENAME field gives only the file name. In case (1) and (3), it is the
 * value of <relative path>. In case (2), it is also <relative path>. Note that
 * in case (1) <relative path> includes everything after the machine domain
 * name.
 * <p>
 * The BASE_URL field gives the base URL from which the resource case. In case(1),
 * it is <repository>, case (2) its the URL of the repository currently in use,
 * and case (3), its the URL from which the resource was actually fetched, as
 * defined by the list of available repositories in the model.
 * <p>
 * <p>
 * <h3>Database Version<h3>
 *
 * Each database has a version. To allow multiple database caches with different
 * versions to exist on the same machine, the location of the asset database
 * includes the version. That is, the location of the database is: derby.system.home +
 * version + database name.
 * 
 * RESOURCE_URI: The abstract URI describing the asset
 * URL: The URL from which the asset was fetched.
 * 
 * @author paulby
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class AssetDB {

    /* The default name of the asset database */
    private static final String DB_NAME = "AssetDB";
    
    /* The maximum length of strings in the database */
    private static final int MAX_STRING_LENGTH = 8192;

    
    /* The error logger for this class */
    private static Logger logger = Logger.getLogger(AssetDB.class.getName());
   
    /* The database connection, null if not connected */
    private Connection dbConnection = null;
    
    /* The list of properties that represent the database configuration */
    private Properties dbProperties = null;
    
    /* True if the database is connected, false if not */
    private boolean isConnected = false;
    
    /* The name of the database, initially DB_NAME */
    private String dbName = null;
    
    /**
     * Default constructor
     */
    public AssetDB() {
        /* Initialize the name to some default name */
        this.dbName = AssetDB.DB_NAME;

        /* Log a message saying we are kicking off the database */
        logger.fine("AssetDB: Initializing the asset database, name=" + this.dbName);
                
        /*
         * Attempt the set the base directory of the database, which could fail.
         * Log an error if it does and exit. XXX Do we really need to exit? XXX
         */
        if (this.setDBSystemDir() == false) {
            logger.severe("AssetDB: Unable to set database directory");
            System.exit(1);
        }
        
        /*
         * Attempt to open the database, exit with severe error. XXX Do we
         * really need to exit? Perhaps we can just continue without using a
         * cache in fail-safe mode? XXX
         */
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, "AssetDB: jdbc EmbeddedDriver not available, exiting.", ex);
            ex.printStackTrace();
            System.exit(1);
        }
        
        /* Create the properties that describe the database */
        dbProperties = new Properties();
        dbProperties.put("user", "assetmgr");
        dbProperties.put("password", "wonderland");    
        dbProperties.put("derby.driver", "org.apache.derby.jdbc.EmbeddedDriver");
        dbProperties.put("derby.url", "jdbc:derby:");
        dbProperties.put("db.table", "ASSET");
        dbProperties.put("db.schema", "APP");   
        
        /*
         * Check to see if the database exists. If it does exist, attempt to
         * connect to it. If it does not exist, then try to create it first. This
         * also handles if a database exists, but is for a previous version.
         */
        if(!dbExists()) {
            try {
                logger.fine("AssetDB does not exist, creating it at location "+getDatabaseLocation());
                createDatabase();
            } catch(Exception e) {
                e.printStackTrace();
            } catch(Error er) {
                er.printStackTrace();
            }
            
            /* Disconnect from the database after creation and attempt to re-connect */
            disconnect();
            if (!connect()) {
                System.out.println("Unable to open AssetDB, exiting");
                System.out.println("Check you don't have a Wonderland client already running");
                System.exit(1);
            }
        } else {            
            if (!connect()) {
                System.out.println("Unable to open AssetDB, exiting");
                System.out.println("Check you don't have a Wonderland client already running");
                System.exit(1);
            }
        }
    }
        
    /**
     * Returns the location of the database. This location is the full path name.
     * 
     * @return The full location to the database
     */
    public String getDatabaseLocation() {
        String dbLocation = System.getProperty("derby.system.home") + "/" + dbName;
        return dbLocation;
    }
    
    /**
     * Returns the URL representation of the database
     * 
     * @return The URL representation of the database
     */
    public String getDatabaseUrl() {
        String dbUrl = dbProperties.getProperty("derby.url") + dbName;
        return dbUrl;
    }
    
    /**
     * Returns true if the database is connected, false if not.
     * 
     * @return True if the database is connected, false if not.
     */
    public boolean isConnected() {
        return this.isConnected;
    }

    /**
     * Connect to the database. Return true upon success, false upon falure
     * 
     * @return True upon success, false upon failure.
     */
    public boolean connect() {
        /*
         * Attempt to connect to the database, also compile some SQL statements
         * that we'll use. Set the isConnected flag upon result.
         */
        try {
            dbConnection = DriverManager.getConnection(this.getDatabaseUrl(), dbProperties);
            stmtSaveNewRecord = dbConnection.prepareStatement(strSaveAsset);
            stmtUpdateExistingRecord = dbConnection.prepareStatement(strUpdateAsset);
            stmtGetAsset = dbConnection.prepareStatement(strGetAsset);
            stmtDeleteAsset = dbConnection.prepareStatement(strDeleteAsset);
            stmtUpdateLastAccessed = dbConnection.prepareStatement(strUpdateLastAccessed);
            stmtComputeTotalSize = dbConnection.prepareStatement(strComputeTotalSize);
            
            this.isConnected = dbConnection != null;
        } catch (SQLException ex) {
            isConnected = false;
            dbConnection = null;
            ex.printStackTrace();
        }
        
        logger.fine("AssetDB: Done attempting to connect, ret=" + this.isConnected);
        return isConnected;
    }
    
    /**
     * Disconnects from the database.
     */
    public void disconnect() {
        if(isConnected) {
            String dbUrl = getDatabaseUrl();
            dbProperties.put("shutdown", "true");
            try {
                DriverManager.getConnection(dbUrl, dbProperties);
            } catch (SQLException ex) {
                /*
                 * When shutting down the database, an ERROR 08006 is normal and
                 * indicates that the database has indeed been shutdown. Check
                 * for that here
                 */
                if (ex.getSQLState().equals("08006") == true) {
                    logger.log(Level.INFO, "AssetDB: Shutdown was normal");
                }
                else {
                    logger.log(Level.WARNING, "Failed to disconnect from AssetDB " + ex.getMessage(), ex);
                }
            }
            isConnected = false;
            dbConnection = null;
        }
    }
    
    /**
     * Adds a new asset to database. Returns true upon success, false upon
     * failure. If the asset already exists, this method logs an exception and
     * returns false.
     * 
     * @param asset The asset to add to the database
     * @return True if the asset was added successfully, false if not
     */
    public boolean addAsset(Asset asset) {
        boolean isSaved = false;
        synchronized(stmtSaveNewRecord) {
            try {
                String checksum = (asset.getChecksum() == null) ? "" : asset.getChecksum();
                logger.fine("[ASSET DB] ADD " + asset.getResourceURI().toString() + " [" + checksum + "]");
                logger.fine("[ASSET DB] ADD url: " + asset.getURL());
                logger.fine("[ASSET DB] ADD type: " + asset.getType().toString());
                stmtSaveNewRecord.clearParameters();
                stmtSaveNewRecord.setString(1, asset.getResourceURI().toString());
                stmtSaveNewRecord.setString(2, checksum);
                stmtSaveNewRecord.setString(3, "" /*asset.getURL()*/);
                stmtSaveNewRecord.setString(4, asset.getType().toString());
                stmtSaveNewRecord.setLong(5, System.currentTimeMillis());
                stmtSaveNewRecord.setLong(6, 0 /* XXX */);
                int row = stmtSaveNewRecord.executeUpdate();
                logger.fine("AssetDB: Saving asset, row=" + row);
                isSaved = true;            
            } catch (java.sql.SQLException sqle) {
                logger.log(Level.SEVERE, "AssetDB: SQL Error saving record for " + asset.getResourceURI().toString());
                sqle.printStackTrace();
            }
        }
        return isSaved;
    }
    
    /**
     * Returns true if the asset database already exist. The asset database is
     * considered to exist, if the proper version of the database exists. If
     * not, a new one is created.
     * <p>
     * The version of the database is encoded in the path of the database. In
     * this way, multiple database versions may exist on a system at once.
     * 
     * @return True if the database exists, false if not
     */
    private boolean dbExists() {
        return new File(this.getDatabaseLocation()).exists();
    }
    
    /**
     * Sets up the directory in which the database resides, creating the directory
     * if it does not exist. Returns true if successfull, false if not. Also
     * checks to see that we are able to do this operation, logs an error if not
     * and returns false.
     */
    private boolean setDBSystemDir() {
        try {
            /* The Derby home directory is simply the user cache directory */
            String systemDir = WonderlandConfigUtil.getWonderlandDir();
            System.setProperty("derby.system.home", systemDir);

            /* Log a message with this directory */
            logger.fine("AssetDB: Database home directory=" + systemDir);
            
            /*
             * Create the directories. Note: an odd thing happens here, if the
             * directories already exist, then mkdirs() return false. So we
             * should not rely upon the return value of mkdirs()
             */
            File fileSystemDir = new File(systemDir);
            fileSystemDir.mkdirs();
            return true;
        } catch (java.lang.SecurityException excp) {
            /* Log an error and return null */
            logger.severe("AssetDB: Not allowed to setup database: " + excp.toString());
            return false;
        }
    }
    
    /**
     * Create the tables in the database, takes an open connection to the database.
     * Returns true upon success, false upon failure.
     * 
     * @param dbConnection The open connection to the database
     * @return True upon success, false upon failure.
     */
    private boolean createTables(Connection dbConnection) {
        try {
            Statement statement = dbConnection.createStatement();
            statement.execute(strCreateAssetTable);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /**
     * Creates the database. Returns true if the database was successfully
     * created, false if not
     * 
     * @return True if the database was successfully created, false if not
     */
    private boolean createDatabase() {
        boolean bCreated = false;

        /*
         * Create the database. Upon exception, print out a message to the log
         * and return false. Otherwise return true.
         */
        dbProperties.put("create", "true");        
        try {
            dbProperties.list(System.out);
            Connection tmpConnection = DriverManager.getConnection(this.getDatabaseUrl(), dbProperties);
            bCreated = createTables(tmpConnection);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to create database "+ex.getMessage(), ex);
            ex.printStackTrace();
        } catch(Exception e) {
            logger.log(Level.SEVERE, "Failed to create database "+e.getMessage(), e);
            e.printStackTrace();
        } catch(Error er) {
            logger.log(Level.SEVERE, "Failed to create database "+er.getMessage(), er);
            er.printStackTrace();
            
        }
        dbProperties.remove("create");
        logger.fine("AssetDB: Created new database at " + this.getDatabaseLocation());
        return bCreated;
    }
    
    /**
     * Updates an existing asset on the database with new information. If the
     * asset does not exist, this method logs an exception and returns false.
     * 
     * @param asset The asset to update
     * @return True upon success, false upon failure
     */
    public boolean updateAsset(Asset asset) {
        boolean bEdited = false;
        synchronized(stmtUpdateExistingRecord) {
            try {
                String checksum = (asset.getChecksum() == null) ? "" : asset.getChecksum();
                logger.fine("[ASSET DB] UPDATE " + asset.getResourceURI().toString() + " [" + checksum + "]");

                stmtUpdateExistingRecord.clearParameters();

                stmtUpdateExistingRecord.setString(1, asset.getResourceURI().toString());
                stmtUpdateExistingRecord.setString(2, checksum);
                stmtUpdateExistingRecord.setString(3, asset.getURL());
                stmtUpdateExistingRecord.setString(4, asset.getType().toString());
                stmtUpdateExistingRecord.setLong(5, System.currentTimeMillis());
                stmtUpdateExistingRecord.setLong(6, 0 /* XXX */);
                stmtUpdateExistingRecord.setString(7, asset.getResourceURI().toString());
                stmtUpdateExistingRecord.setString(8, checksum);

                stmtUpdateExistingRecord.executeUpdate();
                bEdited = true;
            } catch(SQLException sqle) {
                logger.log(Level.SEVERE, "AssetDB: SQL Error updating record for " + asset.getResourceURI().toString());
                sqle.printStackTrace();
            }
        }
        return bEdited;
    }
    
    /**
     * Removes an asset given its unique identifying URI and checksum. Returns
     * true if the asset was successfully removed, false if not.
     * 
     * @param assetID The unique ID of the asset (URI, checksum)
     * @return True upon success, false upon failure
     */
    public boolean deleteAsset(AssetID assetID) {
        boolean bDeleted = false;
        synchronized(stmtDeleteAsset) {
            try {
                String checksum = (assetID.getChecksum() == null) ? "" : assetID.getChecksum();
                stmtDeleteAsset.clearParameters();
                stmtDeleteAsset.setString(1, assetID.getResourceURI().toString());
                stmtDeleteAsset.setString(2, checksum);
                stmtDeleteAsset.executeUpdate();
                bDeleted = true;
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }
        
        return bDeleted;
    }
    
    /**
     * Return the asset record for the supplied unique asset ID, or null if
     * the asset described by the ID is not in the cache
     * 
     * @param assetID The unique asset ID (URI, checksum)
     * @return The asset record in the cache, null if not present.
     */
    public Asset getAsset(AssetID assetID) {
        Asset asset = null;
        synchronized(stmtGetAsset) {
            try {
                String checksum = (assetID.getChecksum() == null) ? "" : assetID.getChecksum();
                logger.fine("[ASSET DB] GET " + assetID.getResourceURI().toString() + " [" + checksum + "]");
                stmtGetAsset.clearParameters();
                stmtGetAsset.setString(1, assetID.getResourceURI().toString());
                stmtGetAsset.setString(2, checksum);
                ResultSet result = stmtGetAsset.executeQuery();
                if (result.next() == true) {
                    /* Fetch the information from the database */
                    String uri = result.getString("ASSET_URI");
                    String cksum = result.getString("CHECKSUM");
                    String url = result.getString("URL");
                    AssetType assetType = AssetType.valueOf(result.getString("TYPE"));
                    long lastAccessed = result.getLong("LAST_ACCESSED");
                    long size = result.getLong("SIZE");
                    
                    /*
                     * Create an ResourceURI class, log and error and return null
                     * if its syntax is invalid.
                     */
                    asset = AssetManager.getAssetManager().assetFactory(assetType, assetID);
                    asset.setURL(url);
                    
                    /*
                     * In the database, a null checksum is an empty string (""),
                     * but in the code, a null checksum is null
                     */
                    if (cksum.compareTo("") == 0) {
                        asset.setChecksum(null);
                    }
                    else {
                        asset.setChecksum(cksum);
                    }
                }
            } catch(SQLException sqle) {
                sqle.printStackTrace();
            }
        }
        
        /* Update the time the asset was last accessed */
        if (asset != null) {
            this.updateLastAccessed(assetID);
        }
        return asset;
    }

    /**
     * Update the "last accessed" time with the current time (in milliseconds
     * since the epoch) for the given asset uri and checksum
     */
    private void updateLastAccessed(AssetID assetID) {
        synchronized(stmtUpdateLastAccessed) {
            try {
                String checksum = (assetID.getChecksum() == null) ? "" : assetID.getChecksum();
                logger.fine("[ASSET DB] UPDATE LAST " + assetID.getResourceURI().toString() + " [" + checksum + "]");
                
                stmtUpdateLastAccessed.clearParameters();
                stmtUpdateLastAccessed.setLong(1, System.currentTimeMillis());
                stmtUpdateLastAccessed.setString(2, assetID.getResourceURI().toString());
                stmtUpdateLastAccessed.setString(3, checksum);
                stmtUpdateLastAccessed.executeUpdate();
            } catch(SQLException sqle) {
                logger.log(Level.SEVERE, "AssetDB: SQL Error updating last accessed for " + assetID.getResourceURI());
                sqle.printStackTrace();
            }
        }
    }
    
    /**
     * Computes and returns the sum of all of the assets.
     * 
     * @return The size in bytes of all of the assets
     */
    public long getTotalSize() {
        synchronized (stmtComputeTotalSize) {
            try {
                /* Do the SQL statement to compute the sum */
                stmtComputeTotalSize.clearParameters();
                ResultSet result = stmtComputeTotalSize.executeQuery();
                
                /* Fetch the one result, which should be the sum */
                if (result.next() == true) {
                    long size = result.getLong(0);
                    return size;
                }
            } catch(SQLException sqle) {
                sqle.printStackTrace();
            }
        }
        return 0;
    }
    
    /**
     * Prints out all of the assets to stdout
     */
    public void listAssets() {
        try {
            Statement queryStatement = dbConnection.createStatement();
            ResultSet result = queryStatement.executeQuery(strGetListEntries);
            logger.warning("[ASSET DB] LIST");
            while(result.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append(result.getString("ASSET_URI") + "\t");
                sb.append(result.getString("CHECKSUM") + "\t");
                sb.append(result.getString("URL")+"\t");
                sb.append(result.getString("TYPE") + "\t");
                sb.append(result.getLong("LAST_ACCESSED") + "\t");
                sb.append(result.getLong("SIZE") + "\n");
                logger.warning(sb.toString());
            }
            logger.warning("[ASSET DB] DONE LIST");
        } catch(SQLException sqle) {
            sqle.printStackTrace();
        }
    }
    
    /**
     * Main method that has a simple command-line interface to test the database.
     * The usage is: java AssetDB [COMMAND] [ARGS], where COMMAND can be:
     * <p>
     * LIST: Lists all of the entries in the database
     * ADD: Add an entry to the database, followed by the required data fields
     */
    public static void main(String[] args) throws URISyntaxException {
        /* Create the database and open a connection */
        AssetDB db = new AssetDB();
        
        /* Print out the essential information */
        logger.warning("AssetDB: Database Location: " + db.getDatabaseLocation());
        logger.warning("AssetDB: Database URL:      " + db.getDatabaseUrl());
        logger.warning("AssetDB: Is Connected?      " + db.isConnected());

//        AssetID assetID = new AssetID(new ResourceURI("wla://mpk20/sphere2.dae"), "4d92377dbd58f3ba2908354d2b9618f06303d5e9");
//        Asset asset = db.getAsset(assetID);
//        asset = new AssetFile(assetID);
//        db.addAsset(asset);
        
        /* List the assets in the database */
        db.listAssets();
        
        /* Disconnect from the database and exit */
        db.disconnect();
    }
   
    /* The various SQL statements to operate on the database */
    private PreparedStatement stmtSaveNewRecord;
    private PreparedStatement stmtUpdateExistingRecord;
    private PreparedStatement stmtGetListEntries;
    private PreparedStatement stmtGetAsset;
    private PreparedStatement stmtDeleteAsset;
    private PreparedStatement stmtUpdateLastAccessed;
    private PreparedStatement stmtComputeTotalSize;
   
    /* Creates the tables in the database */
    private static final String strCreateAssetTable =
            "create table APP.ASSET (" +
            "    ASSET_URI      VARCHAR(" + AssetDB.MAX_STRING_LENGTH + ") not null, " +
            "    CHECKSUM       VARCHAR(40) not null, " +
            "    URL            VARCHAR(" + AssetDB.MAX_STRING_LENGTH + "), " +
            "    TYPE           VARCHAR(10), " +
            "    LAST_ACCESSED  BIGINT, " +
            "    SIZE           BIGINT, " +
            "    PRIMARY KEY (ASSET_URI, CHECKSUM) " +
            ")";
    
    /* Get an asset based upon the unique resource path name */
    private static final String strGetAsset =
            "SELECT * FROM APP.ASSET WHERE ASSET_URI = ? AND CHECKSUM = ?";
    
    /* Save an asset given all of its values */
    private static final String strSaveAsset =
            "INSERT INTO APP.ASSET " +
            "   (ASSET_URI, CHECKSUM, URL, TYPE, LAST_ACCESSED, SIZE)" +
            "VALUES (?, ?, ?, ?, ?, ?)";
    
    /* Return all of the entries based upon the unique resource path key */
    private static final String strGetListEntries =
            "SELECT ASSET_URI, CHECKSUM, URL, TYPE, LAST_ACCESSED, SIZE " +
            "FROM APP.ASSET ORDER BY ASSET_URI ASC";
    
    /* Updates an entry using its resource path and values */
    private static final String strUpdateAsset =
            "UPDATE APP.ASSET " +
            "SET ASSET_URI = ?, " +
            "    CHECKSUM = ?, " +
            "    URL = ?, " +
            "    TYPE = ?, " +
            "    LAST_ACCESSED = ?, " +
            "    SIZE = ? " +
            "WHERE ASSET_URI = ? AND CHECKSUM = ?";
    
    /* Updates an asset's last accessed time, used after a "get" */
    private static final String strUpdateLastAccessed =
            "UPDATE APP.ASSET " +
            "SET LAST_ACCESSED = ? " +
            "WHERE ASSET_URI = ? AND CHECKSUM = ?";
    
    /* Deletes an entry using its unique resource path */
    private static final String strDeleteAsset =
            "DELETE FROM APP.ASSET WHERE ASSET_URI = ? AND CHECKSUM = ?";
    
    /* Computes the sum of the sizes of the assets */
    private static final String strComputeTotalSize = "" +
            "SELECT SUM(SIZE) FROM APP.ASSET";
}
