<%@ page import="edu.mcw.rgd.dao.impl.SearchDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="edu.mcw.rgd.datamodel.search.IndexRow" %>
<%@ page import="java.util.ArrayList" %>


<table border="0" width="95%">
    <tr>
        <td width="20%"></td>
        <td width="20%" style="border-bottom: 1px solid black; font-weight:700;">Symbol</td>
        <td width="20%" style="border-bottom: 1px solid black; font-weight:700;">RGD ID</td>
        <td>&nbsp;</td>
        <td width="20%" style="border-bottom: 1px solid black; font-weight:700;">Type</td>
        <td>&nbsp;</td>
        <td width="20%" style="border-bottom: 1px solid black; font-weight:700;">SPC</td>
    </tr>

<%
    SearchDAO dao = new SearchDAO();

    List lookupList = null;

    boolean objectSet = request.getParameter("objectType") != null && !request.getParameter("objectType").equals("");
    boolean speciesSet = request.getParameter("speciesTypeKey") != null && !request.getParameter("speciesTypeKey").equals("");

    String searchStringLc = request.getParameter("search");

    if (searchStringLc!=null) {
        searchStringLc = searchStringLc.toLowerCase();
    }


    if (objectSet && speciesSet) {
        lookupList = dao.findSymbol(searchStringLc, Integer.parseInt(request.getParameter("speciesTypeKey")), request.getParameter("objectType"));
    }else if (speciesSet  && !objectSet){
        lookupList = dao.findSymbol(searchStringLc, Integer.parseInt(request.getParameter("speciesTypeKey")));
    }else {
        lookupList = dao.findSymbol(searchStringLc);
    }

    Iterator lookIt = lookupList.iterator();

    while (lookIt.hasNext()) {
        IndexRow ir = (IndexRow) lookIt.next();
%>
<tr>
    <td><a href="javascript:lookup_update('<%=ir.getRgdId()%>')">(select)</a></td>
    <td><%=ir.getKeyword()%></td>
    <td align="right"><%=ir.getRgdId()%></td>
    <td>&nbsp;</td>
    <td><%=ir.getObjectType()%></td>
    <td>&nbsp;</td>
    <td><%=ir.getSpeciesTypeKey()%></td>
</tr>
<%
    }
%>

</table>

<% if (lookupList.size() == 0) { %>
  <br>&nbsp;&nbsp;<b>O</b> records found for search term <b>"<%=request.getParameter("search")%>"</b>     

<% } %>
