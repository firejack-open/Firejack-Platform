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
