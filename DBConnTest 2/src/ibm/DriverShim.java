package ibm;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;



class DriverShim implements Driver {
    private Driver driver;

    DriverShim(Driver var1) {
        this.driver = var1;
    }

    public boolean acceptsURL(String var1) throws SQLException {
        return this.driver.acceptsURL(var1);
    }

    public Connection connect(String var1, Properties var2) throws SQLException {
        return this.driver.connect(var1, var2);
    }

    public int getMajorVersion() {
        return this.driver.getMajorVersion();
    }

    public int getMinorVersion() {
        return this.driver.getMinorVersion();
    }

    public DriverPropertyInfo[] getPropertyInfo(String var1, Properties var2) throws SQLException {
        return this.driver.getPropertyInfo(var1, var2);
    }

    public boolean jdbcCompliant() {
        return this.driver.jdbcCompliant();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        try {
            return driver.getParentLogger();
        } catch (UnsupportedOperationException e) {
            throw new SQLFeatureNotSupportedException("Underlying driver doesn't support getParentLogger()");
        }
    }


}