<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
