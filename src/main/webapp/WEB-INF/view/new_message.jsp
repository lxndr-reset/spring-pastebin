<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>New Message</title>
</head>
<body>
<h1>New Message</h1>
<form>
    <label for="content">Content:</label><br>
    <input type="text" id="content" name="content" value="${new-message.content == null ? 0 : new-message.content}"><br>
    <input type="submit" value="Submit">
</form>

<h2>List of all message fields</h2>
<table>
    <tr>
        <th>Field</th>
        <th>Value (0 if null)</th>
    </tr>
    <c:forEach var="field" items="${new-message}">
        <tr>
            <td>${field.key}</td>
            <td>${field.value == null ? 0 : field.value}</td>
        </tr>
    </c:forEach>
</table>
</body>
</html>