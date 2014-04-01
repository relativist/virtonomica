package autotest;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;

public class CreateDB
{
    public static void main( String args[] )
    {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:market.db");
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "CREATE TABLE MARKET " +
                    "(SESSION        INT     NOT NULL," +
                    " COUNTRY           TEXT    NOT NULL, " +
                    " REGION           TEXT    NOT NULL, " +
                    " CITY           TEXT    NOT NULL, " +
                    " LOCALSALES            REAL     NOT NULL, " +
                    " VOLUME            INT     NOT NULL, " +
                    " NUMSALES            INT     NOT NULL, " +
                    " PRICE            REAL     NOT NULL, " +
                    " QA            REAL     NOT NULL, " +
                    " BRAND         REAL)";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }
}
