<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%--
  ~ Licensed to the Apache Software Foundation (ASF) under one
  ~ or more contributor license agreements.  See the NOTICE file
  ~ distributed with this work for additional information
  ~ regarding copyright ownership.  The ASF licenses this file
  ~ to you under the Apache License, Version 2.0 (the
  ~ "License"); you may not use this file except in compliance
  ~ with the License.  You may obtain a copy of the License at
  ~
  ~   http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied.  See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  --%>

<%
    //String appDesc = (String)request.getAttribute("CONS_DESC");
    String token = (String)request.getAttribute("TOKEN");
    String callback = (String)request.getAttribute("CALLBACK");
    if(callback == null)
        callback = "";
    System.out.println("request.getAttribute(\"authorizationUrl\") = " + request.getAttribute("authorizationUrl"));
    System.out.println("callback = " + callback);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>Firejack Technology - Firejack Platform Console</title>
    </head>
    <body>
        <h1>OAuth authentication</h1><hr/>
        <br><br>
        <form action='<c:out value="${requestScope.authorizationUrl}"/>' method="POST" enctype="application/x-www-form-urlencoded">
            <table>
                <tr>
                    <td><label title="Account" for="login"></label></td>
                    <td><input id="login" type="text" name="login"/></td>
                </tr>
                <tr>
                    <td><label title="Password" for="password"></label></td>
                    <td><input id="password" type="password" name="password"/></td>
                </tr>
                <tr>
                    <td>
                        <input type="hidden" name="oauth_token" value="<%= token %>"/>
                        <input type="hidden" name="oauth_callback" value="<%= callback %>"/>
                    </td>
                    <td><input type="submit" value="Authenticate"></td>
                </tr>
            </table>
        </form>
        <table>

        </table>
    </body>
</html>