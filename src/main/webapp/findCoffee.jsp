<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Find page</title>
</head>
<body>
<jsp:useBean id="coffees" scope="request" class="java.util.ArrayList"/>
<c:if test="${coffees.size()==0}">No coffees found</c:if>
<c:if test="${coffees.size()!=0}">
    Found:
    <table>
        <tr>
            <th>Name</th>
            <th>Has Milk</th>
            <th>Delete</th>
            <th>Edit</th>
        </tr>
        <c:forEach items="${coffees}" var="coffee">
            <tr>
                <td>${coffee.name}</td>
                <td>
                    <input type="checkbox" name="hasMilk" onclick="return false"
                    <c:if test="${coffee.has_milk}"> checked </c:if>
                    </input>
                </td>
                <td>
                    <form action="${pageContext.request.contextPath}/delete" method="post">
                        <input type="submit" value="Delete">
                        <input type="hidden" value="${coffee.id}" name="id">
                    </form>
                </td>
                <td>
                    <form action="${pageContext.request.contextPath}/edit" method="post">
                        <input type="submit" value="Edit">
                        <input type="hidden" value="${coffee.id}" name="id">
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</c:if>
</body>
</html>
