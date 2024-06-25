<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<jsp:useBean id="coffees" scope="request" class="java.util.ArrayList"/>
<jsp:useBean id="coffee" scope="request" class="com.nixiedroid.jakarta.rest.models.Coffee"/>
<c:if test="${coffees.size()==0}">
    Empty for now
</c:if>
<c:if test="${coffees.size()>0}">
    Coffee list:
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
                <td>${coffee.has_milk}</td>
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
    <form action="${pageContext.request.contextPath}/find" method="post">
        <input type="text" placeholder="Find" name="find" pattern="[a-zA-Zа-яА-ЯёЁ]+"/>
        <input type="submit" value="Find">
    </form>
</c:if>
<c:if test="${coffee.id == 0}"> Add coffee: </c:if>
<c:if test="${coffee.id != 0}"> Edit coffee: </c:if>
<form method="post" action="${pageContext.request.contextPath}/add">
    <div>
        Name
        <input placeholder=" " name="name" required="required"/>
    </div>
    <div>
        Has milk
        <input type="checkbox" name="hasMilk"></div>
    <hidden path="id"></hidden>
    <div>
        <button type="submit">SAVE</button>
    </div>
</form>

</body>