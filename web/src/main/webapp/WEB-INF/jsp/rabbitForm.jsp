<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<jsp:include page="header.jsp"/>
<div class="row">
    <div class="span8 offset2">
        <div class="page-header">
            <h1>Rabbit Form</h1>
        </div>

        <c:if test="${error != null}">
            <div class="alert">${error}</div>
        </c:if>

        <form:form method="post" modelAttribute="bigOp">
        <label>Name: <form:input path="name"/></label>

            <div class="btn-group">
                <input type="submit" class="btn btn-primary">
            </div>
        </form:form>
    </div>
</div>
<jsp:include page="footer.jsp"/>