package help;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class InsertDB
{
    public static void main( String args[] )
    {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test2.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "INSERT INTO MARKET (SESSION,COUNTRY,REGION,CITY,LOCALSALES,VOLUME,NUMSALES,PRICE,QA,BRAND) " +
                    "VALUES (" +
                    1 +
                    ",'"+"one" +"'"+
                    ",'"+"two" +"'"+
                    ",'"+"three" +"'"+
                    ","+"89.96" +
                    ","+Integer.valueOf("1110") +
                    ","+Integer.valueOf("321") +
                    ","+Long.valueOf("1.23") +
                    ","+Long.valueOf("3.23") +
                    ","+Long.valueOf("4.23") +
                    ");";
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Records created successfully");
    }
}
