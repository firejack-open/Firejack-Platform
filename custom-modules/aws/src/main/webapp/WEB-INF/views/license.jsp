<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--
  ~ Firejack Platform - Copyright (c) 2012 Firejack Technologies
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
        <script type="text/javascript" src="js/jquery-1.10.2.min.js"></script>
        <script type="text/javascript" src="js/jquery-ui.js"></script>
        <script type="text/javascript" src="js/license.js"></script>
        <link rel="stylesheet" type="text/css" href="css/jquery-ui.css"/>
        <link rel="stylesheet" type="text/css" href="css/aop.css"/>
    </head>
    <body>
        <div id="error"></div>
        <form id="licenseForm">
            <div class="form-field">
                <label>Password:</label>
                <input name="accessPassword" type="text"/>
            </div>
            <hr>
            <div class="form-field">
                <label>Name:</label>
                <input name="name" type="text"/>
            </div>
            <div class="form-field">
                <label>Session:</label>
                <input name="session" type="text"/>
            </div>
            <div class="form-field">
                <label>Date:</label>
                <input id="datepicker" name="expired" type="text"/>
            </div>
            <button id="installBtn">Download <span>&rarr;</span></button>
        </form>

        <div id="successMsg" style="display: none;">
        </div>
        <div class="modal"></div>
    </body>
</html>
