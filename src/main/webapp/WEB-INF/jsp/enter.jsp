<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="sf"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <link rel="stylesheet" href="webjars/bootstrap/3.3.6/css/bootstrap.min.css">
    <link rel="stylesheet" href="resources/css/main.css">
    <script type="text/javascript" src="webjars/jquery/2.2.4/jquery.min.js"></script>
    <script type="text/javascript" src="webjars/bootstrap/3.3.6/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="webjars/datatables/1.10.12/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="webjars/noty/2.3.8/js/noty/packaged/jquery.noty.packaged.min.js"></script>
    <title>Travian-play login</title>
</head>
<body>
<div class="container">
    <h2>Travian-play: fill in server and login data</h2>
    <p>Travian-play application has no login data provided. You can enter this data now:</p>
    <sf:form class="form-inline" method="POST" action="/enter" modelAttribute="loginData">
        <p class="errorMessage">
            <c:set var="serverErrors"><sf:errors path="server"/></c:set>
            <c:set var="loginErrors"><sf:errors path="login"/></c:set>
            <c:set var="passwordErrors"><sf:errors path="password"/></c:set>
            <c:if test="${not empty serverErrors}">
                server ${serverErrors} <br/>
            </c:if>
            <c:if test="${not empty loginErrors}">
                login ${loginErrors} <br/>
            </c:if>
            <c:if test="${not empty passwordErrors}">
                password ${passwordErrors} <br/>
            </c:if>
        </p>
        <div class="form-group">
            <label class="sr-only" for="server">Server:</label>
            <sf:input type="text" class="form-control" id="server" style="width:409px;" placeholder="Enter server (e.g., http://www.x50000.aspidanetwork.com)" path="server"/>
        </div>
        <div class="form-group">
            <label class="sr-only" for="login">Login:</label>
            <sf:input type="text" class="form-control" id="login" placeholder="Enter login" path="login"/>
        </div>
        <div class="form-group">
            <label class="sr-only" for="pwd">Password:</label>
            <sf:input type="password" class="form-control" id="pwd" placeholder="Enter password" path="password"/>
        </div>
        <sf:button type="submit" class="btn btn-default">Submit</sf:button>
    </sf:form>
</div>
</body>
</html>
