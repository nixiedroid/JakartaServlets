<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: someb
  Date: 27.06.24
  Time: 17:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Page</title>

</head>
<body>
<jsp:useBean id="coffee" scope="request" class="com.nixiedroid.jakarta.rest.models.Coffee"/>
 Edit coffee:
<form method="post" action="${pageContext.request.contextPath}/editSubmit">
    <div>
        <button type="submit">Submit</button>
    </div>
    <table>
        <tr>
            <th>Name</th>
            <th>Has milk</th>
        </tr>
        <tr>
            <td>
                <input type="text" placeholder=" " name="name" value="${coffee.name}"/>
            </td>
            <td>
                <input type="checkbox" name="hasMilk"
                <c:if test="${coffee.has_milk}"> checked </c:if>
                </input>
            </td>
        </tr>
    </table>
    <input type="hidden" value="${coffee.id}" name="id"> </input>
</form>
</body>
</html>
