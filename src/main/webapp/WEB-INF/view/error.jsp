<%@ page
        import="java.time.LocalDateTime, java.util.HashSet, java.util.Set, java.util.NoSuchElementException, com.pastebin.exception.MessageDeletedException, com.pastebin.exception.NoAvailableShortURLException, com.pastebin.exception.UrlNotExistsException, com.pastebin.exception.UserBlockedException" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Error Page</title>
</head>
<body>
<h2>Error Information</h2>
<%//    HashSet<Class<? extends Exception>> noRethrowExceptions = new HashSet<>(Set.of(NoSuchElementException.class, MessageDeletedException.class,
//        NoAvailableShortURLException.class, UrlNotExistsException.class, UserBlockedException.class,
//        IllegalArgumentException.class));
    Exception exception = (Exception) request.getAttribute("exception");%>

<strong>Message:</strong> <%=exception.getMessage()%><br/>
<strong>Time:</strong> <%=LocalDateTime.now()%><br/>
<strong>Error type:</strong> <%=exception.getClass().toString()%><br/>
</body>
</html>