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
        <td ><span style="font-weight:700;font-size:22px;">Disease</span></td>
        <%@ include file="menu.jsp"%>
    </tr>
</table>

   <% if (result.getAnnotatedTerms().size() ==0) { %>

       <br><br>&nbsp;&nbsp;&nbsp;0 Annotations Found<br>

   <%
    }

   Iterator it = result.getAnnotatedTerms().keySet().iterator();
   while (it.hasNext()) {
        String key = (String) it.next();
        Term t = (Term) result.getAnnotatedTerms().get(key);

    %>
    <table border=0 style="margin-top:15px; border: 1px solid black;" width=100%>
    <tr>
        <td>
            <table cellpadding="0" cellspacing="0" border=0 width="95%">
                <tr>
                    <td width=3%>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                    <td width=65%  colspan=2><span style="font-size:16px;font-weight:700;"><%=t.getTerm()%></span></td>
                    <td>
                        <table>
                            <tr>
                                <td><a href='javascript:showOntTree("<%=t.getAccId()%>");'><img src='/skygen/common/images/tree.png'/></a></td>
                                <td><a style="font-size:13px;" target="_blank" href="http://ghr.nlm.nih.gov/search?query=<%=t.getTerm()%>&Search=">GHR</a></a></td>
                                <td><a style="font-size:13px;" target="_blank" href="http://www.ncbi.nlm.nih.gov/omim/?term=<%=t.getTerm()%>">OMIM</a></td>
                                <td><a style="font-size:13px;"  target="_blank" href="http://rgd.mcw.edu/<%=Link.ontAnnot(t.getAccId())%>">RGD</a></td>
                            </tr>
                        </table>
                    </td>
                    <td  align="center" width=20% colspan=2><span style="font-size:12px;font-weight:700;"><%=result.getGeneCount(t.getAccId())%>&nbsp;Genes&nbsp;-&nbsp;<%=result.getVariantCount(t.getAccId())%>&nbsp;Variants</span></td>
                    <td width=10%><a href="javascript:toggle('<%=t.getAccId()%>')">show/hide&nbsp;detail</a></td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td>
        <table id="<%=t.getAccId()%>" style="display:none;">
            <% if (t.getDefinition() != null) { %>
                <tr>
                <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td colspan=2><span style="font-size:12px;"><%=t.getDefinition()%></td>
                </tr>
            <% }

                String hpTerm="";

                if (result.getHpToRDOXConversions().containsKey(t.getAccId())) {
                    boolean first = true;
                    for (Term term: result.getHpToRDOXConversions().get(t.getAccId())) {
                        if (first) {
                            first = false;
                        }else {
                            hpTerm += ", ";
                        }
                        hpTerm += "<a href='http://rgd.mcw.edu/" + Link.ontAnnot(term.getAccId()) + "' style='color:white;'>" + term.getTerm() + "</a>";
                    }
                }

                String dTerm="";
                if (result.getRelatedDiseases().containsKey(t.getAccId())) {
                    boolean first = true;
                    for (Term term: result.getRelatedDiseases().get(t.getAccId())) {
                        if (first) {
                            first = false;
                        }else {
                            dTerm += ", ";
                        }
                        dTerm += "<a href='http://rgd.mcw.edu/" + Link.ontAnnot(term.getAccId()) + "' style='color:white;'>" + term.getTerm() + "</a>";
                    }
                }

            if (!hpTerm.equals("")) {
            %>
                <tr>
                <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td colspan=2 style="background-color:#053563; color:white;" >Annotation based on link to OMIM phenotype <%=hpTerm%></td>
                </tr>
            <% }

            if (!dTerm.equals("")) {
            %>
                <tr>
                <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td colspan=2 style="background-color:#053563; color:white;" >Annotated term has an ontological parent/child relationship to <%=dTerm%></td>
                </tr>
            <% }

            for (Gene thisGene: result.getGenes(t.getAccId())) {
            %>

                    <tr>
                        <td width=10>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                        <td width=10>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                        <td style='font-weight:700;'>

                        <div style="background-color:white; margin-top:10px;">
                                <table>
                                    <tr>
                                        <td style="font-weight:700;"> <%=thisGene.getSymbol()%> (<%=thisGene.getName()%>)</td>
                                        <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                                        <td><a style="font-size:13px;" target="_blank" href="http://ghr.nlm.nih.gov/gene/<%=thisGene.getSymbol()%>">GHR</a></a></td>
                                        <td><a style="font-size:13px;" target="_blank" href="http://www.ncbi.nlm.nih.gov/omim/?term=<%=thisGene.getSymbol()%>">OMIM</a></td>
                                        <td><a style="font-size:13px;"  target="_blank" href="http://rgd.mcw.edu/<%=Link.it(thisGene.getRgdId())%>">RGD</a></td>
                                    </tr>
                                </table>
                        </div>

                        <% if (thisGene.getDescription() != null) { %>
                        <div style="font-size:12px; font-weight: 200;"><%=thisGene.getDescription()%></div>
                        <% } %>
                        <div style="margin-top:10px;"><%=result.getGenomicElements(t.getAccId(), thisGene).size()%> ClinVar variants found in <%=thisGene.getSymbol()%>
                            <a href="javascript:toggle('<%=thisGene.getRgdId()%>')">show/hide variants</a>
                        </div>

                        <div id="<%=thisGene.getRgdId()%>" style='width:500px; border: 1px solid blue; background-color:white; display:none;'>

                        <table border=0 width=100%>
                            <tr>
                                <td colspan=3 align="right"><a href='clinVar.html?pid=<%=request.getParameter("pid")%>&d=1&dGene=<%=thisGene.getRgdId()%><%=gList%><%=pList%>'>Download Variant File</a></td>
                            </tr>
                        <%  int cnt=1;
                            for (GenomicElement ge: result.getGenomicElements(t.getAccId(), thisGene)) { %>
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



