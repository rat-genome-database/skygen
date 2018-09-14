package edu.mcw.rgd.ontology;

import edu.mcw.rgd.datamodel.ontologyx.TermWithStats;
import edu.mcw.rgd.pathway.PathwayDiagramController;
import edu.mcw.rgd.process.Utils;
import edu.mcw.rgd.reporting.Link;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: mtutaj
 * Date: 10/17/13
 * Time: 2:25 PM
 * <p>
 * ontology tree browser: a header pane + 3 term panes
 * </p>
 */
public class OntBrowser extends SimpleTagSupport {

    OntViewBean bean = null;
    private String url;
    private String offset = "";
    private String opener_sel_acc_id;
    private String opener_sel_term;
    private boolean showSelectButton;
    private boolean alwaysShowSelectButton = false;
    private boolean iframe = false;

    public void setAcc_id(String accId) {

        bean = new OntViewBean();
        try {
            OntViewController.populateBean(accId, null, bean);
        } catch (Exception e) {
            System.out.println("<h1>ERROR</h1><pre>" + e.toString() + "</pre>");
        }
    }

    public void setUrl(String url) {
        this.url = url.indexOf('?')>=0 ? url+"&" : url+"?";

        if( this.url.contains("always_select=1") || this.url.contains("select_always=1") )
            alwaysShowSelectButton = true;

        if( this.url.contains("mode=iframe") )
            iframe = true;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public void setOpener_sel_acc_id(String opener_sel_acc_id) {
        this.opener_sel_acc_id = opener_sel_acc_id;
    }

    public void setOpener_sel_term(String opener_sel_term) {
        this.opener_sel_term = opener_sel_term;
    }

    private boolean canShowSelectButton() {

        return !Utils.isStringEmpty(this.opener_sel_acc_id) ||
               !Utils.isStringEmpty(this.opener_sel_term);
    }


    public void doTag() throws JspException, IOException {

        showSelectButton = canShowSelectButton();

        JspWriter out = this.getJspContext().getOut();

        out.print(generateHeader());
        out.print(generateThreePanes());
        out.print(getScript());
    }

    private String generateHeader() {
        int annotCount = ((TermWithStats)bean.getTerm()).getAnnotCountForTermAndChilds();
        String html =
        "<div style=\"border: 1px solid black;  background-color:#F6F6F6; margin: 5px; padding:5px; \">\n"+
        "<table border=0 align=\"center\" >\n"+
        "  <tr>\n"+
        "    <td valign=\"top\" align=\"center\"  colspan=2 style=\"font-size:18px; color:#2865A3; font-weight:700;\">"+bean.getTerm().getTerm()
                +" <span style=\"font-size:14px;\">("+bean.getAccId()+")</span></td>\n"+
        "  </tr>\n"+

        "  <tr>\n"+
        "   <td colspan=2 style=\"font-size:14px;\" align=\"center\">"+annotCount+" annotations.";
        if( annotCount>0 ) {
            html += "<a href=\""+ Link.ontAnnot(bean.getAccId())+"\">(View Annotations)</a>";
        }
        html +=
        "     </td>\n"+
        "  </tr>\n"+
        "</table>\n"+
        "</div>\n"+

        "<table width=\"100%\">\n"+
        "<tr>\n"+
        "  <td width=\"30%\" align=\"center\" style=\"font-weight:700; color: white; background-image: url(/skygen/common/images/bg3.png);\">Parent Terms</td>\n"+
        "  <td width=\"40%\" align=\"center\" style=\"font-weight:700;color: white; background-image: url(/skygen/common/images/bg3.png);\">Term With Siblings</td>\n"+
        "  <td width=\"30%\" align=\"center\" style=\"font-weight:700;color: white; background-image: url(/skygen/common/images/bg3.png);\">Child Terms</td>\n"+
        "</tr>\n"+
        "</table>\n";
        return html;
    }

    private String generateThreePanes() {
        String html =
        "<div id=\"browser\" style=\"padding-top:0px\">\n"+
        " <table border=0 width=100%>\n"+
        "  <tr>\n";

        html += generateParentTermsPane();
        html += generateSiblingTermsPane();
        html += generateChildTermsPane();

        html +=
       "  </tr>\n"+
       " </table>\n"+
       "</div>\n";

        return html;
    }

    private String generateParentTermsPane() {
        return generateTermsPane("30%", "parent_terms", bean.getAncestorTerms(), "right");
    }

    private String generateSiblingTermsPane() {
        String out =
        "<td valign=\"top\" width=40%>\n"+
        "  <div id=\"viewer\" class=\"tree_box\">\n";

        // compute row nr for selected term
        int row = 0; // current row nr
        int selRow = 0; // row nr for selected term
        for( OntDagNode node: bean.getSiblingTerms() ) {

            row++; // next row nr for sibling
            if( node.getTermAcc().equals(bean.getAccId())) {
                selRow = row; // the sibling term is the selected term!!!
                break;
            }
        }
        row = 0; // row nr for sibling term

        for( OntDagNode node: bean.getSiblingTerms() ) {
            row++; // next row nr for sibling

            if( row == selRow ) {
                out += "<div id=\"sibling_terms\" >\n";
            }

            out += "<div class='tp'>";

            if( showSelectButton )
                out+= generateSelectButton(node);

            out += "<span class='sibterm' id='"+node.getTermAcc()+"' onclick=keepY(this) >"+node.getTerm().replace('_', ' ')+"</span>";

            // write '+' if child terms are present
            // show link to annotations, if there are any
            // show link to pathway diagram, if any
            // show link to pathway diagram, if any
            out += generateIcons(node);

            if( row == selRow ) {
                // selected term: show link to strain report page, if applicable at the end of the line
                if( bean.getStrainRgdId()!=0 ) {
                    out += "&nbsp; &nbsp; <a href=\""+ Link.strain(bean.getStrainRgdId())+"\"> (View Strain Report)</a>\n";
                }

                // selected term: show term definition below term name, if available
                String definition = Utils.defaultString(bean.getTerm().getDefinition());
                // display pathway mini diagram if available
                if( bean.getAccId().startsWith("PW") ) {
                    out += generatePathwayMiniDiagram(definition);
                } else {
                    out += "<br><div class=\"seltermdef\">"+definition+"</div>\n";
                }
                out += "</div>";
            } else {
                out += "</br>";
            }

            out +="</div>\n";
        }

        return out +
        "  </div>\n"+
        "</td>\n";
    }


    private String generateChildTermsPane() {
        return generateTermsPane("30%", "child_terms", bean.getChildTerms(), "left");
    }

    private String generateTermsPane(String width, String id, Collection<OntDagNode> nodes, String showRelImages) {

        String out =
        "<td valign=\"top\" width="+width+">\n"+
        "  <div id=\""+id+"\" class=\"tree_box\">\n";

        for( OntDagNode node: nodes ) {
            out += "<div class='tp'>";

            if( showSelectButton )
                out+= generateSelectButton(node);

            // term relation image -- could be null
            if( showRelImages.equals("left") ) {
                String image = OntViewBean.getRelationImage(node.getOntRel());
                if( image!=null ) {
                    out += "<img class='rel' src='/skygen/common/images/"+image+"' title='"+node.getOntRel()+"'>&nbsp;";
                }
            }

            out += "<a id=\""+node.getTermAcc()+"\" href=\""+this.url+"acc_id="+node.getTermAcc()+"\">"+node.getTerm().replace('_', ' ')+"</a>";

            // write '+' if child terms are present
            // show link to annotations, if there are any
            // show link to pathway diagram, if any
            // show link to pathway diagram, if any
            out += generateIcons(node);

            // term relation image -- could be null
            if( showRelImages.equals("right") ) {
                String image = OntViewBean.getRelationImage(node.getOntRel());
                if( image!=null ) {
                    //out += "<div class='prel'>";
                    //out += "<img class='rel' src='/common/images/"+image+"'>";
                    //out += "</div>";
                    out += "&nbsp;&nbsp;<img class='rel' src='/skygen/common/images/"+image+"' title='"+node.getOntRel()+"'>";
                }
            }

            out +="</div>\n";
        }

        return out +
        "  </div>\n"+
        "</td>\n";
    }

    // child count icon '+'
    // annotation icon [A]
    // pathway diagram icon [D]
    private String generateIcons(OntDagNode node) {

        String out = "";

        // write '+' if child terms are present
        if( node.getChildCount()>0 ) {
            out += "<span title=\""+node.getChildCount()+" child terms\" class='cc'>&nbsp;+</span> ";
        }

        // show link to annotations, if there are any
        if( node.getAnnotCountForTermAndChilds()>0 ) {
            out += "&nbsp;<a class='annotlnk' title=\"show term annotations\" href=\""+ Link.ontAnnot(node.getTermAcc())+"\"></a>";
        }

        // show link to pathway diagram, if any
        if( node.isHasPathwayDiagram() ) {
            out += "&nbsp;<a class='diaglnk' title=\"view interactive pathway diagram\" href=\"/rgdweb/pathway/pathwayRecord.html?acc_id="+node.getTermAcc()+"\"></a>";
        }

        // show link to pathway diagram, if any
        if( node.getCountOfPathwayDiagramsForTermChilds()>0 ) {
            out += "&nbsp;<span class='diaglnkdot' title=\""+node.getCountOfPathwayDiagramsForTermChilds()+" child term(s) have interactive pathway diagrams\"></span>";
        }

        return out;
    }

    private String generatePathwayMiniDiagram(String definition) {

        String diagramImageUrl;
        try {
            diagramImageUrl = new PathwayDiagramController().generateContent("small", bean.getAccId());
        }
        catch( Exception e ) {
            diagramImageUrl = "";
        }

        //String diagramImageUrl = "/pathway/PW0000363/leptin%20system%20pathway/pwmap.png";
        if( !diagramImageUrl.isEmpty() ) {
            return
               "<table>"
             + "  <tr>"
             + "    <td><div class=\"seltermdef\">"+definition+"</div></td>"
             + "    <td valign=\"top\" style=\"\">"
             + "     <a href=\"/rgdweb/pathway/pathwayRecord.html?acc_id="+bean.getAccId()+"\" title=\"view interactive pathway diagram\">"
             + "     <img src=\""+diagramImageUrl+"\" alt=\"\" style=\"width:150px;\" border=\"1\"/><br/>"
             + "     View Interactive Diagram</a>"
             + "    </td>"
             + " </tr>"
             + "</table>";
        } else {
            return "<br><div class=\"seltermdef\">"+definition+"</div>\n";
        }
    }

    private String generateSelectButton(OntDagNode node) {

        if( alwaysShowSelectButton || node.getAnnotCountForTermAndChilds()>0 ) {
            return "<span class='term_select' onclick=\"selectTerm('"+node.getTermAcc()+"','"+node.getTerm().replace("'", "\\'")+"')\">select</span>&nbsp;";
        } else {
            return "<span class='term_select_disabled'>select</span>&nbsp;";
        }
    }

    private String getScript() {
        String selectTermFunction = "function selectTerm(accId,termName) {\n";
        String opener = iframe ? "  window.parent" : "  window.opener";
        if( !Utils.isStringEmpty(this.opener_sel_acc_id) ) {
            selectTermFunction += opener + (iframe ? ".postMessage(accId+'|'+termName, '*');\n" :
                     ".document.getElementById('"+this.opener_sel_acc_id+"').value=accId;\n");
        }
        if( !Utils.isStringEmpty(this.opener_sel_term) ) {
            selectTermFunction +=
                opener + ".document.getElementById('"+this.opener_sel_term+"').value=termName;\n";
        }
        selectTermFunction += "  window.close();\n";
        selectTermFunction += "}\n";

        return "<script>\n" +
                selectTermFunction +
                "\n" +
                "    function keepY(obj) {\n" +
                "        var v = document.getElementById(\"viewer\");\n" +
                "        var offset = obj.offsetTop - v.scrollTop;\n" +
                "        location.href=\""+this.url+"acc_id=\" + obj.id + \"&offset=\" + offset ;\n" +
                "    }\n" +
                "\n" +
                "    function loadIt() {\n" +
                "        var obj = document.getElementById(\""+bean.getAccId()+"\");\n" +
                "        objTop = obj.offsetTop;\n" +
                "        var offset = "+this.offset+";\n" +
                "\n" +
                "        if (offset) {\n" +
                "            document.getElementById(\"viewer\").scrollTop=objTop - offset;\n" +
                "        }else {\n" +
                "            document.getElementById(\"viewer\").scrollTop=objTop - 11;\n" +
                "        }\n" +
                "    }\n" +
                "    onload=loadIt;\n" +
                "\n" +
                "</script>";
    }
}
