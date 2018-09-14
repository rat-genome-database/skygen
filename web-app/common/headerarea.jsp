<%@ page import="edu.mcw.rgd.dao.impl.SkyGenDAO" %>
<%@ page import="org.springframework.security.core.Authentication" %>
<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Iterator" %>
<html>
<head>
    <link rel="stylesheet" href="/skygen/css/jquery/jquery-ui-1.8.18.custom.css" type="text/css" />
    <link rel="stylesheet" href="/OntoSolr/files/jquery.autocomplete.css" type="text/css" />
    <script type="text/javascript" src="/skygen/js/jquery/jquery-1.7.1.min.js"></script>
    <script type="text/javascript" src="/skygen/js/jquery/jquery-ui-1.8.18.custom.min.js"></script>
    <style>
        body {
                 font-family: 'Lucida Sans Unicode', 'Lucida Grande', 'sans-serif';
                 background-color:  #EFF1F0;
        }


    </style>
</head>

<%
    String type = "";
    if (request.getParameter("type") != null) {
        type = "&type=" + request.getParameter("type");
    }
String pid = request.getParameter("pid");
SkyGenDAO sdao = new SkyGenDAO();

Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String name = auth.getName(); //get logged in username


%>


<script>
    function updateGenesAndPhenotypes() {

        var loc = location.href.split("?")[0];
        var locArray = location.href.split("?");

        if (document.getElementById("gList").checked && document.getElementById("pList").checked) {
            location.href=locArray[0] + "?pid=<%=pid%><%=type%>";
            return;
        }

        if (document.getElementById("gList").checked) {
            location.href=locArray[0] + "?pid=<%=pid%>&gList=1<%=type%>";
            return;
        }

        if (document.getElementById("pList").checked) {
            location.href=locArray[0] + "?pid=<%=pid%>&pList=1<%=type%>";
        }

        return;
    }
</script>


<body>

<table cellspacing=0 cellpadding=10 width=100%>
<tr >
    <td style="background-image:url('/skygen/images/bg3.png');"><table border=0><tr><td style="font-size:35px; color:white;font-family: Impact, Charcoal, sans-serif;">Skynet</td><td align="top" style="font-size:35px; color:white;font-family: Impact, Charcoal, sans-serif;">&nbsp;&#9702;&nbsp;</td><td style="font-size:35px; color:white;font-family: Impact, Charcoal, sans-serif;">Genome</td></tr></table></td>
    <td style="background-image:url('/skygen/images/bg3.png');" width=10% align="center"><img src="/skygen/images/HMGC.jpg" height=75 width=75/></td>
</tr>
</table>

    <%
        ArrayList error = (ArrayList) request.getAttribute("error");
        if (error != null) {
            Iterator errorIt = error.iterator();
            while (errorIt.hasNext()) {
                String err = (String) errorIt.next();
                out.println("<br><span style=\"color:red;\">" + err + "</span>");
            }
            out.println("<br>");
        }

        ArrayList status = (ArrayList) request.getAttribute("status");
        if (status !=null) {
            Iterator statusIt = status.iterator();
            while (statusIt.hasNext()) {
                String stat = (String) statusIt.next();
                out.println("<br><span style=\"color:blue;\">" + stat + "</span>");
            }
            out.println("<br><br>");
        }
    %>
