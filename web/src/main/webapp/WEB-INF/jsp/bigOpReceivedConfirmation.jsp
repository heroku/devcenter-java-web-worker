<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="header.jsp"/>
<div class="row">
    <div class="span8 offset2">
        <div class="page-header">
            <h1>Big Operation Submitted</h1>
        </div>

        <div>
            <p>
                Your request was received and has been sent for processing. 
            </p>
            <p>
                If you haven't already done so, be sure to scale up one or more worker dynos and tail the logs:
            </p>
            <p>
                <code>$ heroku scale worker=1</code><br/>
                <code>$ heroku logs --tail</code>
            </p>
            <p>
                In your logs you should see that the operation passed from the <code>web</code> dyno
                to one of your <code>worker</code> dynos, which should look something like this:
            </p>
            <p>
                <code>16:50:29 web.1     | Sent to RabbitMQ: BigOperation{name='${bigOp.name}'}</code><br/>
                <code>16:50:30 worker.1  | Received from RabbitMQ: BigOperation{name='${bigOp.name}'}</code>
            </p>
        </div>
    </div>
</div>
<jsp:include page="footer.jsp"/>