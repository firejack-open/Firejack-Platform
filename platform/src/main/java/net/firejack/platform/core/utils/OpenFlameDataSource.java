/*
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.core.utils;

import net.firejack.platform.core.model.registry.DatabaseName;
import net.firejack.platform.core.model.registry.DatabaseProtocol;
import net.firejack.platform.core.utils.db.DBUtils;
import net.firejack.platform.utils.OpenFlameConfig;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Enumeration;


public class OpenFlameDataSource extends BasicDataSource {

    protected static Logger logger = Logger.getLogger(OpenFlameDataSource.class);

	private DatabaseName name;
    private String sid;
    private String schema;

    /***/
    public OpenFlameDataSource() {
        if (ConfigContainer.isAppInstalled()) {
            refreshDBProperties();
        }
    }

	public DatabaseName getName() {
		return name;
	}

	public void setName(DatabaseName name) {
		this.name = name;
	}

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    /***/
    public void refreshDBProperties() {
        url = OpenFlameConfig.DB_URL.getValue();
        password = OpenFlameConfig.DB_USER_PASSWORD.getValue();
        username = OpenFlameConfig.DB_USER_NAME.getValue();
    }

	public DataSource database() {
		String user = OpenFlameConfig.DB_USER_NAME.getValue();
		String password = OpenFlameConfig.DB_USER_PASSWORD.getValue();
		String host = OpenFlameConfig.DB_HOST.getValue();
		String port = OpenFlameConfig.DB_PORT.getValue();

		return convert(host, port, null, user, password);
	}

	public DataSource schema() {
		String user = OpenFlameConfig.DB_USER_NAME.getValue();
		String password = OpenFlameConfig.DB_USER_PASSWORD.getValue();
		String host = OpenFlameConfig.DB_HOST.getValue();
		String port = OpenFlameConfig.DB_PORT.getValue();
		String schema = OpenFlameConfig.DB_SCHEMA.getValue();

		return convert(host, port, schema, user, password);
	}

	/**
	 * @param host
	 * @param port
	 * @param schema
	 * @param user
	 * @param password
	 * @return
	 */
	public DataSource convert(String host, String port, String schema, String user, String password) {
		if (StringUtils.isBlank(host) || StringUtils.isBlank(port)) {
			return null;
		}
		String dbUrl;
		if (StringUtils.isNotBlank(schema)) {
			dbUrl = DatabaseName.MySQL.getDbSchemaUrlConnection(DatabaseProtocol.JDBC, host, port, schema);
		} else {
			dbUrl = DatabaseName.MySQL.getDbUrlConnection(DatabaseProtocol.JDBC, host, port);
		}
		return DBUtils.populateDataSource(getDriverClassName(), dbUrl, user, password);
	}

	public static boolean ping(DatabaseName type, String host, String port, String user, String password, String sid, String schema) {
		try {
			Class.forName(type.getDriver());
			String url;
			if (type == DatabaseName.MySQL) {
				url = type.getDbUrlConnection(DatabaseProtocol.JDBC, host, port);
			} else {
				url = type.getDbSchemaUrlConnection(DatabaseProtocol.JDBC, host, port, sid, schema);
			}

			Connection connection = DriverManager.getConnection(url, user, password);
			if (connection == null) {
				return false;
			}

			Statement statement = connection.createStatement();
			statement.execute(type.getValidate());
			connection.close();
			return true;
		} catch (Exception e) {
			logger.error("Ping to db has failed.");
			return false;
		}
	}

	/**
     * Creates a JDBC connection factory for this datasource.  This method only
     * exists so subclasses can replace the implementation class.
     */
    protected ConnectionFactory createConnectionFactory() throws SQLException {
        try {
            return super.createConnectionFactory();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public synchronized void close() throws SQLException {
        super.close();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            DriverManager.deregisterDriver(drivers.nextElement());
        }
    }
}
