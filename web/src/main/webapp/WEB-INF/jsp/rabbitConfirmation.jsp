<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="header.jsp"/>
<div class="row">
    <div class="span8 offset2">
        <div class="page-header">
            <h1>Rabbit Form</h1>
        </div>

        <c:if test="${error != null}">
            <div class="alert">${error}</div>
        </c:if>

        Thanks!
    </div>
</div>
<jsp:include page="footer.jsp"/>