<%@ page import="edu.mcw.rgd.dao.spring.StringMapQuery" %>
<%@ page import="java.util.List" %>
<%@ page import="edu.mcw.rgd.datamodel.ontology.Annotation" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.google.gson.internal.StringMap" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="edu.mcw.rgd.datamodel.ontologyx.Term" %>
<%@ page import="edu.mcw.rgd.dao.impl.*" %>
<%@ page import="edu.mcw.rgd.datamodel.GenomicElement" %>
<%@ page import="edu.mcw.rgd.reporting.Link" %>
<%@ page import="edu.mcw.rgd.datamodel.Gene" %>
<%@ page import="edu.mcw.rgd.clinvar.ClinVarSearchResult" %>
<%@ page import="edu.mcw.rgd.datamodel.ontology.SNVAnnotation" %>
<%@ page import="java.util.Iterator" %>

<%@ include file="/common/headerarea.jsp"%>

<script>
function toggle(divId) {
   if (document.getElementById(divId).style.display == "none") {
       document.getElementById(divId).style.display = "block";
   }else {
       document.getElementById(divId).style.display = "none";
   }

}
</script>


<%
    ClinVarSearchResult result=(ClinVarSearchResult)request.getAttribute("searchResult");
%>

<%
String gList = "";
String pList = "";

if (request.getParameter("gList") != null) {
   gList="&gList=" + request.getParameter("gList");
}

if (request.getParameter("pList") != null) {
    pList="&pList=" + request.getParameter("pList");
}



%>

<br>
<table border=0 width='100%'>
    <tr>
        <td colspan='8'><span style="font-weight:700;font-size:22px;">Gene List Inspector</span></td>
        <%@ include file="menu.jsp"%>
    </tr>
</table>

<table cellpadding="10">
    <tr>
<!--
        <td valign="top">
            <table style="border: 1px dashed black; padding:10px;">
                <tr>
                    <td colspan=3 style="background-color:white; font-size:18px;font-weight:700;padding:5px;">Phenotype and Disease List</td>
                </tr>

                <%  for (StringMapQuery.MapPair phenotype : result.getPatientPhenotypes()) {    %>

                    <tr><td>&nbsp;&nbsp;&nbsp;</td><td colspan=2><li><%=phenotype.stringValue%></li><td></tr>
                    <%  if (result.getHpToRDOXConversions().get(phenotype.keyValue) != null) {  %>
                            <%
                            for (Term t: result.getHpToRDOXConversions().get(phenotype.keyValue)) {
                    %>
                                <tr>
                                    <td></td>
                                    <td>&nbsp;</td>
                                    </td><td>-&nbsp;<%=t.getTerm()%>&nbsp;<span style="font-size:12px;">(via OMIM)<span></td>
                                </tr>

                    <%      }
                        }
                    %>

                <%
                    }
                %>

            </table>
        </td>
-->
        <td valign="top">
            <table style="border: 1px dashed black; padding:10px;">
                <tr>
                    <td colspan=2 style="background-color:white; font-size:18px;font-weight:700;padding:5px;">Annotated&nbsp;To&nbsp;Phenotype&nbsp;List</td>
                </tr>
                <%
                int i=1;

                Iterator it = result.getAnnotatedGenes().keySet().iterator();


                //for (StringMapQuery.MapPair gene : result.getAnnotatedPatientGenes()) {
                while (it.hasNext()) {
                    String key = (String) it.next();
                    Gene g = result.getAnnotatedGenes().get(key);

                %>
                    <tr>
                        <td width=10>&nbsp;&nbsp;</b><%=g.getSymbol()%></td>
                    </tr>
                <% }  %>
            </table>
        </td>
        <td valign="top">
            <table style="border: 1px dashed black; padding:10px;">
                <tr>
                    <td colspan=2 style="background-color:white; font-size:18px;font-weight:700;padding:5px;">Unannotated&nbsp;to&nbsp;Phenotype&nbsp;List</td>
                </tr>

                <%
                i=1;
                for (StringMapQuery.MapPair gene : result.getUnannotatedPatientGenes()) {
                %>
                    <tr>
                        <td width=10>&nbsp;&nbsp;</b><%=gene.stringValue%></td>
                    </tr>
                <% }  %>
            </table>
        </td>
    </tr>
</table>













<br>
<br>
<br>
<br>
<%@ include file="/common/footerarea.jsp"%>



