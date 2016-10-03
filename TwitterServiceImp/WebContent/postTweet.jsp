<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link href="./main.css" rel="stylesheet" type="text/css">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Post tweet</title>
</head>
<body>
	<% if (request.getAttribute("status") == null) { %>
		<h3>Post your tweet below: </h3>
		<form action="MainServlet" method="POST">
			<input type="hidden" name="oauth_verifier" id="oauth_verifier" value="<%=request.getParameter("oauth_verifier")%>" />
			<input type="hidden" name="oauth_token" id="oauth_token" value="<%=request.getParameter("oauth_token")%>" />
			<input type="text" class="tweet-box" name="status" maxlength="140" value="" />
			<input type="submit" name="postStatus" value="Tweet" />
		</form>
	<% } else {
		String status = request.getAttribute("status").toString(); %>
		<span>Your tweet "</span> <%=status%> <span>" has been successfully posted</span>
	<% } %>
</body>
</html>