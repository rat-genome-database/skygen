<%@ page import="edu.mcw.rgd.controller.PatientController" %>

<%@ page import="edu.mcw.rgd.dao.spring.StringMapQuery" %>
<%@ page import="java.util.List" %>
<%@ page import="edu.mcw.rgd.reporting.Link" %>
<html>
<head>
    <link rel="stylesheet" href="/skygen/css/jquery/jquery-ui-1.8.18.custom.css" type="text/css" />
    <link rel="stylesheet" href="/OntoSolr/files/jquery.autocomplete.css" type="text/css" />
    <script type="text/javascript" src="/skygen/js/jquery/jquery-1.7.1.min.js"></script>
    <script type="text/javascript" src="/skygen/js/jquery/jquery-ui-1.8.18.custom.min.js"></script>
    <script>
        function showOntTree(ontId) {
            window.parent.showOntTree(ontId);
            $("oldPhenoId").val(ontId);
        }

    </script>
</head>
<%
    String pid= request.getParameter("pid");
%>
<table width=100%>
<%
    List<StringMapQuery.MapPair> phenotypes = (List <StringMapQuery.MapPair>) request.getAttribute("phenotypes");
    int i=1;
    for (StringMapQuery.MapPair phenotype : phenotypes) {
    %>
        <tr>
            <td width=20><a href='/skygen/patient.html?pid=<%=pid%>&phenoId=<%=phenotype.keyValue%>&a=delPheno'><img src='/skygen/common/images/remove.png'/></a></td>
            <td width=20><b><%=i++%>.&nbsp;&nbsp;</b></td>
            <td><a href='javascript:showOntTree("<%=phenotype.keyValue%>");'><%=phenotype.stringValue%></a></td>
            <td align=right>
                <table>
                    <tr>
                        <td><a href='javascript:showOntTree("<%=phenotype.keyValue%>");'><img src='/skygen/common/images/tree.png'/></a></td>
                        <td><a style="font-size:13px;" target="_blank" href="http://ghr.nlm.nih.gov/search?query=<%=phenotype.stringValue%>&Search=">GHR</a></a></td>
                        <td><a style="font-size:13px;" target="_blank" href="http://www.ncbi.nlm.nih.gov/omim/?term=<%=phenotype.stringValue%>">OMIM</a></td>
                        <td><a style="font-size:13px;"  target="_blank" href="http://rgd.mcw.edu/<%=Link.ontAnnot(phenotype.keyValue)%>">RGD</a></td>
                    </tr>
                </table>
            </td>

        </tr>

    <% }  %>
</table>

<form id="phenoForm" method="GET" action="/skygen/patient.html">
    <input name="pid" type="hidden" value="<%=pid%>"/>
    <input id="formAction" name="a" type="hidden" value="addPheno"/>
    <input id="phenoId" name="phenoId" type="hidden" value=""/>
    <input id="oldPhenoId" name="oldPhenoId" type="hidden" value=""/>
</form>
</html>