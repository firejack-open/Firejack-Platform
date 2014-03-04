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
        <script type="text/javascript" src="js/aws.js"></script>
        <link rel="stylesheet" type="text/css" href="css/aop.css"/>
    </head>
    <body>
        <div id="error"></div>
        <p>Just enter your AWS access key and secret key and we will securely create an instance of the Firejack Platform for you to play with.<br>
            This instance is full-featured with a 3 session license installed - and no expiration date.<br>
            <b>*Note:</b> Instance creation can take up to 10 minutes.</p>

            <p>Installed Firejack Platform Console can be viewed at: <strong>&lsaquo;Public DNS&rsaquo;:8080/platform</strong><br>
            Installed Firejack Platform Gateway can be viewed at: <strong>&lsaquo;Public DNS&rsaquo;:8080/</strong></p>

        <form id="installForm">
            <div class="form-field">
                <label>Access Key:</label>
                <input name="accessKey" type="text"/>
            </div>
            <div class="form-field">
                <label>Secret Key:</label>
                <input name="secretKey" type="text"/>
            </div>
            <button id="installBtn">Install <span>&rarr;</span></button>
        </form>

        <div id="successMsg" style="display: none">
            <h2>The new Amazon EC2 Instance launched. The Platform installation has been started.</h2>
            <p>Save the private key file in a safe place. You'll need to provide the name of your key pair when you launch an instance and the corresponding private key each time you connect to the instance. <br>
            Now that you've launched your instance, you can connect to it and use it. For more information, see <a href="http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-connect-to-instance-linux.html" title="Connect to Your Instance" target="_blank">Connect to Your Instance</a>.</p>
        </div>
        <p><img src="images/amazon-logo.png" alt="Amazon web sevices"/></p>
        <div class="modal"></div>
    </body>
</html>
