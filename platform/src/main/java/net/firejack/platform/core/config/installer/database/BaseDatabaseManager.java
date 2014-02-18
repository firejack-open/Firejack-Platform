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

package net.firejack.platform.core.config.installer.database;

import net.firejack.platform.core.config.installer.IDatabaseManager;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

public abstract class BaseDatabaseManager implements IDatabaseManager {

	protected static Logger logger = Logger.getLogger(BaseDatabaseManager.class);

	/**
	 * @param dataSource
	 * @param sql
	 * @return
	 */
    protected boolean executeStatement(DataSource dataSource, String sql) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		try {
            logger.info("Trying to execute sql statement: [\n" + sql + "\n]");
			template.execute(sql);
		} catch (DataAccessException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

    protected <T> List<T> query(DataSource dataSource, String sql, RowMapper<T> mapper) {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		try {
            logger.info("Trying to execute sql statement: [\n" + sql + "\n]");
			return template.query(sql, mapper);
		} catch (DataAccessException e) {
			logger.error(e.getMessage(), e);

		}
		return Collections.emptyList();
	}

}
