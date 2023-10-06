<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <title>Message Page</title>
</head>
<body>
<h1>Message Details</h1>

<p><strong>Hash:</strong> <%=request.getAttribute("hash")%></p>
<p><strong>Message:</strong> <%=request.getAttribute("message")%></p>
</body>
</html>