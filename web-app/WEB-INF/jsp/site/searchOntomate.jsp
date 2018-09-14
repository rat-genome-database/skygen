<%@ page import="java.util.List" %>
<%@ page import="edu.mcw.rgd.dao.spring.StringMapQuery" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Getting Result</title>
</head>
<body onload='document.forms[0].submit()'>
Querying OntoMate...
<form id="qForm" action="http://morgan.hmgc.mcw.edu/QueryBuilder/getSkygenResult/" method="post">
    <%
    List<StringMapQuery.MapPair> genes = (List<StringMapQuery.MapPair>) request.getAttribute("genes");
    int i=0;
    if (genes != null) {

    for (StringMapQuery.MapPair gene : genes) {
    %>
    <input type="hidden" name="qRgdIds[<%=i%>].rgdId" value="<%=gene.keyValue%>"/>
    <%
            i++;
        }
    }
        List<StringMapQuery.MapPair> phenotypes = (List <StringMapQuery.MapPair>) request.getAttribute("phenotypes");
        if (phenotypes != null) {
        i = 0;
        for (StringMapQuery.MapPair phenotype : phenotypes) {
        %>
    <input type="hidden" name="qOntoIds[<%=i%>].ontoId" value="<%=phenotype.keyValue%>"/>

    <%
                i++;
            }
    }%>

    <input type="hidden" name="qGeneCond" value=""/>
    <input type="hidden" name="qCond" value=""/>
</form>
</body>
</html>