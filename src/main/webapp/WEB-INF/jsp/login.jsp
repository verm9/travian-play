<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <link rel="stylesheet" href="webjars/bootstrap/3.3.6/css/bootstrap.min.css">
    <script type="text/javascript" src="webjars/bootstrap/3.3.6/js/bootstrap.min.js"></script>
    <title>Travian-play Login Page</title>
    <style>
        .error {
            padding: 15px;
            margin-bottom: 20px;
            border: 1px solid transparent;
            border-radius: 4px;
            color: #a94442;
            background-color: #f2dede;
            border-color: #ebccd1;
        }


        #login-box {
            width: 400px;
            padding: 20px;
            margin: 100px auto;
            background: #fff;
            -webkit-border-radius: 2px;
            -moz-border-radius: 2px;
            border: 1px solid #333;
        }
    </style>
</head>
<body onload='document.loginForm.username.focus();'>

<div id="login-box" align="center">

    <h2>Enter the passphrase:</h2>

    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>

    <form name='loginForm'
          action="<c:url value='j_spring_security_check' />" method='POST'>

        <input type='hidden' name='username' value='theChosenOne'>
        <input type='password' name='password' />
        <button type="submit" class="btn btn-default">Submit</button>

    </form>
</div>

</body>
</html>