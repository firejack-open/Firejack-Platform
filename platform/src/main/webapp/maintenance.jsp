<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%--<link rel="stylesheet" type="text/css" href="css/style.css"/>--%>
    <style type="text/css">
        html, body {
            margin: 0;
            padding: 0;
            height: 100%;
        }
        .container {
            width: 1200px;
            min-height: 100%;
            margin: 0 auto;
            overflow: hidden;
            border: 1px solid #ccc;
            border-top: 0;
            border-bottom: 0;
        }

        .header {
            padding: 0 20px;
            background-color: #006fbf;
            *zoom: 1;
            filter: progid:DXImageTransform.Microsoft.gradient(gradientType=0, startColorstr='#FF006FBF', endColorstr='#FF00548F');
            background-image: -webkit-gradient(linear, 50% 0%, 50% 100%, color-stop(0%, #006fbf), color-stop(11%, #006bb8), color-stop(42%, #005693), color-stop(50%, #00538d), color-stop(53%, #004e85), color-stop(55%, #004c82), color-stop(87%, #004c82), color-stop(100%, #00548f));
            background-image: -webkit-linear-gradient(top, #006fbf 0%, #006bb8 11%, #005693 42%, #00538d 50%, #004e85 53%, #004c82 55%, #004c82 87%, #00548f 100%);
            background-image: -moz-linear-gradient(top, #006fbf 0%, #006bb8 11%, #005693 42%, #00538d 50%, #004e85 53%, #004c82 55%, #004c82 87%, #00548f 100%);
            background-image: -o-linear-gradient(top, #006fbf 0%, #006bb8 11%, #005693 42%, #00538d 50%, #004e85 53%, #004c82 55%, #004c82 87%, #00548f 100%);
            background-image: linear-gradient(top, #006fbf 0%, #006bb8 11%, #005693 42%, #00538d 50%, #004e85 53%, #004c82 55%, #004c82 87%, #00548f 100%);
            border-style: solid;
            border-width: 1px 0 0 0;
            border-color: #000;
            overflow: hidden;
            /* Search */
            /* End Search */
        }
        .header .side-l {
            overflow: hidden;
        }
        .header .logo {
            display: block;
            color: #fff;
            font: 15px normal Arial;
            text-decoration: none;
            overflow: hidden;
            padding-top: 3px;
        }
        .header .logo .logo-img_w32 {
            width: 32px;
            height: 24px;
            background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAYCAYAAACbU/80AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyFpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDE0IDc5LjE1MTQ4MSwgMjAxMy8wMy8xMy0xMjowOToxNSAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIChXaW5kb3dzKSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDo4NUMwOTc2RjAwM0ExMUUzOURGRkFGNEY2QURENzNBOSIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDo4NUMwOTc3MDAwM0ExMUUzOURGRkFGNEY2QURENzNBOSI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOjg1QzA5NzZEMDAzQTExRTM5REZGQUY0RjZBREQ3M0E5IiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOjg1QzA5NzZFMDAzQTExRTM5REZGQUY0RjZBREQ3M0E5Ii8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+9+/lKQAAAoxJREFUeNqcll1IVUEQx292OX5wjZISxBA06UNfoqeIwgfTQCQiEIJI8SGMfFAQUuohQwXFhAiEEPHNDxANhBApCLHsIfHFF0XsqQIrUDSwbhe3/9pcmqbdPXsd+ME5M7Ozc/bs7kxEKRXZB5mgWuhKQTTVWGmR/Uk/KBS6ADTTcw5oAbHQSCEZHjLo2tUfuSr0J8A6rY5+vwJWwWnXHFJxFLSCt2ALdIPDzH5P/ZUCMfYi6W8w3SvwBRT7JFAPNijIOgXk9kds8mVDsDayDTFdHemW2MoYE2hT/0o5czoI+oW9RQRKB2tkeyd+S1Ie2hI4CRLMcVUEHheTfzR8zQCzr4jTkhS9ulmmBO6LCdbIGIAX6n+5LoJ0Cvu8JQG5P6zHsAhUgDFQJWzPwCR77wUPhM8Key4VtsumY3gW7IpMNw1f/oZWJZl9lzLLNebTK2wLtk04qNyi/99xNvC2xW+Z3Yb5YFvYv9kS0F825kjgDhukN+2OwUdv5DJ2cl4bfOKue0APemxJYBGcJ78Ri89dFqvH4pMIuwk1PyyD9T55brH3sPG3HCv5ySeBTZWaTIADNPYC+OnwfelzDBfF+wSYAnGDbwLMggyQR76Bo/bN+FTDRpH1OOlzQINlA36lG9QlemVyfX5BjBWlZHB+/htI/x1MGY6aTZ74lGNbcapltjQwB0bpPUrVziX6fshOJYEo9QR89x5h9mPgJj0XhUwep1WapFoQ80kgQjffB7GDA4NfnWPyBWpG9LhzdIP2UdNTpattWEumk3jPAk4bSmqHYeJf9M/Tw5pbn841oJK9wbqbM8z+VNx0em+c8u2K9y4QT9Edbg2oBCVgGAyAJnAJTINR8DmV9vq3AAMAD1WqIMGMo84AAAAASUVORK5CYII=');
            margin-right: 5px;
            top: -3px;
            position: relative;
        }
        .header .logo span {
            display: block;
            float: left;
        }

        .main {
            padding-bottom: 70px;
        }

        .message {
            width: 560px;
            margin: 40px auto 0;
            border: 1px solid #ddd;
            -webkit-border-radius: 5px;
            -moz-border-radius: 5px;
            border-radius: 5px;
            padding: 30px 20px;
            background-color: #FDFCFC;
        }
        h1 {
            text-align: center;
        }
        h1 span {
            display: block;
            color: #005693;
        }

        .footer {
            background: #34495e;
            position: relative;
            width: 1200px;
            margin: -70px auto 0;
            height: 70px;
        }

        .footer-inner {
            overflow: hidden;
            padding: 13px 20px;
        }

        .footer .logo {
            display: block;
            color: #fff;
            font: 15px normal Arial;
            text-decoration: none;
            overflow: hidden;
            padding-top: 3px;
        }
        .footer .logo-img_w26 {
            width: 26px;
            height: 20px;
            background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABoAAAAUCAYAAACTQC2+AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyFpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDE0IDc5LjE1MTQ4MSwgMjAxMy8wMy8xMy0xMjowOToxNSAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIChXaW5kb3dzKSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDpCNkFCRDZDMTBCMzExMUUzQTc3NEQ2QjI1N0UyMTlFNiIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDpCNkFCRDZDMjBCMzExMUUzQTc3NEQ2QjI1N0UyMTlFNiI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOkI2QUJENkJGMEIzMTExRTNBNzc0RDZCMjU3RTIxOUU2IiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOkI2QUJENkMwMEIzMTExRTNBNzc0RDZCMjU3RTIxOUU2Ii8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+E5RcSQAAAgZJREFUeNqUldsrZVEcx7fb4IxEmeTS8cA0kZkkkuur1Dx4knKJ1KTIH8A0yTx4UuRhpDwopSQlnVyKPKtxlIej1OSSeBB1yL2278pvN99Zrb3Pnl99Tut8f2ut316338+ybdv6DyrAJ/rf4HdsvOXfMsACuCGtBZRIOx2kuQ3mQEEwCvZACBSTLyBaNrgg/QPokXYUdIEkYyRZWj+4t99sDMTTslPBqvjWSY8Dp+A3ae1gyLR16qfD/mv7IIE6BMAG+dvJ1yTaDWmfwTV4bwoUoYkWtCDb5FP9EsWXDv6IHqUxGaK1mgJd0mSqnQneyTY59kI3TPk2yXdAEyaJ9ssUaM7+19R5TGjadzoXvf8MTZgp2o4pUC44tt0tJAFU30GDv54m7BXt1BRIUaCdlWMPIF/6lIJnzb9CkxWBK9Fv3QJZclMmDcF2wVcwq+nqq3PoQ3lX7rwCObgZr+YRVEn/j4atP/aTgvjlz4NDaSeSvglOQCHYkqzCFnbLDMyi4YFW0v479iRnYbJuP1vXTAOmSR8W7Qf45nFTz+SxxwykUlCY0ksWZYNz8AXUeJzlBRgHtfQsLLf6US0HrmxJqz958khNNiLZoQx0ggHQCIJexapNUo+yn5rv0PDe+rwKX6zKWAeOZLIpkCz6nZaySmJVWD9lOEVu0TJYk5QTkcRZ7reUvwowAMKy7ZpkO2TkAAAAAElFTkSuQmCC');
            margin-right: 5px;
            top: -3px;
            position: relative;
        }
        .footer .logo span {
            display: block;
            float: left;
        }

        .footer .build {
            color: #7d98b2;
            padding-left: 32px;
        }

        .footer .col.left {
            float: left;
        }
        .footer .col.right {
            float: right;
            padding-top: 12px;
        }

            /* Copyright */
        .footer .copyright {
            color: #fff;
        }
    </style>
    <title></title>
</head>
<body>
    <div class="container">
        <div class="header">
            <a class="logo" href="http://www.firejack.net" title="Firejack Platform">
                <span class="logo-img_w32"></span>
                <span>Firejack Platform</span>
            </a>
        </div>
        <div class="main">
            <div class="message">
                <h1>Sorry! This site is currently <span>Under Construction</span></h1>
            </div>
        </div>
    </div>
    <div class="footer">
        <div class="footer-inner">
            <div class="col left">
                <a class="logo" href="http://www.firejack.net" title="Firejack Platform">
                    <span class="logo-img_w26"></span>
                    <span>Firejack Platform</span>
                </a>
                <div class="build" id="ext-gen1147">
                    <span>build 4533</span>
                </div>
            </div>
            <div class="col right">
                <div class="copyright">Copyright © 2010 - 2013 - Firejack Technologies</div>
            </div>
        </div>
    </div>
</body>
</html>