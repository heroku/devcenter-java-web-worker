<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<jsp:include page="header.jsp"/>
<div class="row">
    <div class="span8 offset2">
        <div class="page-header">
            <h1>Big Operation Submission Form</h1>
        </div>
        <div>
            <p>
                This is a form to submit a imaginary operation that might take a long time be processed in a web request,
                so is processed asynchronously by one or more worker dynos.
                First, make sure you have a one or more worker dynos running:
            </p>
            <p>
                <code>$ heroku scale worker=1</code>
            </p>
            <p>
                Next, start tailing the logs so you can see operation being passed from
                the <code>web</code> dyno to one of your <code>worker</code> dynos.
            </p>
            <p>
                <code>$ heroku logs --tail</code>
            </p>
            <p>
                With the logs open, give your operation a name and submit the form below:
            </p>
        </div>

        <form:form method="post" modelAttribute="bigOp" class="well form-inline">
            <label>Name: <form:input path="name"/></label>
            <input type="submit" class="btn btn-primary">
        </form:form>
    </div>
</div>
<jsp:include page="footer.jsp"/>