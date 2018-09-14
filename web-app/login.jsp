<%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core' %>
<%
    String pageTitle = "Secure Login";
    String headContent = "";
    String pageDescription = "";

%>

<%@ include file="/common/headerarea.jsp"%>


<c:if test="${not empty param.login_error}">
 <br><br>
  <span style="color:red;">
    Your login attempt was not successful, try again.<BR><BR>
    Reason: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}" />
  </span>
</c:if>
<br><br>
<br><br>
<form action="<c:url value='j_spring_security_check'/>" method="POST">
<table align="center"><tr><td>
    <tr><td>User:</td><td><input size=30 type='text' name='j_username' value="" <c:if test="${not empty param.login_error}">value='<c:out value="${ACEGI_SECURITY_LAST_USERNAME}"/>'</c:if>></td></tr>
    <tr><td>Password:</td><td><input size=30 type='password' name='j_password' value=""></td></tr>
    <tr><td>&nbsp;</td></tr>
    <!--<tr><td><input type="checkbox" name="j_spring_security_remember_me"></td><td>Don't ask for my password for two weeks (not implemented yet)</td></tr>-->

    <tr><td colspan='2' align="right"><input name="submit" type="submit" value="Log In"></td></tr>
  </table>

</td></tr></table>
</form>

<%@ include file="/common/footerarea.jsp"%>