<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="model.User"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="../LibraryCSS/style.css" />

</head>
<body
	style="background: url(../LibraryIMG/background.jpg) repeat-x scroll left top transparent"
	class="main">
	<%
		User user = (User) session.getAttribute("user");
	%>

	<div align="center">
		<!--<div id="header">
			<div class="Top1Band">-->
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>

				<td width="10%"><img src="../LibraryIMG/movietape.jpg"
					width="70" height="70" /></td>
				<td align="left" width="90%"><font color="ffffff"
					face="WildWest" size=8>Hello <%=user.getDisplayName()%> !
						Welcome to the Video Library..
				</font></td>
			</tr>

		</table>
		<%
			if (user.getPreference().getPreferenceId() == 3) {
		%>

		<form method="post" action="DecideAction">
			<table>
				<tr>
					<td>
					<td><input type="submit" name="Home" value="Home"></td>
					<td><input type="submit" name="Movies" value="Movies"></td>
					<td><input type="submit" name="Users" value="Users"></td>
					<td><input type="submit" name="Revenue" value="Revenue"></td>
					<td><input type="submit" name="SignOut" value="Sign Out"></td>
				</tr>
			</table>
		</form>
		<!-- <nav>
		<ul class="fancyNav">
			<li id="home"><a href="Home.jsp" class="homeIcon">Home</a></li>
			<li id="art"><a href="MovieDetails.jsp">Movie</a></li>
			<li id="cinema"><a href="">User</a></li>
			<li id="contact"><a href="Logout.jsp">Sign Out</a></li>
			<li id="login"><a href="MyProfile.jsp" class="loginIcon">Login</a></li>
		</ul>
		</nav> -->
		<%
			} else if (user.getPreference().getPreferenceId() == 2){
		%>

		<form method="post" action="DecideAction">
			<table>
				<tr>
					<td>
					<td><input type="submit" name="Home" value="Home"></td>
					<td><input type="submit" name="MyAccount" value="My Account"></td>
					<td><input type="submit" name="Movies" value="Movies"></td>
					<td><input type="submit" name="MyCart" value="My Cart"></td>
					<td><input type="submit" name="SignOut" value="Sign Out"></td>
				</tr>
			</table>
		</form>
		<!--  <nav>
		<ul class="fancyNav">
			<li id="home"><a href="Home.jsp" class="homeIcon">Home</a></li>
			<li id="myAccount"><a href="MyAccount.jsp">My Account</a></li>
			<li id="movies"><a href="MovieDetails.jsp">Movies</a></li>
			<li id="movies"><a href="Cart.jsp">My Cart</a></li>
			<li id="signOut"><a href="Logout.jsp">Sign Out</a></li>
			<li id="login"><a href="MyProfile.jsp" class="loginIcon">Login</a></li>
		</ul>
		</nav>-->
		<%
			} else if (user.getPreference().getPreferenceId() == 1){
		%>

		<form method="post" action="DecideAction">
			<table>
				<tr>
					<td>
					<td><input type="submit" name="Home" value="Home"></td>
					<td><input type="submit" name="MyAccount" value="My Account"></td>
					<td><input type="submit" name="Movies" value="Movies"></td>
					<td><input type="submit" name="MyCart" value="My Cart"></td>
					<td><input type="submit" name="AddBalance" value="Add Balance"></td>
					<td><input type="submit" name="SignOut" value="Sign Out"></td>
				</tr>
			</table>
		</form>
		<!--  <nav>
		<ul class="fancyNav">
			<li id="home"><a href="Home.jsp" class="homeIcon">Home</a></li>
			<li id="myAccount"><a href="MyAccount.jsp">My Account</a></li>
			<li id="movies"><a href="MovieDetails.jsp">Movies</a></li>
			<li id="movies"><a href="Cart.jsp">My Cart</a></li>
			<li id="signOut"><a href="Logout.jsp">Sign Out</a></li>
			<li id="login"><a href="MyProfile.jsp" class="loginIcon">Login</a></li>
		</ul>
		</nav>-->
		<%
			}
		%>
	</div>
</body>
</html>