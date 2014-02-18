package net.firejack.platform.web.security.filter.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IAccessDeniedHandler {

    /**
     * @param httpRequest income http request
     * @param httpResponse outgoing http response
     * @throws java.io.IOException method may produce IOException
     */
    void onAccessDenied(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException;

}