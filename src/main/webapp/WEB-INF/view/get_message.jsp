<%@ page import="com.pastebin.entity.Message" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <title>Message Page</title>
</head>
<body>
<h1>Message Details</h1>
<%Message message = (Message) request.getAttribute("message");%>
<p><strong>Id:</strong> <%=message.getId()%></p>
<p><strong>Message:</strong> <%=message.getValue()%></p>
</body>
</html>