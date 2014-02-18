package net.firejack.platform.web.security.filter.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface IAuthenticationSuccessHandler {

    /**
     * @param httpResponse
     * @throws java.io.IOException
     */
    void onSuccessAuthentication(HttpServletRequest httpRequest, HttpServletResponse httpResponse, List<String> notRedirectUrls) throws IOException;
}