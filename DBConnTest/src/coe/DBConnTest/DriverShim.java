
package coe.DBConnTest;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;


class DriverShim implements Driver {
    private Driver driver;

    DriverShim(final Driver driver) {
        this.driver = driver;
    }

    public boolean acceptsURL(final String s) throws SQLException {
        return this.driver.acceptsURL(s);
    }

    public Connection connect(final String s, final Properties properties) throws SQLException {
        return this.driver.connect(s, properties);
    }

    public int getMajorVersion() {
        return this.driver.getMajorVersion();
    }

    public int getMinorVersion() {
        return this.driver.getMinorVersion();
    }

    public DriverPropertyInfo[] getPropertyInfo(final String s, final Properties properties) throws SQLException {
        return this.driver.getPropertyInfo(s, properties);
    }

    public boolean jdbcCompliant() {
        return this.driver.jdbcCompliant();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}