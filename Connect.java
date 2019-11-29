import java.sql.*;

public class Connect {

    private Connection conDB;   // Connection to the database system.
    private String url;         // URL: Which database?

    public Connect(){
        this.url = "jdbc:db2:c3421a";

        // Register the driver with DriverManager.
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
        } catch(SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }

    }

    public void closeConnection(){
        commit();
        try {
            this.conDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void commit(){
        try {
            conDB.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConDB() {
        return conDB;
    }

    public void setConDB(Connection conDB) {
        this.conDB = conDB;
    }
}
