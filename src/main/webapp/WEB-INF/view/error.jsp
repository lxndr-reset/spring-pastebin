<%@ page import="java.time.LocalDateTime" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Error Page</title>
</head>
<body>
<h2>Error Information</h2>
<%Exception exception = (Exception) request.getAttribute("exception");%>

<strong>Message:</strong> <%=exception.getMessage()%><br/>
<strong>Time:</strong> <%=LocalDateTime.now()%><br/>
<strong>Error type:</strong> <%=exception.getClass().toString()%><br/>
<!--<strong>Trace:</strong><br/>-->
<%--<pre>--%>
<%--    <%--%>
<%--        for (StackTraceElement stackTraceElement : exception.getStackTrace()) {--%>
<%--            out.println(stackTraceElement);--%>
<%--        }--%>
<%--    %>--%>
<%--</pre>--%>

</body>
</html>