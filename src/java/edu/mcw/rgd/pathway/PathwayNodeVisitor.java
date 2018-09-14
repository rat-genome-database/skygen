package edu.mcw.rgd.pathway;

import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.reporting.Link;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.StringFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.NodeVisitor;

/**
 * Created by IntelliJ IDEA.
 * User: jdepons
 * Date: Feb 18, 2008
 * Time: 10:53:59 AM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Visitor class used to operate on nodes of the html file built by pathway studio.  When
 * nodes that contain links to resources contained in RGD are found, the link is replaced with
 * a link to RGD
 */
public class PathwayNodeVisitor extends NodeVisitor {

       private static String uploadingDir;

    public static String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    private static String dataDir;
       OntologyXDAO ox = new OntologyXDAO();

    public String getAcc_id() {
        return acc_id;
    }

    public void setAcc_id(String acc_id) {
        this.acc_id = acc_id;
    }

    private String acc_id;




    /**
     *When nodes that contain links to resources contained in RGD are found, the link is replaced with 
     * a link to RGD*
     * @param tag
     */

    public void visitTag(Tag tag){
        String mainTag = "";
        if (tag.getTagName().toLowerCase().equals("area")) {

            String href = tag.getAttribute("href");
            if(href.contains("%5C")){
                href = href.replaceAll("\\%5C","/");
            }

            tag.removeAttribute("target");

            if(href.contains("http://")){
                mainTag = href;
                tag.setAttribute("href", mainTag);
            }else{
                try {

                    //Parser parser = new Parser(wwwDir+ "/pathway/"+ href);
                    //Parser parser = new Parser(uploadingDir+acc_id+"/"+href);
                    Parser parser = new Parser(uploadingDir+acc_id+"/"+href);

                    parser.reset();
                    NodeList nl = parser.extractAllNodesThatMatch(new StringFilter("RGD ID"));

                    String rgdId = "";

                    if (nl.size() > 0) {
                        rgdId = nl.elementAt(0).getParent().getNextSibling().getNextSibling().getChildren().elementAt(0).toHtml().trim();
                        tag.setAttribute("href", Link.gene(Integer.parseInt(rgdId)));
                        return;
                    }

                    parser.reset();

                    nl = parser.extractAllNodesThatMatch(new StringFilter("PW"));

                    String pwId = "";

                    if (nl.size() > 0) {
                        pwId = nl.elementAt(0).getParent().getNextSibling().getNextSibling().getChildren().elementAt(0).toHtml().trim();
                        tag.setAttribute("href","/rgdweb/ontology/annot.html?acc_id=" + pwId);
                        return;
                    }

                    parser.reset();

                    nl = parser.extractAllNodesThatMatch(new StringFilter("ListLink"));

                    String link = "";

                    if (nl.size() > 0) {
                        //System.out.println("ListLink here******: "+nl.elementAt(0).getParent().getNextSibling().getNextSibling().getChildren());
                        //link = nl.elementAt(0).getParent().getNextSibling().getNextSibling().getChildren().elementAt(0).toHtml().trim();
                        for (int i=0; i< nl.elementAt(0).getParent().getNextSibling().getNextSibling().getChildren().size(); i++) {
                            if (nl.elementAt(0).getParent().getNextSibling().getNextSibling().getChildren().elementAt(i) instanceof org.htmlparser.tags.LinkTag) {
                                link = nl.elementAt(0).getParent().getNextSibling().getNextSibling().getChildren().elementAt(i).getChildren().elementAt(0).toHtml().trim();
                            }
                        }
                        tag.setAttribute("href",link);
                       // System.out.println("Link Goes here******: "+link);
                        return;
                    }

                    parser.reset();

                    nl = parser.extractAllNodesThatMatch(new StringFilter("LinkURL"));

                    String url = "";

                    if (nl.size() > 0) {
                       // System.out.println("LinkUrl here******: "+nl.elementAt(0).getParent().getNextSibling().getNextSibling().getChildren());
                        for (int i=0; i< nl.elementAt(0).getParent().getNextSibling().getNextSibling().getChildren().size(); i++) {
                            if (nl.elementAt(0).getParent().getNextSibling().getNextSibling().getChildren().elementAt(i) instanceof org.htmlparser.tags.LinkTag) {
                                url = nl.elementAt(0).getParent().getNextSibling().getNextSibling().getChildren().elementAt(i).getChildren().elementAt(0).toHtml().trim();
                            }
                        }
                        tag.setAttribute("href",url);
                        //System.out.println("Link Goes here******: "+url);
                        return;
                    }

                    tag.removeAttribute("href");

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (tag.getTagName().toLowerCase().equals("img")) {
            String ref = tag.getAttribute("src");

            

            tag.setAttribute("src", "/pathway/"+acc_id+"/"+ ref);
            return;
        }
    }

    public String getUploadingDir() {
           return uploadingDir;
       }

       public void setUploadingDir(String uploadingDir) {
           this.uploadingDir = uploadingDir;
       }

}
