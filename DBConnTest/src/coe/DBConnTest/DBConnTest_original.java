// ---------------------------------------------------------------------------------------------------------------
// DBConnTest - Database Connectivity Test
//
//  Originally Developed by:  Nicolas Turdo for CoEnterprise
// Rebuilt:  2023
// ---------------------------------------------------------------------------------------------------------------

package coe.DBConnTest;

// ---------------------------------------------------------------------------------------------------------------
// Imports

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.Properties;



public class DBConnTest_original {


    public static void main(final String[] array) {
        final Properties properties = new Properties();
        FileInputStream inStream = null;
        String url  = null;
        String name = null;
        String str  = null;
        String Propertyfile = "dbconntest.properties";
        System.out.println( "\nDBConnTest - Database Connectivity Test" );
        System.out.println( "" );
        System.out.println( "\tFirst checking for " + Propertyfile );
        try {
            inStream = new FileInputStream( Propertyfile );
            System.out.println("\t" + Propertyfile + " found. Reading properties\n\n");
            properties.load(inStream);
        }
        catch (Exception ex5) {
            System.out.println("\t" + Propertyfile + " not found. Switching to interactive mode \n\n");
            // Will want to create a default dbconntest.properties file
            // Write newly saved variables into file
        }
        String str2 = properties.getProperty("DB_VENDOR");
        String str3 = properties.getProperty("DB_HOST");
        String str4 = properties.getProperty("DB_PORT");
        String str5 = properties.getProperty("DB_DATA");
        String s = properties.getProperty("DB_USER");
        String s2 = properties.getProperty("DB_PASS");
        String s3 = properties.getProperty("DRIVER_PATH");
        try {
            while (str2 == null || (!str2.equals("Oracle") && !str2.equals("MSSQL") && !str2.equals("DB2") && !str2.equals("DB2i_app") && !str2.equals("DB2i_toolbox"))) {
                if (str2 != null && str2.equals("")) {
                    str2 = null;
                }
                System.out.println("***Parameter DB_VENDOR cannot be " + str2 + ". Please enter a valid value.\n");
                System.out.print("DB vendor (Oracle, MSSQL, DB2, DB2i_app, DB2i_toolbox): ");
                str2 = ReadString();
            }
            System.out.println(" \nUsing DB vendor -> " + str2 + "\n");
            str3 = prompt(str3, "DB Host");
            if (!str2.startsWith("DB2")) {
                str4 = prompt(str4, "DB Port");
            }
            str5 = prompt(str5, "DB Name");
            s = prompt(s, "DB User");
            s2 = prompt(s2, "DB Pass");
            s3 = prompt(s3, "Driver Path");
            System.out.println("Using the following properties: ");
            System.out.println(" Type:\t " + str2);
            System.out.println(" Host:\t " + str3);
            if (str4 != null && str4.length() != 0) {
                System.out.println(" Port:\t " + str4);
            }
            System.out.println(" Name:\t " + str5);
            System.out.println(" User:\t " + s);
            System.out.println(" Pass:\t " + s2);
            if (s3 != null) {
                System.out.println(" Driver Path:\t " + s3);
            }
            if (str != null) {
                System.out.println(" Use Oracle Service Name? \t " + str);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            if (inStream != null) {
                try {
                    inStream.close();
                }
                catch (IOException ex2) {
                    ex2.printStackTrace();
                }
            }
        }
        finally {
            if (inStream != null) {
                try {
                    inStream.close();
                }
                catch (IOException ex3) {
                    ex3.printStackTrace();
                }
            }
        }
        try {
            if (str2.equals("Oracle")) {
                System.out.print(" Use the catalog name as an Oracle Service Name instead of SID? (y/n) ");
                for (str = ReadString(); !str.equalsIgnoreCase("Y") && !str.equalsIgnoreCase("yes") && !str.equalsIgnoreCase("N") && !str.equalsIgnoreCase("no"); str = ReadString()) {
                    System.out.print("Please answer 'yes' or 'no' ");
                }
                if (str.equalsIgnoreCase("Y") || str.equalsIgnoreCase("YES")) {
                    str = "true";
                }
            }
            final URL url2 = new URL("jar:file:" + s3 + "!/");
            if (str2.equals("Oracle")) {
                name = "oracle.jdbc.OracleDriver";
                if (str != null && str.equals("true")) {
                    url = "jdbc:oracle:thin:@" + str3 + ":" + str4 + "/" + str5;
                }
                else {
                    url = "jdbc:oracle:thin:@" + str3 + ":" + str4 + ":" + str5;
                }
            }
            if (str2.equals("MSSQL")) {
                name = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                url = "jdbc:sqlserver://" + str3 + ":" + str4 + ";DatabaseName=" + str5;
            }
            if (str2.equals("DB2")) {
                name = "com.ibm.db2.jcc.DB2Driver";
                url = "jdbc:db2://" + str3 + ":" + str4 + "/" + str5;
            }
            if (str2.equals("DB2i_app")) {
                name = "com.ibm.db2.jdbc.app.DB2Driver";
                url = "jdbc:db2://" + str3 + "/" + str5;
            }
            if (str2.equals("DB2i_toolbox")) {
                name = "com.ibm.as400.access.AS400JDBCDriver";
                url = "jdbc:as400://" + str3 + "/" + str5;
            }
            if (s3 != null) {
                DriverManager.registerDriver(new DriverShim((Driver)Class.forName(name, true, new URLClassLoader(new URL[] { url2 })).newInstance()));
            }
            final Connection connection = DriverManager.getConnection(url, s, s2);
            System.out.println("\n\tConnection successful. \n");
            System.out.print("Run default query? (y/n) ");
            final String readString = ReadString();
            if (readString.equals("n") || readString.equals("N") || readString.equals("No")) {
                System.out.println("'No' entered.  Exiting");
                return;
            }
            siVersion(connection);
        }
        catch (Exception ex4) {
            ex4.printStackTrace();
        }
        finally {
            System.out.println("\nAll done\n\n");
        }
    }

    public static String prompt(String readString, final String s) throws Exception {
        while (readString == null || readString.length() == 0) {
            System.out.println("***" + s + " cannot be empty = " + readString);
            System.out.print("Please enter valid " + s + ": ");
            readString = ReadString();
        }
        return readString;
    }

    public static void siVersion(final Connection connection) throws SQLException {
        Statement statement = null;
        final String s = "SELECT PRODUCT_LABEL, BUILD_NUMBER FROM SI_VERSION WHERE PRODUCT_LABEL='SI'";
        System.out.println("\nQuerying build number... ");
        try {
            statement = connection.createStatement();
            final ResultSet executeQuery = statement.executeQuery(s);
            System.out.println("\n\tProduct Label\tBuild Number");
            while (executeQuery.next()) {
                System.out.println("\t" + executeQuery.getString("PRODUCT_LABEL") + "\t\t" + executeQuery.getString("BUILD_NUMBER"));
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public static String ReadString() {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        try {
            line = bufferedReader.readLine();
        }
        catch (IOException ex) {
            System.out.println("IO error trying to read input");
        }
        return line;
    }


    // ---------------------------------------------------------------------------------------------------------------
    // Function: Build_Property_File
    // This will create a new property file, populating it the variables and examples
    public boolean Build_Property_File() {
        // Create a file on the OS
        System.out.println("\nCreating a new properties file... ");
        try {

            PrintWriter writer = new PrintWriter( "Propertyfile", "UTF-8");
            writer.println("The first line");
            writer.println("The second line");
            writer.close();


            return true;
        }
        catch (Exception ex) {
            System.out.println("IO error trying to read input");
        }


        return true;
    }


}