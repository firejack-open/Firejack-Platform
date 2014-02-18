/**
 * Firejack Open Flame - Copyright (c) 2011 Firejack Technologies
 * <p/>
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 * <p/>
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

package net.firejack.platform.service.statistics.endpoint;

import net.firejack.platform.api.statistics.domain.LogEntry;
import net.firejack.platform.api.statistics.domain.LogTransaction;
import net.firejack.platform.api.statistics.domain.MetricsEntry;
import net.firejack.platform.core.request.ServiceRequest;
import net.firejack.platform.core.response.ServiceResponse;
import org.apache.cxf.interceptor.InFaultInterceptors;
import org.apache.cxf.interceptor.InInterceptors;
import org.apache.cxf.interceptor.OutInterceptors;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


@SOAPBinding(style = SOAPBinding.Style.RPC)
@InInterceptors(interceptors = {
		"org.apache.cxf.interceptor.LoggingInInterceptor",
		"org.apache.cxf.binding.soap.saaj.SAAJInInterceptor",
		"net.firejack.platform.web.security.ws.interceptor.OpenFlameWSS4JInInterceptor",
		"net.firejack.platform.web.security.ws.interceptor.OpenFlameAuthorizingInInterceptor"})
@OutInterceptors(interceptors = {
		"org.apache.cxf.interceptor.LoggingOutInterceptor",
		"net.firejack.platform.web.security.ws.interceptor.OpenFlameWSS4JOutInterceptor"})
@InFaultInterceptors(interceptors = "org.apache.cxf.interceptor.LoggingOutInterceptor")
@WebService(endpointInterface = "net.firejack.platform.service.statistics.endpoint.IStatisticsEndpoint")
public interface IStatisticsEndpoint {

    /**
	 * Service retrieves all log entries
	 *
	 * @return list of all log tracks
	 */
	@WebMethod
    ServiceResponse<LogEntry> readAllLogEntries();

	/**
	 * Service searches the log entries by search parameters
	 *
	 * @param lookup        - lookup to search by
	 * @param term          - term to search by
	 * @param startDate     - start date of the period to search within
	 * @param startTime     - start time of the period to search within
	 * @param endDate       - end date of the period to search within
	 * @param endTime       - end time of the period to search within
	 * @param offset        - item to start listing from
	 * @param limit         - number of items to be listed
	 * @param sortColumn    - column to sort by
	 * @param sortDirection - sorting direction
	 *
	 * @return list of found log tracks
	 */
	@WebMethod
	ServiceResponse<LogEntry> searchAllLogEntries(
			@WebParam(name = "lookup") String lookup,
			@WebParam(name = "term") String term,
			@WebParam(name = "startDate") String startDate,
			@WebParam(name = "startTime") String startTime,
			@WebParam(name = "endDate") String endDate,
			@WebParam(name = "endTime") String endTime,
			@WebParam(name = "start") Integer offset,
			@WebParam(name = "limit") Integer limit,
			@WebParam(name = "sort") String sortColumn,
			@WebParam(name = "dir") String sortDirection,
            @WebParam(name = "logEntryType") String logEntryType);

	/**
	 * Service adds log entries
	 *
	 * @param request - log track data
	 *
	 * @return information about the success of the operation
	 */
	@WebMethod
	ServiceResponse saveStatisticsBunch(@WebParam(name = "request") ServiceRequest<LogEntry> request);

    /**
	 * Service searches the log transactions by search parameters
	 *
	 * @param lookup        - lookup to search by
	 * @param term          - term to search by
	 * @param level         - interval to search by
	 * @param startDate     - start date of the period to search within
	 * @param startTime     - start time of the period to search within
	 * @param endDate       - end date of the period to search within
	 * @param endTime       - end time of the period to search within
	 * @param offset        - item to start listing from
	 * @param limit         - number of items to be listed
	 * @param sortColumn    - column to sort by
	 * @param sortDirection - sorting direction
	 *
	 * @return list of found log tracks
	 */
	@WebMethod
	ServiceResponse<LogTransaction> searchAllLogTransactions(
			@WebParam(name = "lookup") String lookup,
			@WebParam(name = "term") String term,
            @WebParam(name = "level") String level,
			@WebParam(name = "startDate") String startDate,
			@WebParam(name = "startTime") String startTime,
			@WebParam(name = "endDate") String endDate,
			@WebParam(name = "endTime") String endTime,
			@WebParam(name = "start") Integer offset,
			@WebParam(name = "limit") Integer limit,
			@WebParam(name = "sort") String sortColumn,
			@WebParam(name = "dir") String sortDirection);

    /**
     * Service saves log transaction
     * @param request - log transaction data
     * @return information about the success of the operation
     */
    @WebMethod
    ServiceResponse saveLogTransaction(@WebParam(name = "request") ServiceRequest<LogTransaction> request);

    @WebMethod
	ServiceResponse<MetricsEntry> searchAllMetricsEntries(
			@WebParam(name = "startDate") String startDate,
			@WebParam(name = "startTime") String startTime,
			@WebParam(name = "endDate") String endDate,
			@WebParam(name = "endTime") String endTime,
			@WebParam(name = "term") String term,
			@WebParam(name = "level") String level,
			@WebParam(name = "lookup") String lookup,
			@WebParam(name = "start") Integer offset,
			@WebParam(name = "limit") Integer limit,
			@WebParam(name = "sort") String sortColumn,
			@WebParam(name = "dir") String sortDirection,
            @WebParam(name = "logEntryType") String logEntryType);

	@WebMethod
	ServiceResponse<MetricsEntry> findMetricsEntryByExample(@WebParam(name = "request") ServiceRequest<MetricsEntry> request);

	@WebMethod
	ServiceResponse saveMetricsEntry(@WebParam(name = "request") ServiceRequest<MetricsEntry> request);

}