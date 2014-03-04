/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
