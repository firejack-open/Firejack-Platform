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

package net.firejack.platform.service.statistics.broker.logentry;

import net.firejack.platform.api.statistics.domain.LogEntry;
import net.firejack.platform.core.broker.ListBroker;
import net.firejack.platform.core.exception.BusinessFunctionException;
import net.firejack.platform.core.model.registry.statistics.LogEntryModel;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.store.statistics.ILogEntryStore;
import net.firejack.platform.web.statistics.annotation.TrackDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * Class encapsulates functionality of reading the log entry list
 */
@TrackDetails
@Component("readLogEntryListBrokerEx")
public class ReadLogEntryListBroker extends ListBroker<LogEntryModel, LogEntry, LogEntry> {

    @Autowired
    @Qualifier("logEntryStore")
	private ILogEntryStore logEntryStore;

    /**
     * Get list of models according input request
     * @param request request object that intent to provide input parameters
     * @return list of LogEntryModel
     * @throws BusinessFunctionException may throw BusinessFunctionException
     */
    @Override
    protected List<LogEntryModel> getModelList(ServiceRequest<LogEntry> request)
            throws BusinessFunctionException {
        return logEntryStore.findAll();
    }

}