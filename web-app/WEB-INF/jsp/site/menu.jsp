

<% if (request.getParameter("type") ==null || !request.getParameter("type").equals("j")) { %>

<td align="right">
    <table style="border: 1px solid black; background-color:white;">
        <tr>
            <td align="right">
                <%
                    String gPicked = "";
                    String pPicked = "";

                    if (request.getParameter("pList") == null && request.getParameter("gList") == null) {
                        gPicked = "checked";
                        pPicked = "checked";
                    }

                    if (request.getParameter("pList") != null && request.getParameter("pList").equals("1")) {
                        pPicked="checked";
                    }
                    if (request.getParameter("gList") != null && request.getParameter("gList").equals("1")) {
                        gPicked="checked";
                    }
                %>

                Lists:&nbsp;<input id="gList" type="checkbox" onClick="updateGenesAndPhenotypes()" style="font-size:14px; padding-right:5px;" <%=gPicked%>>Genes <input id="pList" onclick="updateGenesAndPhenotypes()" type="checkbox" style="font-size:14px; padding-right:5px;" <%=pPicked%>>Diseases
            </td>
            <td>&nbsp;&nbsp;&nbsp;<a href="patient.html?pid=<%=pid%>">Edit Lists</a>&nbsp;</td>
        </tr>
     </table>
</td>

<% } %>

<td align='right'>
        <table style="border: 1px solid black; background-color:white; ">
            <tr>
                <% if (request.getParameter("type") != null && request.getParameter("type").equals("g")) { %>
                    <td>
                        <a style="font-size:14px; padding-right:5px;" href="clinVar.html?pid=<%=pid%>&type=p<%=gList%><%=pList%>">Disease View</a>
                    </td>
                <% } else if (request.getParameter("type") != null && request.getParameter("type").equals("i")) { %>
                    <td>
                        <a style="font-size:14px; padding-right:5px;" href="clinVar.html?pid=<%=pid%>&type=p<%=gList%><%=pList%>">Disease View</a>
                    </td>
                    <td>
                        <a style="font-size:14px; padding-right:5px;" href="clinVar.html?pid=<%=pid%>&type=g<%=gList%><%=pList%>">Gene View</a>
                    </td>
                <% } else { %>
                    <td>
                        <a style="font-size:14px; padding-right:5px;" href="clinVar.html?pid=<%=pid%>&type=g<%=gList%><%=pList%>">Gene View</a>
                    </td>
                <% } %>

                <% if (request.getParameter("type") ==null || !request.getParameter("type").equals("i")) { %>
                <td>
                    <a style="font-size:14px; padding-right:5px;" href="clinVar.html?pid=<%=pid%>&type=i">Inspect Gene List</a>
                </td>
                <td>
                    <a style="font-size:14px; padding-right:5px;"  href="clinVar.html?pid=<%=pid%>&d=1<%=gList%><%=pList%>">Download&nbsp;Variants</a>
                </td>
                <% } %>
                <td>
                    <a style="font-size:14px; padding-right:5px;"  href="patient.html?pid=<%=pid%>">Return&nbsp;to&nbsp;Patient</a>

                </td>
            </tr>
        </table>
</td>
