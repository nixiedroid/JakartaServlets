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
    <form action="${pageContext.request.contextPath}/findCoffee" method="post">
        <input type="search" placeholder="Coffee name" name="name" pattern="[a-zA-Zа-яА-ЯёЁ]+"/>
        <input type="submit" value="Find">
    </form>
</c:if>
<br>
<c:if test="${coffee.id == 0}"> Add coffee: </c:if>

<form method="post" action="${pageContext.request.contextPath}/add">
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
                <input type="checkbox" name="hasMilk">
            </td>
        </tr>
    </table>
    <hidden path="id"></hidden>
</form>
</body>