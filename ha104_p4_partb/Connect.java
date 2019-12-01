import java.sql.*;

/**
 *  Name: Hassaan Abid
 *  ID: 214243935
 *  EECS Account: ha104
 *  Part B - Database Application
 *  File: Connect.java
 *
 *  Class Connect - establishes the connection with the database
 *  also handles commits and close connection request.
 */
public class Connect {

    private Connection conDB;   // Connection to the database system.
    private String url;         // URL: Which database?

    /**
     * Connect Constructor
     * Register the driver with DriverManager.
     * Handles the possible exceptions.
     */
    public Connect() {
        this.url = "jdbc:db2:c3421a";

        try {
            Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }

        // create connection
        try {
            conDB = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        // turn off auto-commit
        try {
            conDB.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }

    }

    /**
     * Closes DB connection.
     */
    public void closeConnection() {
        commit();
        try {
            this.conDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Commits to the database.
     */
    public void commit() {
        try {
            conDB.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return database connection
     */
    public Connection getConDB() {
        return conDB;
    }

    /**
     * Sets database connection
     * @param conDB database connection
     */
    public void setConDB(Connection conDB) {
        this.conDB = conDB;
    }
}
