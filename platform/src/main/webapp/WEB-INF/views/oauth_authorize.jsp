<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%--
  ~ Firejack Platform - Copyright (c) 2011 Firejack Technologies
  ~
  ~ This source code is the product of the Firejack Technologies
  ~ Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
  ~ Asanov) and licensed only under valid, executed license agreements
  ~ between Firejack Technologies and its customers. Modification and / or
  ~ re-distribution of this source code is allowed only within the terms
  ~ of an executed license agreement.
  ~
  ~ Any modification of this code voids any and all warranties and indemnifications
  ~ for the component in question and may interfere with upgrade path. Firejack Technologies
  ~ encourages you to extend the core framework and / or request modifications. You may
  ~ also submit and assign contributions to Firejack Technologies for consideration
  ~ as improvements or inclusions to the platform to restore modification
  ~ warranties and indemnifications upon official re-distributed in patch or release form.
  --%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>Firejack Technology - OpenFlame Console</title>
    </head>
    <body>
        <h1>OAuth authentication</h1><hr/>
        <br><br>
        <form action='<c:out value="${requestScope.authorizationUrl}"/>'>
            <table>
                <tr>
                    <td><label title="Account" for="login"></label></td>
                    <td><input id="login" type="text" name="login"/></td>
                </tr>
                <tr>
                    <td><label title="Password" for="password"></label></td>
                    <td><input id="password" type="text" name="password"/></td>
                </tr>
                <tr>
                    <td><input id=""/></td></td><td><input type="submit" value="Authenticate"></td>
                </tr>
            </table>
        </form>
        <table>

        </table>
    </body>
</html>