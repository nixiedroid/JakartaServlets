<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<%
    String message = pageContext.getException().getMessage();
    String exception = pageContext.getException().getClass().toString();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Exception</title>
</head>
<body>
<h2>Internal Server Error</h2>
<p>Type: <%= exception%></p>
<p>Message: <%= message %></p>
</body>
</html>
