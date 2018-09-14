<%@ page import="edu.mcw.rgd.controller.PatientController" %>
<%@ page import="edu.mcw.rgd.dao.spring.StringMapQuery" %>
<%@ page import="java.util.List" %>
<%@ page import="edu.mcw.rgd.reporting.Link" %>

<html>

<table width=100% border=0>
<%
    String pid = request.getParameter("pid");
%>

<%
    List<StringMapQuery.MapPair> genes = (List<StringMapQuery.MapPair>) request.getAttribute("genes");
    int i=1;
    for (StringMapQuery.MapPair gene : genes) {
    %>
        <tr>
            <td width=20><a href='/skygen/patient.html?pid=<%=pid%>&geneId=<%=gene.keyValue%>&a=delGene'><img src='/skygen/common/images/remove.png'/></a></td>
            <td width=20><b><%=i++%>.</td>
            <td width=10>&nbsp;&nbsp;</b><%=gene.stringValue%></td>
            <td>&nbsp;</td>
            <td align=right>
                <table>
                    <tr>
                        <td><a style="font-size:13px;" target="_blank" href="http://ghr.nlm.nih.gov/gene/<%=gene.stringValue%>">GHR</a></a></td>
                        <td><a style="font-size:13px;" target="_blank" href="http://www.ncbi.nlm.nih.gov/omim/?term=<%=gene.stringValue%>">OMIM</a></td>
                        <td><a style="font-size:13px;"  target="_blank" href="http://rgd.mcw.edu/<%=Link.it(gene.keyValue)%>">RGD</a></td>
                    </tr>
                </table>
            </td>
        </tr>

    <% }  %>
</table>



<form id="geneForm" method="GET" action="/skygen/patient.html">
    <input name="pid" type="hidden" value="<%=pid%>"/>
    <input name="a" type="hidden" value="addGene"/>
    <input id="geneId" name="geneId" type="hidden" value=""/>
</form>
</html>