package net.firejack.platform.web.security.session;

/**
 *
 */
public interface ISessionExpirationCheckStrategy {

    /**
     * @param sessionToken
     * @return
     */
    boolean isSessionExpired(String sessionToken);

    /**
     * @param sessionToken
     * @return
     */
    boolean isSessionTokenActive(String sessionToken);

}