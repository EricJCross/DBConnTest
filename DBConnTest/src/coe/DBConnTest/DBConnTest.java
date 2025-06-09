// ---------------------------------------------------------------------------------------------------------------
// DBConnTest - Database Connectivity Test
//
//  Originally Developed by:  IBM
// Rebuilt:  2023
// ---------------------------------------------------------------------------------------------------------------

package coe.DBConnTest;

// ---------------------------------------------------------------------------------------------------------------
// Imports
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.PrintWriter;
import java.io.File;



public class DBConnTest {
    String Propertyfile = "dbconntest.properties";

    Boolean Need_New_Properties = false;
    String DB_Vendor   = null;
    String DB_Host     = null;
    String DB_Port     = null;
    String DB_Data     = null;
    String DB_User     = null;
    String DB_Password = null;
    String DB_Driver   = null;
    String URL         = null;


    // ---------------------------------------------------------------------------------------------------------------
    // Function: main
    // This will instantiate an instance (object)
    public static void main(final String[] array) {
        DBConnTest DBConnectionTest = new DBConnTest();
        DBConnectionTest.start();
    }

    public void start() {
        // can now access non-static fields
        final Properties properties = new Properties();
        FileInputStream inStream = null;
        //String url  = null;
        String name = null;
        String str  = null;
        System.out.println( "\nDBConnTest - Database Connectivity Test" );
        System.out.println( "" );
        System.out.println( "\tFirst checking for " + this.Propertyfile );
        try {
            inStream = new FileInputStream( this.Propertyfile );
            System.out.println("\t" + this.Propertyfile + " found. Reading properties\n\n");
            properties.load(inStream);
        }
        catch (Exception ex5) {
            System.out.println("\t" + this.Propertyfile + " not found. Switching to interactive mode \n\n");
            this.Need_New_Properties = true;
            // Will want to create a default dbconntest.properties file
            // Write newly saved variables into file
        }
        // Get Connectivity variables from property file
        this.DB_Vendor   = properties.getProperty("DB_VENDOR");
        this.DB_Host     = properties.getProperty("DB_HOST");
        this.DB_Port     = properties.getProperty("DB_PORT");
        this.DB_Data     = properties.getProperty("DB_DATA");
        this.DB_User     = properties.getProperty("DB_USER");
        this.DB_Password = properties.getProperty("DB_PASS");
        this.DB_Driver   = properties.getProperty("DRIVER_PATH");

        try {
            while (this.DB_Vendor == null || (!this.DB_Vendor.equals("Oracle") && !this.DB_Vendor.equals("MSSQL") && !this.DB_Vendor.equals("DB2") && !this.DB_Vendor.equals("DB2i_app") && !this.DB_Vendor.equals("DB2i_toolbox"))) {
                if (this.DB_Vendor != null && this.DB_Vendor.equals("")) {
                    this.DB_Vendor = null;
                }
                System.out.println("*** Parameter DB_VENDOR cannot be " + this.DB_Vendor + ". Please enter a valid value.\n");
                System.out.print("Valid DB vendor's: Oracle, MSSQL, DB2, DB2i_app, DB2i_toolbox: ");
                this.DB_Vendor = ReadString();
            }
            System.out.println(" \nUsing DB vendor -> " + this.DB_Vendor + "\n");

            this.DB_Host = prompt(this.DB_Host, "DB Host");
            if (!this.DB_Vendor.startsWith("DB2")) {
                this.DB_Port = prompt(this.DB_Port, "DB Port");
            }
            this.DB_Data = prompt(this.DB_Data, "DB Name");
            this.DB_User = prompt(this.DB_User, "DB User");
            this.DB_Password = prompt(this.DB_Password, "DB Pass");
            this.DB_Driver = prompt(this.DB_Driver, "Driver Path");

            System.out.println("\n\n Will attempt to Validate connectivity using the following properties: ");
            System.out.println("\tType:\t " + this.DB_Vendor);
            System.out.println("\tHost:\t " + this.DB_Host);
            if (this.DB_Port != null && this.DB_Port.length() != 0) {
                System.out.println("\tPort:\t " + this.DB_Port);
            }
            System.out.println("\tName:\t " + this.DB_Data);
            System.out.println("\tUser:\t " + this.DB_User);
            System.out.println("\tPass:\t " + this.DB_Password);
            if (this.DB_Driver != null) {
                System.out.println(" Driver Path:\t " + this.DB_Driver);
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

            if ( this.Need_New_Properties ) {
                this.Build_Property_File();
            }

            File File_Driver = new File( this.DB_Driver );
            if ( File_Driver.exists() ) {


                if (this.DB_Vendor.equals("Oracle")) {
                    System.out.print(" Use the catalog name as an Oracle Service Name instead of SID? (y/n) ");
                    for (str = ReadString(); !str.equalsIgnoreCase("Y") && !str.equalsIgnoreCase("yes") && !str.equalsIgnoreCase("N") && !str.equalsIgnoreCase("no"); str = ReadString()) {
                        System.out.print("Please answer 'yes' or 'no' ");
                    }
                    if (str.equalsIgnoreCase("Y") || str.equalsIgnoreCase("YES")) {
                        str = "true";
                    }
                }
                final URL url2 = new URL("jar:file:" + this.DB_Driver + "!/");
                if (this.DB_Vendor.equals("Oracle")) {
                    name = "oracle.jdbc.OracleDriver";
                    if (str != null && str.equals("true")) {
                        this.URL = "jdbc:oracle:thin:@" + this.DB_Host + ":" + this.DB_Port + "/" + this.DB_Data;
                    }
                    else {
                        this.URL = "jdbc:oracle:thin:@" + this.DB_Host + ":" + this.DB_Port + ":" + this.DB_Data;
                    }
                }
                if (this.DB_Vendor.equals("MSSQL")) {
                    name = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                    this.URL = "jdbc:sqlserver://" + this.DB_Host + ":" + this.DB_Port + ";DatabaseName=" + this.DB_Data;
                }
                if (this.DB_Vendor.equals("DB2")) {
                    name = "com.ibm.db2.jcc.DB2Driver";
                    this.URL = "jdbc:db2://" + this.DB_Host + ":" + this.DB_Port + "/" + this.DB_Data;
                }
                if (this.DB_Vendor.equals("DB2i_app")) {
                    name = "com.ibm.db2.jdbc.app.DB2Driver";
                    this.URL = "jdbc:db2://" + this.DB_Host + "/" + this.DB_Data;
                }
                if (this.DB_Vendor.equals("DB2i_toolbox")) {
                    name = "com.ibm.as400.access.AS400JDBCDriver";
                    this.URL = "jdbc:as400://" + this.DB_Host + "/" + this.DB_Data;
                }

                if (this.DB_Driver != null) {
                    System.out.println( "\tURL:\t " + this.URL );
                    DriverManager.registerDriver(new DriverShim((Driver)Class.forName(name, true, new URLClassLoader(new URL[] { url2 })).newInstance()));
                }
                System.out.println( "" );
                Properties Connection_Properties = new Properties();
                Connection_Properties.setProperty( "user", this.DB_User );
                Connection_Properties.setProperty( "password", this.DB_Password );
                //final Connection connection = DriverManager.getConnection(this.URL, this.DB_User, this.DB_Password);
                final Connection connection = DriverManager.getConnection(this.URL, Connection_Properties );

                System.out.println("\n\tConnection successful. \n");
                System.out.print("Run default query? (y/n) ");
                final String readString = ReadString();
                if (readString.equals("n") || readString.equals("N") || readString.equals("No")) {
                    System.out.println("'No' entered.  Exiting");
                    return;
                }
                siVersion(connection);



            }
            else {

                System.out.print("*** The driver does not exist.  Cannot perform Connection attempt. ");
            }


        }
        catch (Exception ex4) {
            ex4.printStackTrace();
        }
        finally {
            System.out.println("\nDBConnTest Exiting\n\n");
        }
    }

    public static String prompt(String readString, final String s) throws Exception {
        System.out.print("Please enter " + s + ": ");
        readString = ReadString();

        while (readString == null || readString.length() == 0) {
            System.out.println("*** "  + s + " cannot be empty = " + readString);
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
        //System.out.println("\nCreating a new properties file... ");
        int Length_Comment = 85;
        //String Comment = "";
        StringBuilder CommentBuilder = new StringBuilder();
        for (int i = 0; i < Length_Comment; i++) {
            CommentBuilder.append('#');
        }
        String Comment = CommentBuilder.toString();
        try {

            PrintWriter writer = new PrintWriter( this.Propertyfile, "UTF-8");

            writer.println( Comment );
            writer.println( this.CommentLine( "DBConnTest Properties File", Length_Comment ) );
            writer.println( Comment );
            writer.println( );
            writer.println( Comment );
            writer.println( this.CommentLine( "DB_VENDOR: Database Vendor Type", Length_Comment ) );
            writer.println( this.CommentLine( "Valid Options: DB2, MSSQL, Oracle, DB2i_app, DB2i_toolbox", Length_Comment ) );
            writer.println( Comment );
            writer.println( "DB_VENDOR=" + this.DB_Vendor );
            writer.println( );
            writer.println( Comment );
            writer.println( this.CommentLine( "DB_HOST:", Length_Comment ) );
            writer.println( this.CommentLine( "Valid Options: IP or Host Name of Database Server", Length_Comment ) );
            writer.println( Comment );
            writer.println( "DB_HOST=" + this.DB_Host );
            writer.println( );
            writer.println( Comment );
            writer.println( this.CommentLine( "DB_PORT:", Length_Comment ) );
            writer.println( this.CommentLine( "Valid Options: TCP/IP Database Port", Length_Comment ) );
            writer.println( Comment );
            writer.println( "DB_PORT=" + this.DB_Port );
            writer.println( );
            writer.println( Comment );
            writer.println( this.CommentLine( "DB_DATA:", Length_Comment ) );
            writer.println( this.CommentLine( "Database Catalogue Name", Length_Comment ) );
            writer.println( this.CommentLine( "(SID or ServiceName in Oracle)", Length_Comment ) );
            writer.println( Comment );
            writer.println( "DB_DATA=" + this.DB_Data );
            writer.println( );
            writer.println( Comment );
            writer.println( this.CommentLine( "DB_USER:", Length_Comment ) );
            writer.println( this.CommentLine( "Database User", Length_Comment ) );
            writer.println( Comment );
            writer.println( "DB_USER=" + this.DB_User );
            writer.println( );
            writer.println( Comment );
            writer.println( this.CommentLine( "DB_PASS:", Length_Comment ) );
            writer.println( this.CommentLine( "Database Password", Length_Comment ) );
            writer.println( Comment );
            writer.println( "DB_PASS=" + this.DB_Password );
            writer.println( );
            writer.println( Comment );
            writer.println( this.CommentLine( "DRIVER_PATH: Path to the JDBC driver", Length_Comment ) );
            writer.println( this.CommentLine( "Examples:", Length_Comment ) );
            writer.println( this.CommentLine( "", Length_Comment ) );
            writer.println( this.CommentLine( "Linux", Length_Comment ) );
            writer.println( this.CommentLine( "DRIVER_PATH=/opt/Sterling/drivers/mssql/mssql-jdbc-6.4.0.jre8.jar", Length_Comment ) );
            writer.println( this.CommentLine( "Windows - Use escape characters", Length_Comment ) );
            writer.println( this.CommentLine( "DRIVER_PATH=C:\\IBM\\install\\jdbc\\MSSQL\\mssql-jdbc-6.4.0.jre8.jar", Length_Comment ) );
            writer.println( Comment );
            writer.println( "DRIVER_PATH=" + this.DB_Driver );

            writer.println( "" );
            writer.println( "" );
            writer.println( "" );
            writer.println( "" );
            writer.println( "" );
            writer.println( Comment );
            writer.println( this.CommentLine( "Examples", Length_Comment ) );
            writer.println( this.CommentLine( "", Length_Comment ) );
            writer.println( Comment );
            writer.println( this.CommentLine( "", Length_Comment ) );
            writer.println( this.CommentLine( "", Length_Comment ) );

            writer.println( Comment );
            writer.println( this.CommentLine( "DB_VENDOR=DB2i_toolbox", Length_Comment ) );
            writer.println( this.CommentLine( "DB_HOST=192.168.1.1", Length_Comment ) );
            writer.println( this.CommentLine( "DB_PORT=8471", Length_Comment ) );
            writer.println( this.CommentLine( "DB_DATA=B2Bi_52", Length_Comment ) );
            writer.println( this.CommentLine( "DB_USER=SI_USER", Length_Comment ) );
            writer.println( this.CommentLine( "DB_PASS=password", Length_Comment ) );
            writer.println( this.CommentLine( "DRIVER_PATH=C:\\ibm\\drivers\\iseries\\jt400.jar", Length_Comment ) );
            writer.println( this.CommentLine( "", Length_Comment ) );
            writer.println( this.CommentLine( "", Length_Comment ) );
            writer.println( this.CommentLine( "DB_VENDOR=DB2i_app", Length_Comment ) );
            writer.println( this.CommentLine( "DB_HOST=192.168.1.2", Length_Comment ) );
            writer.println( this.CommentLine( "DB_DATA=B2Bi_526", Length_Comment ) );
            writer.println( this.CommentLine( "DB_USER=sterling", Length_Comment ) );
            writer.println( this.CommentLine( "DB_PASS=password", Length_Comment ) );
            writer.println( this.CommentLine( "DRIVER_PATH=/opt/Sterling/drivers/db2i/db2_classes16.jar", Length_Comment ) );
            writer.println( this.CommentLine( "", Length_Comment ) );
            writer.println( this.CommentLine( "", Length_Comment ) );
            writer.println( this.CommentLine( "DB_VENDOR=Oracle", Length_Comment ) );
            writer.println( this.CommentLine( "DB_HOST=192.168.1.3", Length_Comment ) );
            writer.println( this.CommentLine( "DB_PORT=1521", Length_Comment ) );
            writer.println( this.CommentLine( "DB_DATA=SERVER.COMPANY.COM  (Service Name example)", Length_Comment ) );
            writer.println( this.CommentLine( "DB_USER=sterling", Length_Comment ) );
            writer.println( this.CommentLine( "DB_PASS=password", Length_Comment ) );
            writer.println( this.CommentLine( "DRIVER_PATH=C:\\ibm\\drivers\\oracle\\ojdbc7.jar", Length_Comment ) );
            writer.println( this.CommentLine( "", Length_Comment ) );
            writer.println( this.CommentLine( "", Length_Comment ) );
            writer.println( this.CommentLine( "DB_VENDOR=Oracle", Length_Comment ) );
            writer.println( this.CommentLine( "DB_HOST=192.168.1.4", Length_Comment ) );
            writer.println( this.CommentLine( "DB_PORT=1521", Length_Comment ) );
            writer.println( this.CommentLine( "DB_DATA=SERVER12C (SID example)", Length_Comment ) );
            writer.println( this.CommentLine( "DB_USER=sterling", Length_Comment ) );
            writer.println( this.CommentLine( "DB_PASS=password", Length_Comment ) );
            writer.println( this.CommentLine( "DRIVER_PATH=/opt/Sterling/drivers/oracle/ojdbc6.jar", Length_Comment ) );
            writer.println( this.CommentLine( "", Length_Comment ) );
            writer.println( this.CommentLine( "", Length_Comment ) );
            writer.println( this.CommentLine( "DB_VENDOR=DB2", Length_Comment ) );
            writer.println( this.CommentLine( "DB_HOST=192.168.1.5", Length_Comment ) );
            writer.println( this.CommentLine( "DB_PORT=50001", Length_Comment ) );
            writer.println( this.CommentLine( "DB_DATA=dbo", Length_Comment ) );
            writer.println( this.CommentLine( "DB_USER=sterling", Length_Comment ) );
            writer.println( this.CommentLine( "DB_PASS=password", Length_Comment ) );
            writer.println( this.CommentLine( "DRIVER_PATH=/opt/Sterling/drivers/db2/db2jcc.jar", Length_Comment ) );
            writer.println( this.CommentLine( "", Length_Comment ) );
            writer.println( this.CommentLine( "", Length_Comment ) );
            writer.println( this.CommentLine( "DB_VENDOR=MSSQL", Length_Comment ) );
            writer.println( this.CommentLine( "DB_HOST=192.168.1.6", Length_Comment ) );
            writer.println( this.CommentLine( "DB_PORT=1433", Length_Comment ) );
            writer.println( this.CommentLine( "DB_DATA=SI_USER", Length_Comment ) );
            writer.println( this.CommentLine( "DB_USER=SI_USER", Length_Comment ) );
            writer.println( this.CommentLine( "DB_PASS=password", Length_Comment ) );
            writer.println( this.CommentLine( "DRIVER_PATH=/opt/Sterling/drivers/mssql/mssql-jdbc-6.4.0.jre8.jar", Length_Comment ) );
            writer.println( this.CommentLine( "", Length_Comment ) );
            writer.println( this.CommentLine( "", Length_Comment ) );
            writer.println( Comment );
            writer.println( "" );
            writer.println( "" );

            writer.close();
        }
        catch (Exception ex) {
            System.out.println("IO error trying to read input");
        }

        return true;
    }

    // ---------------------------------------------------------------------------------------------------------------
    // Function: CommentLine
    // This will ensure a correclty spaced property line
    public String CommentLine( String Line, int Length ) {
        String Final_String = "# " + Line;
        Line = Final_String;

        if ( Line.length() < Length ) {
            int Spaces_to_Add = Length - Line.length();
            //Final_String = Line;
            for ( int i = 0; i < Spaces_to_Add - 1; i++  ) {
                Final_String = Final_String + " ";
            }
            Final_String = Final_String + "#";

        }
        return Final_String;

    }



}