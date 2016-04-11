<%@ page
	import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Welcome to the dropbox application :)</title>
<style>
* {
	font-family: Calibri;
	font-size: 14pt;
}

p {
	line-height: 1.3em
}

.buttonStyle {
	color: black;
	background: white;
	font-weight: bold;
	border: 2px solid black;
	margin-right: 10pt;
}

.buttonStyle:hover {
	color: white;
	background: #900;
}

.textfield {
	border: 2px solid black;
}

h2 {
	color: #B40404;
	font-size: 18pt;
}

h3 {
	color: #B40404;
	font-size: 15pt;
	margin-bottom: 0;
}

welcome {
	font-size: 18pt;
	font-weight: bold;
	color: #B40404
}

.messagefield {
	border: 2px solid #B40404;
	width: 20cm;
}
}
</style>
</head>
<body>
	<c:choose>
		<c:when test="${user != null }">
			<welcome>Welcome ${user.email}!</welcome> <br/><br/>
You can signout of your dropbox <a href="${logout_url}">here</a>!<br />
			</p>
Add a new directory:
<form method="post" action="/directory">
				<input type="text" name="textfield" placeholder="Enter the name of your directory!" size=40/> <input
					type="submit" name="button" value="Add" class="buttonStyle" />
			</form>
			<br />	
Upload a new file:
	<form enctype="multipart/form-data" method="post" action="${uploadURL}"/>
			<input type="file" name="file" size=100/>
			<input type="submit" class="buttonStyle" value="Add file"/>
			</form>

			<br />
			<h2>Contents of ${currentPath}</h2>
			<c:choose>
				<c:when test="${currentPath != '/'}">
					<p>
					<form method="post" action="/directory">
						<input type="hidden" name="hidden_value" value="../" /> 
						<input type="submit" name="button" value="change" class="buttonStyle" />
						../
					</form>
				</c:when>
			</c:choose>

			<h3>Subdirectories</h3>
			<c:forEach items="${subDirectories}" var="name" begin="0"
				varStatus="loop">
				<form method="post" action="/directory">
					<input type="hidden" name="hidden_value" value="${name}" /> 
					<table>
					<colgroup width="100"></colgroup>
					<tr>
					<td width="200"> ${name} </td>
					<td> <input type="submit" name="button" value="change" class="buttonStyle"> </td>
					<td> <input type="submit" name="button" value="delete" class="buttonStyle"> </td>
					</tr>
				</form>
				</table>
			</c:forEach>

			<h3>Files</h3>
			<c:forEach items="${fileNames}" var="name" begin="0" varStatus="loop">
				<form method="get" action="/files">
					<input type="hidden" name="hidden_value" value="${name.name}"/>
					<table>
					<colgroup width="100"></colgroup>
					<tr>
					<td width=300> ${name.name} </td> 
					<td> <input type="submit" name="button" value="download" class="buttonStyle"> </td>
					<td> <input type="submit" name="button" value="delete" class="buttonStyle"> </td>
					</tr>
				</form>
				</table>
			</c:forEach>
		<br/><br/>
		<input type="text" placeholder="Information for the user is illustrated here!" class="messagefield" value="${message }" readonly> 
	
		</c:when>
		
		<c:otherwise>
			<p>
				Welcome! <a href="${login_url}">Sign in or register!</a>
			</p>
		</c:otherwise>
	</c:choose>
</body>
</html>