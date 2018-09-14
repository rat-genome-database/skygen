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
        <td colspan='8'><span style="font-weight:700;font-size:22px;">Genes</span></td>
        <%@ include file="menu.jsp"%>

    </tr>
</table>




<% if (result.getAnnotatedGenes().size() ==0) { %>

    <br><br>&nbsp;&nbsp;&nbsp;0 Genes Found<br>

<%
    }

       Iterator it = result.getAnnotatedGenes().keySet().iterator();

       while (it.hasNext()) {

        String key = (String) it.next();

        Gene g = (Gene) result.getAnnotatedGenes().get(key);

       %>
    <table border=0 style="margin-top:15px; border: 1px solid black;" width=100%>
    <tr>
        <td>
            <table cellpadding="0" cellspacing="0" border=0 width="95%">
                <tr>
                    <td width=3%>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                    <td width=65%  colspan=2><span style="font-size:16px;font-weight:700;"><%=g.getSymbol()%> (<%=g.getName()%>)</span></td>
                    <td>
                        <table>
                            <tr>
                                <td><a style="font-size:13px;" target="_blank" href="http://ghr.nlm.nih.gov/gene/<%=g.getSymbol()%>">GHR</a></a></td>
                                <td><a style="font-size:13px;" target="_blank" href="http://www.ncbi.nlm.nih.gov/omim/?term=<%=g.getSymbol()%>">OMIM</a></td>
                                <td><a style="font-size:13px;"  target="_blank" href="http://rgd.mcw.edu/<%=Link.it(g.getRgdId())%>">RGD</a></td>
                            </tr>
                        </table>

                    </td>
                    <td  align="center" width=20% colspan=2><span style="font-size:12px;font-weight:700;"><%=result.getTermCount(g.getRgdId() + "")%>&nbsp;Terms&nbsp;-&nbsp;<%=result.getVariantCountForGene(g.getRgdId()+ "")%>&nbsp;Variants</span></td>
                    <td width=10%><a href="javascript:toggle('<%=g.getRgdId()%>')">show/hide&nbsp;detail</a></td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td>
        <table id="<%=g.getRgdId()%>" style="display:none;">
            <% if (g.getDescription() != null) { %>
                <tr>
                <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td colspan=2><span style="font-size:12px;"><%=g.getDescription()%></td>
                </tr>
            <% }

            for (Term thisTerm: result.getTerms(g.getRgdId() + "")) {
            %>

                    <tr>
                        <td width=10>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                        <td width=10>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                        <td style='font-weight:700;'>

                        <div style="background-color:white; margin-top:10px;">

                            <table>
                                <tr>
                                    <td style="font-weight:700;"><%=thisTerm.getTerm()%></td>
                                    <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                                    <td><a href='javascript:showOntTree("<%=thisTerm.getAccId()%>");'><img src='/skygen/common/images/tree.png'/></a></td>
                                    <td><a style="font-size:13px;" target="_blank" href="http://ghr.nlm.nih.gov/search?query=<%=thisTerm.getTerm()%>&Search=">GHR</a></a></td>
                                    <td><a style="font-size:13px;" target="_blank" href="http://www.ncbi.nlm.nih.gov/omim/?term=<%=thisTerm.getTerm()%>">OMIM</a></td>
                                    <td><a style="font-size:13px;"  target="_blank" href="http://rgd.mcw.edu/<%=Link.ontAnnot(thisTerm.getAccId())%>">RGD</a></td>
                                </tr>
                            </table>

                        </div>

                        <% if (thisTerm.getDefinition() != null) { %>
                        <div style="font-size:12px; font-weight: 200;"><%=thisTerm.getDefinition()%></div>
                        <% } %>
                        <div style="margin-top:10px;"><%=result.getGenomicElements(thisTerm.getAccId(), g).size()%> ClinVar variants found in <%=g.getSymbol()%>
                            <a href="javascript:toggle('<%=thisTerm.getAccId()%>')">show/hide variants</a>
                        </div>

                        <div id="<%=thisTerm.getAccId()%>" style='width:500px; border: 1px solid blue; background-color:white; display:none;'>

                        <table border=0 width=100%>
                            <tr>
                                <td colspan=3 align="right"><a href='clinVar.html?pid=<%=request.getParameter("pid")%>&d=1&dGene=<%=g.getRgdId()%><%=gList%><%=pList%>'>Download Variant File</a></td>
                            </tr>
                        <%  int cnt=1;
                            for (GenomicElement ge: result.getGenomicElements(thisTerm.getAccId(), g)) { %>
                            <tr>
                                <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                                <td align="right"><%=cnt++%>.&nbsp;</td>
                                <td><a style='font-size:12px;' href='http://rgd.mcw.edu/<%=Link.it(ge.getRgdId())%>'><%=ge.getName()%></a></td>

                            </tr>
                        <% } %>
                        </table>
                        </div>
                        </td>
                    </tr>

              <%}%>

        </table>
    </td>
    </tr>
    </table>
<% } %>


<!--        <div style="font-size:20px;margin-top: 20px;">0 Clinvar annotations found connecting Phenotype/Disease terms and gene list</div>-->

<br>
<br>
<br>
<br>
<%@ include file="/common/footerarea.jsp"%>



