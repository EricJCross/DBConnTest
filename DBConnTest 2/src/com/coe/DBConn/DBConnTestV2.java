
package com.coe.DBConn;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.io.PrintWriter;




public class DBConnTestV2 {
    private BufferedReader Read_Buffer = new BufferedReader(new InputStreamReader(System.in));
    private Properties DB_ConnTest_Properties = new Properties();
    String Propertyfile = "database.properties";  // This is the default property filename.  Will also be set via a commandline argument


    Boolean Need_New_Properties = false;
    String DB_Vendor = null;
    String DB_Host = null;
    String DB_Port = null;
    String DB_Data = null;
    String DB_User = null;
    String DB_Password = null;
    String DB_Driver_Path = null;
    String DB_Driver = null;
    URL URL = null;
    String TrustStore_Path = null;
    String TrustStore_Password = null;
    String SSLTLS = null;
    String J2SSE_OVERRIDE_TLS = null;
    String Oracle_Service_Name = null;
    String Connection_String = null;
    Connection DB_Connection = null;


    //---------------------------------------------------------------------------------------------------------------#
    // Function: DBConnTestV2 (constructor)
    // Description: Allow overriding the default property file via a command‐line argument.
    // Parameter: propertyFilePath - optional path to the .properties file; if null or empty, use default.
    // Returns: none
    //---------------------------------------------------------------------------------------------------------------#
    public DBConnTestV2( String propertyFilePath ) {
        if (propertyFilePath != null && !propertyFilePath.trim().isEmpty() ) {
            this.Propertyfile = propertyFilePath;
        } // else keep the existing default "database.properties"
    
        // ensure the Properties object is always initialized
        this.DB_ConnTest_Properties = new Properties();
    }


