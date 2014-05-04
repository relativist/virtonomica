package help;


import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;

public class CreateDB
{
    @Test
    public void testRun() throws Exception {
        CreateDB.main();
    }

    public static void main()
    {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:store.db");
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
//            String sql = "CREATE TABLE MARKET " +
//                    "(SESSION        INT     NOT NULL," +
//                    " PRODUCT           TEXT    NOT NULL, " +
//                    " COUNTRY           TEXT    NOT NULL, " +
//                    " REGION           TEXT    NOT NULL, " +
//                    " CITY           TEXT    NOT NULL, " +
//                    " LOCALSALES            REAL     NOT NULL, " +
//                    " VOLUME            INT     NOT NULL, " +
//                    " NUMSALES            INT     NOT NULL, " +
//                    " PRICE            REAL     NOT NULL, " +
//                    " QA            REAL     NOT NULL, " +
//                    " BRAND         REAL)";
            String sql = "CREATE TABLE MARKET " +
                    "(SESSION        INT     NOT NULL," +
                    " DEPNAME           TEXT    NOT NULL, " +
                    " DEPURL           TEXT    NOT NULL, " +
                    " RESULT           TEXT    NOT NULL, " +
                    " PRODUCT           TEXT    NOT NULL) " ;

            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }

    public void createMarket(){

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:market.db");
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "CREATE TABLE MARKET " +
                    "(SESSION        INT     NOT NULL," +
                    " PRODUCT           TEXT    NOT NULL, " +
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


    public void createStore(){

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:store.db");
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "CREATE TABLE MARKET " +
                    "(SESSION        INT     NOT NULL," +
                    " DEPNAME           TEXT    NOT NULL, " +
                    " DEPURL           TEXT    NOT NULL, " +
                    " RESULT           TEXT    NOT NULL, " +
                    " PRODUCT           TEXT    NOT NULL) " ;

            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }

    public void createPlant(){

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:plant.db");
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "CREATE TABLE PLANT " +
                    "(SESSION        INT     NOT NULL," +
                    " DEPURL           TEXT    NOT NULL) " ;

            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }

    public void createReport(){

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:report.db");
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "CREATE TABLE REPORT " +
                    "(SESSION        INT     NOT NULL," +
                    " DEPURL           TEXT    NOT NULL, " +
                    " PROBLEM           TEXT    NOT NULL) " ;

            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }

    public void createStoreBuild(){

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:storeBuild.db");
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "CREATE TABLE DEPARTMENT " +
                    "(CITY        TEXT     NOT NULL," +
                    " DEPNAME           TEXT    NOT NULL, " +
                    " ISBUILD           BLOB    NOT NULL) " ;

            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }

    public void createMedicalDb(){

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:medical.db");
            System.out.println("Opened database successfully");


            stmt = c.createStatement();
            String sql = "CREATE TABLE MARKET " +
                    "(SESSION        INT     NOT NULL," +
                    " CITY           TEXT    NOT NULL, " +
                    " BOL            REAL     NOT NULL, " +
                    " DC            REAL     NOT NULL, " +
                    " POL            REAL     NOT NULL, " +
                    " STOMK            REAL     NOT NULL, " +
                    " CNM            REAL     NOT NULL, " +
                    " PRICE            REAL     NOT NULL, " +
                    " VOLUME            REAL     NOT NULL, " +
                    " COUNT         REAL      NOT NULL )";


            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }

    public void createLaundry(){

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:laundry.db");
            System.out.println("Opened database successfully");


            stmt = c.createStatement();
            String sql = "CREATE TABLE MARKET " +
                    "(SESSION        INT     NOT NULL," +
                    " CITY           TEXT    NOT NULL, " +
                    " PRSAM            REAL     NOT NULL, " +
                    " HIM            REAL     NOT NULL, " +
                    " PR            REAL     NOT NULL, " +
                    " PRICE            REAL     NOT NULL, " +
                    " VOLUME            REAL     NOT NULL, " +
                    " COUNT         REAL      NOT NULL )";


            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }

    public void createRestDb(){

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:rest.db");
            System.out.println("Opened database successfully");


            stmt = c.createStatement();
            String sql = "CREATE TABLE MARKET " +
                    "(SESSION        INT     NOT NULL," +
                    " CITY           TEXT    NOT NULL, " +
                    " COFFEE            REAL     NOT NULL, " +
                    " GREECE            REAL     NOT NULL, " +
                    " USTR            REAL     NOT NULL, " +
                    " PRICE            REAL     NOT NULL, " +
                    " VOLUME            REAL     NOT NULL, " +
                    " COUNT         REAL      NOT NULL )";


            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }

    public void createFitnesDb(){

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:fitnes.db");
            System.out.println("Opened database successfully");


            stmt = c.createStatement();
            String sql = "CREATE TABLE MARKET " +
                    "(SESSION        INT     NOT NULL," +
                    " CITY           TEXT    NOT NULL, " +
                    " HEALTH            REAL     NOT NULL, " +
                    " TANZ            REAL     NOT NULL, " +
                    " FITNES            REAL     NOT NULL, " +
                    " PRICE            REAL     NOT NULL, " +
                    " VOLUME            REAL     NOT NULL, " +
                    " COUNT         REAL      NOT NULL )";


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
