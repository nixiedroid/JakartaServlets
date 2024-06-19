<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="springForm" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-16">
    <title>Title</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles.css"/>
</head>
<body>
<form action="${pageContext.request.contextPath}/" method="get">
    <input type="submit" value="Назад"/>
</form>
<c:if test="${products.size()==0}">
    Здессь пока что ничего нет

</c:if>
<c:if test="${products.size()>0}">
    Список продуктов:
    <table>
        <tr>
            <th>Название</th>
            <th>Цена</th>
            <th>Количество</th>
            <th>Удалить</th>
            <th>Изменить</th>
        </tr>
        <c:forEach items="${products}" var="product">
            <tr>
                <td>${product.name}</td>
                <td>${product.price}</td>
                <td>${product.amount}</td>
                <td>
                    <form action="/productsListDelete" method="post">
                        <input type="submit" value="Удалить">
                        <input type="hidden" value="${product.id}" name="id">
                    </form>
                </td>
                <td>
                    <form action="/productsListSave" method="post">
                        <input type="submit" value="Изменить">
                        <input type="hidden" value="${product.id}" name="id">
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
    <form action="/productsListFind" method="post">
        <input type="text" placeholder="Найти" name="find" pattern="[a-zA-Zа-яА-ЯёЁ]+"/>
        <span class="form__error" style="text-align: center">Поле поиска не может содержать спецсимволы</span>
        <input type="submit" value="Найти">
    </form>
</c:if>
<c:if test="${product.id == 0}"> Добавить продукт: </c:if>
<c:if test="${product.id != 0}"> Изменить продукт: </c:if>
<springForm:form method="post" action="/productsListSaveProcess" modelAttribute="product">
    <div>
        Название
        <springForm:input placeholder=" " path="name" required="true"/></div>
    <div><springForm:errors path="name" cssClass="error"/>
    </div>
    <div>
        Цена: <springForm:input type="number" path="price" required="true"/></div>
    <div><springForm:errors path="price" cssClass="error"/>
    </div>
    <div> Количество
        <springForm:input type="number" path="amount" required="true" min="1" value="1" max="10000000"/></div>
    <div><span class="form__error" style="text-align: center">Неверный ввод</span></div>
    <springForm:errors path="amount" cssClass="error"/>


    <springForm:hidden path="id"/>
    <div>
        <button type="submit">SAVE</button>
    </div>
</springForm:form>

<sec:authorize access="hasAnyRole('ROLE_ADMIN')">
    <form action="/productsListAdd20" method="post">
        <input type="submit" value="Добавить 20">
    </form>
</sec:authorize>
</body>