    //---------------------------------------------------------------------------------------------------------------#
    // Function: main
    // Description: This is the entry point of the program. It creates an instance of DBConnTestV2 and calls its
    // start method.
    //---------------------------------------------------------------------------------------------------------------#
    public static void main(final String[] args ) {
        System.out.println("\nDBConnTest - Database Connectivity Test");
        System.out.println("");
        // Optional help flag
        if ( args.length > 0 && 
                ( 
                    args[0].equals( "-h" ) || 
                    args[0].equals( "--help" ) || 
                    args[0].contains("?" )
                ) 
            ) {
            System.out.println("\nHelp:");
            System.out.println("\tIf you omit the property file path, it will look for 'database.properties' next to the JAR.");
            System.out.println("\tIf no property file is found, a default 'database.properties' will be created for you to fill in.");
            System.out.println("\nUsage:");
            System.out.println("\tjava -jar DBConnTestV2");
            System.out.println("\tjava -jar DBConnTestV2 [path/to/database.properties]");
            return;
        }

        // If user supplied a file path, use it; otherwise pass null to use the default
        String propertyFilePath;
        if ( args.length > 0 ) {
            propertyFilePath = args[0]; // use the first argument
        }
        else {
            propertyFilePath = null; // no argument, will default to database.properties
        }

        //DBConnTestV2 DBConnectionTest = new DBConnTestV2();
        DBConnTestV2 DBConnectionTest = new DBConnTestV2( propertyFilePath );
        //DBConnectionTest.start();
        
        try {
            DBConnectionTest.start();
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }


    
    //---------------------------------------------------------------------------------------------------------------#
    // Function: start
    // Description: This method initiates the database connectivity test. It reads properties from a configuration file
    //              or interacts with the user to obtain the necessary database connection information.
    //              It then validates the connection and executes default or custom queries.
    //---------------------------------------------------------------------------------------------------------------#
    public void start() {
        FileInputStream inStream = null;
        System.out.println("\tFirst checking for " + this.Propertyfile);

        try {
            inStream = new FileInputStream(this.Propertyfile);
            System.out.println("\t" + this.Propertyfile + " found. Reading properties\n\n");
            //properties.load(inStream);
            Load_Or_Prompt_Properties();
        } catch (Exception ex5) {
            System.out.println("\t" + this.Propertyfile + " not found. Switching to interactive mode \n\n");
            this.Need_New_Properties = true;
            // After the user enters in values, it will create a new properties file
        }


        this.DB_Vendor = DB_ConnTest_Properties.getProperty("DB_VENDOR");
        this.DB_Host = DB_ConnTest_Properties.getProperty("DB_HOST");
        this.DB_Port = DB_ConnTest_Properties.getProperty("DB_PORT");
        this.DB_Data = DB_ConnTest_Properties.getProperty("DB_DATA");
        this.DB_User = DB_ConnTest_Properties.getProperty("DB_USER");
        this.DB_Password = DB_ConnTest_Properties.getProperty("DB_PASS");
        this.DB_Driver_Path = DB_ConnTest_Properties.getProperty("DRIVER_PATH");
        this.TrustStore_Path = DB_ConnTest_Properties.getProperty("TRUSTSTORE_PATH");
        this.TrustStore_Password = DB_ConnTest_Properties.getProperty("TRUSTSTORE_PASSWORD");
        this.SSLTLS = DB_ConnTest_Properties.getProperty("SSLTLS");
        this.J2SSE_OVERRIDE_TLS = DB_ConnTest_Properties.getProperty("J2SSE_OVERRIDE_TLS");
        this.Oracle_Service_Name = DB_ConnTest_Properties.getProperty("Oracle_Service_Name");


        try {
            while (this.DB_Vendor == null || !this.DB_Vendor.equals("Oracle") && !this.DB_Vendor.equals("MSSQL") && !this.DB_Vendor.equals("DB2") && !this.DB_Vendor.equals("DB2i_app") && !this.DB_Vendor.equals("DB2i_toolbox")) {
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
            this.DB_Password = prompt(this.DB_Password, "DB Password");
            this.DB_Driver_Path = prompt(this.DB_Driver_Path, "Driver Path");
            if (this.SSLTLS == null) {
                this.SSLTLS = "false";
            }

            if (this.SSLTLS.equals("true")) {
                this.TrustStore_Path = prompt(this.TrustStore_Path, "Truststore path");
                this.TrustStore_Password = prompt(this.TrustStore_Password, "Truststore password");
            }

            System.out.println();
            System.out.println("\n\n Will attempt to Validate connectivity using the following properties: ");
            System.out.println(" Type:\t " + this.DB_Vendor);
            System.out.println(" Host:\t " + this.DB_Host);
            if (this.DB_Port != null && this.DB_Port.length() != 0) {
                System.out.println(" Port:\t " + this.DB_Port);
            }

            System.out.println(" Name:\t " + this.DB_Data);
            System.out.println(" User:\t " + this.DB_User);
            System.out.println(" Pass:\t " + this.DB_Password);
            if (this.DB_Driver_Path != null) {
                System.out.println(" Driver Path:\t " + this.DB_Driver_Path);
            }

            if (this.J2SSE_OVERRIDE_TLS != null) {
                System.out.println(" overrideDefaultTLS:\t " + this.J2SSE_OVERRIDE_TLS);
            }

            if (this.SSLTLS != null && this.SSLTLS.equals("true")) {
                System.out.println();
                System.out.println(" SSLTLS: " + this.SSLTLS);
                System.out.println(" Truststore Path: " + this.TrustStore_Path);
                System.out.println(" Truststore Password: " + this.TrustStore_Password);
                System.out.println();
            }

            if (this.Oracle_Service_Name == null) {
                this.Oracle_Service_Name = "No";
                //System.out.println(" Use Oracle Service Name? \t " + this.Oracle_Service_Name);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    ((FileInputStream) inStream).close();
                } catch (IOException ex2) {
                    ex2.printStackTrace();
                }
            }

        }


        try {

            if ( this.Need_New_Properties ) {
                this.Create_Example_Properties_File();
            }


            if (this.DB_Vendor.equals("Oracle")) {
                System.out.print("\n Use the catalog name as an Oracle Service Name instead of SID? (y/n) ");

                for (this.Oracle_Service_Name = ReadString(); !this.Oracle_Service_Name.equalsIgnoreCase("Y") && !this.Oracle_Service_Name.equalsIgnoreCase("yes") && !this.Oracle_Service_Name.equalsIgnoreCase("N") && !this.Oracle_Service_Name.equalsIgnoreCase("no"); this.Oracle_Service_Name = ReadString()) {
                    System.out.print("Please answer 'yes' or 'no' ");
                }

                if (this.Oracle_Service_Name.equalsIgnoreCase("Y") || this.Oracle_Service_Name.equalsIgnoreCase("YES")) {
                    this.Oracle_Service_Name = "true";
                }
            }

            this.URL = new URL("jar:file:" + this.DB_Driver_Path + "!/");
            // var19 = URL
            // var6 = DB_Driver
            if (this.DB_Vendor.equals("Oracle")) {
                this.DB_Driver = "oracle.jdbc.OracleDriver";
                if (this.Oracle_Service_Name != null && this.Oracle_Service_Name.equals("true")) {
                    this.Connection_String = "jdbc:oracle:thin:@" + this.DB_Host + ":" + this.DB_Port + "/" + this.DB_Data;
                } else {
                    this.Connection_String = "jdbc:oracle:thin:@" + this.DB_Host + ":" + this.DB_Port + ":" + this.DB_Data;
                }
            }

            if (this.DB_Vendor.equals("MSSQL")) {
                this.DB_Driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                this.Connection_String = "jdbc:sqlserver://" + this.DB_Host + ":" + this.DB_Port + ";DatabaseName=" + this.DB_Data;
            }

            if (this.DB_Vendor.equals("DB2")) {
                this.DB_Driver = "com.ibm.db2.jcc.DB2Driver";
                this.Connection_String = "jdbc:db2://" + this.DB_Host + ":" + this.DB_Port + "/" + this.DB_Data;
            }

            if (this.DB_Vendor.equals("DB2i_app")) {
                this.DB_Driver = "com.ibm.db2.jdbc.app.DB2Driver";
                this.Connection_String = "jdbc:db2://" + this.DB_Host + "/" + this.DB_Data;
            }

            if (this.DB_Vendor.equals("DB2i_toolbox")) {
                this.DB_Driver = "com.ibm.as400.access.AS400JDBCDriver";
                this.Connection_String = "jdbc:as400://" + this.DB_Host + "/" + this.DB_Data;
            }

            if (this.DB_Driver_Path != null) {
                URLClassLoader URL_Class = new URLClassLoader(new URL[]{this.URL});
                Driver DriverInstance = (Driver) Class.forName(this.DB_Driver, true, URL_Class).newInstance();
                DriverManager.registerDriver(new DriverShim(DriverInstance));
            }

            Properties Property_Values = new Properties();
            Property_Values.setProperty("user", this.DB_User);
            Property_Values.setProperty("password", this.DB_Password);
            if (this.J2SSE_OVERRIDE_TLS != null && this.J2SSE_OVERRIDE_TLS.equals("true")) {
                System.setProperty("com.ibm.jsse2.overrideDefaultTLS", "true");
            }

            if (this.SSLTLS != null && this.SSLTLS.equals("true")) {
                Property_Values.setProperty("javax.net.ssl.trustStoreType", "JKS");
                Property_Values.setProperty("javax.net.ssl.trustStore", this.TrustStore_Path);
                Property_Values.setProperty("javax.net.ssl.trustStorePassword", this.TrustStore_Password);
                Connection_String = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST =(ADDRESS=(PROTOCOL=TCPS)(HOST=" + this.DB_Host + ")(PORT=" + this.DB_Port + ")))(CONNECT_DATA=(SERVICE_NAME=orcl)))";
                this.DB_Connection = DriverManager.getConnection(Connection_String, Property_Values);
                System.out.println("\n\tOracle JDBC URL: \t" + Connection_String);
            } else {
                this.DB_Connection = DriverManager.getConnection(Connection_String, Property_Values);
            }

            System.out.println("\n\tConnection successful. \n");
            //System.out.print("Run default query? (y/n) ");
            //String User_Answer = ReadString();
            //if (User_Answer.equals("y") || User_Answer.equals("y") || User_Answer.equals("Yes") || User_Answer.equals("YES")) {
                //siVersion();
            Test_Connection_Metadata();
            Test_Simple_Date_Query();
            //}

            System.out.println();
            System.out.print("Run custom query? (y/n) ");
            String User_Answer = ReadString();
            if (User_Answer.equals("n") || User_Answer.equals("N") || User_Answer.equals("No") || User_Answer.equals("NO")) {
                System.out.println("'No' entered.  Exiting");
                return;
            }

            if (!User_Answer.equals("y") && !User_Answer.equals("y") && !User_Answer.equals("Yes") && !User_Answer.equals("YES")) {
                System.out.println("Input unrecognized.  Exiting");
                return;
            }

            System.out.print("Please enter the custom query: ");
            String User_SQL = ReadString();
            customQuery(User_SQL);
        } catch (Exception ex ) {
            ex.printStackTrace();
        } finally {
            if ( this.Need_New_Properties ) {
                this.Create_Example_Properties_File();
            }
            System.out.println("\nConnectivity Test Completed\n\n");
        }

    }

    //---------------------------------------------------------------------------------------------------------------#  
    // Function: loadOrPromptProperties  
    // Description: Load from disk if available; otherwise prompt interactively and set Need_New_Properties.  
    //---------------------------------------------------------------------------------------------------------------#  
    private void Load_Or_Prompt_Properties() throws IOException {
        try (FileInputStream fis = new FileInputStream( this.Propertyfile ) ) {
            this.DB_ConnTest_Properties.load( fis );
            System.out.println("Loaded properties from " + this.Propertyfile );
        } catch ( Exception e ) {
            System.out.println( "Error loading properties file " + this.Propertyfile );
            //e.printStackTrace();
            System.out.println( "" + e.getMessage() );
            Need_New_Properties = true;
        }
        
    }


    //---------------------------------------------------------------------------------------------------------------#
    // Function: prompt
    // Description: This method prompts the user to enter a value for a specific variable. It ensures that the value
    //              entered is not null or empty.
    // Parameters:
    //   - Variable_Value: The current value of the variable.
    //   - Prompt: The message displayed to prompt the user for input.
    // Returns:
    //   - String: The value entered by the user.
    //---------------------------------------------------------------------------------------------------------------#
    public String prompt(String Variable_Value, String Prompt) throws Exception {
        while (Variable_Value == null || Variable_Value.length() == 0) {
            System.out.print(" " + Prompt + ": ");
            Variable_Value = ReadString();
        }

        return Variable_Value;
    }

    //---------------------------------------------------------------------------------------------------------------#
    // Function: siVersion
    // Description: This method executes a default query to retrieve product label and build number information
    //              from the database and prints the results.
    // Throws:
    //   - SQLException: If there is an error executing the SQL query.
    //---------------------------------------------------------------------------------------------------------------#
    public void siVersion() throws SQLException {
        String sql = "SELECT PRODUCT_LABEL, BUILD_NUMBER FROM SI_VERSION WHERE PRODUCT_LABEL='SI'";
        System.out.println("\nQuerying build number... " + sql);

        try (Statement SQL_Statement = this.DB_Connection.createStatement();
             ResultSet resultSet = SQL_Statement.executeQuery(sql)) {
            System.out.println("\n\tProduct Label\tBuild Number");

            while (resultSet.next()) {
                String productLabel = resultSet.getString("PRODUCT_LABEL");
                String buildNumber = resultSet.getString("BUILD_NUMBER");
                System.out.printf("\t%-15s\t%-15s%n", productLabel, buildNumber);
            }
        } catch (SQLException ex) {
            System.err.println("Error executing SQL query: " + ex.getMessage());
            throw ex; // Rethrow the exception for higher-level handling if necessary
        }
    }


    //---------------------------------------------------------------------------------------------------------------#
    // Function: customQuery
    // Description: This method executes a custom SQL query provided by the user and prints the results.
    // Parameters:
    //   - sql: The custom SQL query to execute.
    // Throws:
    //   - SQLException: If there is an error executing the SQL query.
    //---------------------------------------------------------------------------------------------------------------#
    public void customQuery(String SQL) throws SQLException {
        try (Statement statement = this.DB_Connection.createStatement()) {
            System.out.println("\nRunning custom query... " + SQL + "\n");
            try (ResultSet results = statement.executeQuery(SQL)) {
                ResultSetMetaData metaData = results.getMetaData();
                int columnCount = metaData.getColumnCount();

                if (columnCount > 1) {
                    results.next();
                    StringBuilder columnNames = new StringBuilder();
                    for (int i = 1; i <= columnCount; ++i) {
                        columnNames.append("\t ").append(metaData.getColumnName(i));
                    }
                    System.out.println(columnNames);
                }

                while (results.next()) {
                    StringBuilder row = new StringBuilder();
                    for (int j = 1; j <= columnCount; ++j) {
                        row.append("\t ").append(results.getString(j));
                    }
                    System.out.println(row);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error executing SQL query: " + ex.getMessage());
            throw ex; // Rethrow the exception for higher-level handling if necessary
        }
    }


    //---------------------------------------------------------------------------------------------------------------#
    // Function: ReadString
    // Description: This method reads a string input from the user through the console.
    // Returns:
    //   - String: The string input by the user.
    //---------------------------------------------------------------------------------------------------------------#
    public String ReadString() {
        //BufferedReader Read_Buffer = new BufferedReader(new InputStreamReader(System.in));
        String Line = null;

        try {
            Line = Read_Buffer.readLine();
        } catch (IOException ex) {
            System.out.println("IO error trying to read input" + ex.getMessage());
        }

        return Line;
    }


    //---------------------------------------------------------------------------------------------------------------#
    // Function: Test_Connection_Metadata
    // Description: Uses JDBC metadata to print the product name and version.
    // Throws: SQLException if something goes wrong.
    //---------------------------------------------------------------------------------------------------------------#
    private void Test_Connection_Metadata() throws SQLException {
        DatabaseMetaData meta = DB_Connection.getMetaData();
        String productName    = meta.getDatabaseProductName();
        String productVersion = meta.getDatabaseProductVersion();
        // collapse any newlines into a single space
        productName = productName.replaceAll("\\r?\\n", " ");
        productVersion = productVersion.replaceAll("\\r?\\n", " ");
        System.out.printf("Successfully Connected to:\n\t%s\n\t%s\n", productName, productVersion );
    }

    //---------------------------------------------------------------------------------------------------------------#
    // Function: Test_Simple_Date_Query
    // Description: Runs a tiny date query in a vendor‐specific way to retrieve the current date/time.
    // Throws: SQLException if something goes wrong.
    //---------------------------------------------------------------------------------------------------------------#
    private void Test_Simple_Date_Query() throws SQLException {
        String dateSql;
        switch (DB_Vendor) {
        case "Oracle":
            // Oracle: SYSDATE returns current date+time
            dateSql = "SELECT SYSDATE FROM DUAL";
            break;
        case "DB2":
            // DB2: CURRENT TIMESTAMP returns date+time
            dateSql = "SELECT CURRENT TIMESTAMP FROM SYSIBM.SYSDUMMY1";
            break;
        case "MSSQL":
            // SQL Server: GETDATE() returns date+time
            dateSql = "SELECT GETDATE()";
            break;
        default:
            // ANSI SQL: CURRENT_TIMESTAMP should work on most modern databases
            dateSql = "SELECT CURRENT_TIMESTAMP";
            break;
        }

        try (Statement stmt = DB_Connection.createStatement(); ResultSet rs = stmt.executeQuery(dateSql)) {
            if (rs.next()) {
                // Use getTimestamp to cover both date and time
                java.sql.Timestamp now = rs.getTimestamp(1);
                System.out.println( "\tDatabase Current Date/Time: " + now );
            }
        }
    }

    //---------------------------------------------------------------------------------------------------------------#
    // Function: createExamplePropertiesFile
    // Description: This method creates an example dbconntest.properties file with default values and comments.
    // Parameters: None
    // Returns: None
    //---------------------------------------------------------------------------------------------------------------#
    public void Create_Example_Properties_File() {
        int Length_Comment = 85;
        StringBuilder CommentBuilder = new StringBuilder();

        for (int i = 0; i < Length_Comment; i++) {
            CommentBuilder.append('#');
        }
        String Comment = CommentBuilder.toString();

        try {
            PrintWriter Property_Line = new PrintWriter( this.Propertyfile, "UTF-8");

            Property_Line.println( Comment );
            Property_Line.println( this.CommentLine( "DBConnTest Properties File", Length_Comment ) );
            Property_Line.println( Comment );
            Property_Line.println( );
            Property_Line.println( Comment );
            Property_Line.println( this.CommentLine( "DB_VENDOR: Database Vendor Type", Length_Comment ) );
            Property_Line.println( this.CommentLine( "Valid Options: DB2, MSSQL, Oracle, DB2i_app, DB2i_toolbox", Length_Comment ) );
            Property_Line.println( Comment );
            Property_Line.println( "DB_VENDOR=" + this.DB_Vendor );
            Property_Line.println( );
            Property_Line.println( Comment );
            Property_Line.println( this.CommentLine( "DB_HOST:", Length_Comment ) );
            Property_Line.println( this.CommentLine( "Valid Options: IP or Host Name of Database Server", Length_Comment ) );
            Property_Line.println( Comment );
            Property_Line.println( "DB_HOST=" + this.DB_Host );
            Property_Line.println( );
            Property_Line.println( Comment );
            Property_Line.println( this.CommentLine( "DB_PORT:", Length_Comment ) );
            Property_Line.println( this.CommentLine( "Valid Options: TCP/IP Database Port", Length_Comment ) );
            Property_Line.println( Comment );
            Property_Line.println( "DB_PORT=" + this.DB_Port );
            Property_Line.println( );
            Property_Line.println( Comment );
            Property_Line.println( this.CommentLine( "DB_DATA:", Length_Comment ) );
            Property_Line.println( this.CommentLine( "Database Catalogue Name", Length_Comment ) );
            Property_Line.println( this.CommentLine( "(SID or ServiceName in Oracle)", Length_Comment ) );
            Property_Line.println( Comment );
            Property_Line.println( "DB_DATA=" + this.DB_Data );
            Property_Line.println( );
            Property_Line.println( Comment );
            Property_Line.println( this.CommentLine( "DB_USER:", Length_Comment ) );
            Property_Line.println( this.CommentLine( "Database User", Length_Comment ) );
            Property_Line.println( Comment );
            Property_Line.println( "DB_USER=" + this.DB_User );
            Property_Line.println( );
            Property_Line.println( Comment );
            Property_Line.println( this.CommentLine( "DB_PASS:", Length_Comment ) );
            Property_Line.println( this.CommentLine( "Database Password", Length_Comment ) );
            Property_Line.println( Comment );
            Property_Line.println( "DB_PASS=" + this.DB_Password );
            Property_Line.println( );
            Property_Line.println( Comment );
            Property_Line.println( this.CommentLine( "DRIVER_PATH: Path to the JDBC driver", Length_Comment ) );
            Property_Line.println( this.CommentLine( "Examples:", Length_Comment ) );
            Property_Line.println( this.CommentLine( "", Length_Comment ) );
            Property_Line.println( this.CommentLine( "Linux", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DRIVER_PATH=/opt/Sterling/drivers/mssql/mssql-jdbc-6.4.0.jre8.jar", Length_Comment ) );
            Property_Line.println( this.CommentLine( "Windows - Use escape characters", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DRIVER_PATH=C:\\IBM\\install\\jdbc\\MSSQL\\mssql-jdbc-6.4.0.jre8.jar", Length_Comment ) );
            Property_Line.println( Comment );
            Property_Line.println( "DRIVER_PATH=" + this.DB_Driver );
            Property_Line.println( );
            Property_Line.println( Comment );
            Property_Line.println( this.CommentLine( "SSLTLS (Optional): Whether to use SSL / TLS", Length_Comment ) );
            Property_Line.println( this.CommentLine( "Examples:", Length_Comment ) );
            Property_Line.println( this.CommentLine( "", Length_Comment ) );
            Property_Line.println( this.CommentLine( "SSLTLS=true", Length_Comment ) );
            Property_Line.println( this.CommentLine( "SSLTLS=false (default)", Length_Comment ) );
            Property_Line.println( Comment );
            Property_Line.println( "SSLTLS=" + this.SSLTLS );
            Property_Line.println( );
            Property_Line.println( Comment );
            Property_Line.println( this.CommentLine( "TRUSTSTORE_PATH (Optional): Path to the Truststore", Length_Comment ) );
            Property_Line.println( this.CommentLine( "                (Required) if SSLTLS=true", Length_Comment ) );
            Property_Line.println( this.CommentLine( "", Length_Comment ) );
            Property_Line.println( this.CommentLine( "Examples:", Length_Comment ) );
            Property_Line.println( this.CommentLine( "", Length_Comment ) );
            Property_Line.println( this.CommentLine( "Linux", Length_Comment ) );
            Property_Line.println( this.CommentLine( "TRUSTSTORE_PATH=/opt/Sterling/Utils/truststore.jks", Length_Comment ) );
            Property_Line.println( this.CommentLine( "Windows - Use escape characters", Length_Comment ) );
            Property_Line.println( this.CommentLine( "TRUSTSTORE_PATH=C:\\IBM\\Utils\\truststore.jks", Length_Comment ) );
            Property_Line.println( Comment );
            Property_Line.println( "TRUSTSTORE_PATH=" + this.TrustStore_Path );
            Property_Line.println( );
            Property_Line.println( Comment );
            Property_Line.println( this.CommentLine( "TRUSTSTORE_PASSWORD (Optional): Truststore Password", Length_Comment ) );
            Property_Line.println( this.CommentLine( "                (Required) if SSLTLS=true", Length_Comment ) );
            Property_Line.println( this.CommentLine( "", Length_Comment ) );
            Property_Line.println( this.CommentLine( "Examples:", Length_Comment ) );
            Property_Line.println( this.CommentLine( "", Length_Comment ) );
            Property_Line.println( this.CommentLine( "TRUSTSTORE_PASSWORD=Wdf4Cn5Jg0IDV$!dbYwE", Length_Comment ) );
            Property_Line.println( Comment );
            Property_Line.println( "TRUSTSTORE_PATH=" + this.TrustStore_Password );
            Property_Line.println( );
            Property_Line.println( Comment );
            Property_Line.println( this.CommentLine( "J2SSE_OVERRIDE_TLS (Optional): Indicating whether to override default TLS settings.", Length_Comment ) );
            Property_Line.println( this.CommentLine( "Examples:", Length_Comment ) );
            Property_Line.println( this.CommentLine( "", Length_Comment ) );
            Property_Line.println( this.CommentLine( "J2SSE_OVERRIDE_TLS=true", Length_Comment ) );
            Property_Line.println( Comment );
            Property_Line.println( "J2SSE_OVERRIDE_TLS=" + this.J2SSE_OVERRIDE_TLS );
            Property_Line.println( );


            Property_Line.println( "" );
            Property_Line.println( "" );
            Property_Line.println( "" );
            Property_Line.println( "" );
            Property_Line.println( "" );
            Property_Line.println( Comment );
            Property_Line.println( this.CommentLine( "Examples", Length_Comment ) );
            Property_Line.println( this.CommentLine( "", Length_Comment ) );
            Property_Line.println( Comment );
            Property_Line.println( this.CommentLine( "", Length_Comment ) );
            Property_Line.println( this.CommentLine( "", Length_Comment ) );

            Property_Line.println( Comment );
            Property_Line.println( this.CommentLine( "DB_VENDOR=Oracle", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_HOST=192.168.1.3", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_PORT=1521", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_DATA=SERVER.COMPANY.COM  (Service Name example)", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_USER=sterling", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_PASS=password", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DRIVER_PATH=C:\\ibm\\drivers\\oracle\\ojdbc7.jar", Length_Comment ) );
            Property_Line.println( this.CommentLine( "", Length_Comment ) );
            Property_Line.println( this.CommentLine( "", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_VENDOR=Oracle", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_HOST=192.168.1.4", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_PORT=1521", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_DATA=SERVER12C (SID example)", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_USER=sterling", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_PASS=password", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DRIVER_PATH=/opt/Sterling/drivers/oracle/ojdbc8.jar", Length_Comment ) );
            Property_Line.println( this.CommentLine( "SSLTLS=true", Length_Comment ) );
            Property_Line.println( this.CommentLine( "J2SSE_OVERRIDE_TLS=true", Length_Comment ) );
            Property_Line.println( this.CommentLine( "", Length_Comment ) );
            Property_Line.println( this.CommentLine( "", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_VENDOR=DB2", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_HOST=192.168.1.5", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_PORT=50001", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_DATA=dbo", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_USER=sterling", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_PASS=password", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DRIVER_PATH=/opt/Sterling/drivers/db2/db2jcc.jar", Length_Comment ) );
            Property_Line.println( this.CommentLine( "", Length_Comment ) );
            Property_Line.println( this.CommentLine( "", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_VENDOR=DB2i_toolbox", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_HOST=192.168.1.1", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_PORT=8471", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_DATA=B2Bi_52", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_USER=SI_USER", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_PASS=password", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DRIVER_PATH=C:\\ibm\\drivers\\iseries\\jt400.jar", Length_Comment ) );
            Property_Line.println( this.CommentLine( "", Length_Comment ) );
            Property_Line.println( this.CommentLine( "", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_VENDOR=DB2i_app", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_HOST=192.168.1.2", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_DATA=B2Bi_526", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_USER=sterling", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_PASS=password", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DRIVER_PATH=/opt/Sterling/drivers/db2i/db2_classes16.jar", Length_Comment ) );
            Property_Line.println( this.CommentLine( "", Length_Comment ) );
            Property_Line.println( this.CommentLine( "", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_VENDOR=MSSQL", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_HOST=192.168.1.6", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_PORT=1433", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_DATA=SI_USER", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_USER=SI_USER", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DB_PASS=password", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DRIVER_PATH=/opt/Sterling/drivers/mssql/mssql-jdbc-6.4.0.jre8.jar", Length_Comment ) );
            Property_Line.println( this.CommentLine( "DRIVER_PATH=C:\\IBM\\Utils\\truststore.jks", Length_Comment ) );
            Property_Line.println( this.CommentLine( "TRUSTSTORE_PASSWORD=Wdf4Cn5Jg0IDV$!dbYwE", Length_Comment ) );
            Property_Line.println( this.CommentLine( "", Length_Comment ) );
            Property_Line.println( this.CommentLine( "", Length_Comment ) );
            Property_Line.println( Comment );
            Property_Line.println( "" );
            Property_Line.println( "" );

            Property_Line.close();


            System.out.println("\nYour entered values have been used to create a new " + this.Propertyfile );
        } catch (IOException e) {
            System.err.println("Error creating example properties file: " + e.getMessage());
            // You might choose to handle or log the exception here
        }
    }

    //---------------------------------------------------------------------------------------------------------------#
    // Function: commentLine
    // Description: This method creates a comment line for the properties file with a specified length.
    // Parameters:
    //   - line: The content of the comment line.
    //   - length: Length of the comment line.
    // Returns:
    //   - String: The generated comment line.
    //---------------------------------------------------------------------------------------------------------------#
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

