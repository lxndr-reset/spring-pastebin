<%@ page import="com.pastebin.entity.Message" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <title>Message Page</title>
</head>
<body>
<h1>Message Details</h1>
<%Message message = (Message) request.getAttribute("message");%>
<p><strong>Id:</strong> <%=message.getId()%>
</p>
<p><strong>Message:</strong> <%=message.getValue()%>
</p>
<p><strong>Link:</strong> <a href="http://localhost:8080/get-message/<%=message.getShortURL().getUrlValue()%>">http://localhost:8080/get-message/<%=message.getShortURL().getUrlValue()%>
</a></p>
<p><strong>Deletion date:</strong> <%=message.getDeletionDate() == null ? "Never" : message.getDeletionDate()%>
</p>
</body>
</html>