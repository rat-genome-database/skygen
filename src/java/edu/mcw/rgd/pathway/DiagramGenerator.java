package edu.mcw.rgd.pathway;

import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.dao.impl.PathwayDAO;
import edu.mcw.rgd.datamodel.Pathway;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import org.apache.commons.io.FileUtils;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: jdepons
 * Date: Feb 15, 2008
 * Time: 10:48:39 AM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Creates pathway diagrams from pathway studio generated HTML.  The class will replace links created
 * by pathway studio to be links to RGD resources.
 */
public class DiagramGenerator {

    //public static String dataDir = "/rgd/data";
    //public static String wwwDir = "/rgd/www";
    PathwayDAO pwDao = new PathwayDAO();
    OntologyXDAO ontDao = new OntologyXDAO();

     private static String uploadingDir;

    public static String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    private static String dataDir;

    public String getUploadingDir() {
        return uploadingDir;
    }

    public void setUploadingDir(String uploadingDir) {
        this.uploadingDir = uploadingDir;
    }




    public static void main (String[] args) throws Exception {
        PathwayDiagramController pg = new PathwayDiagramController();
        //System.out.println(new DiagramGenerator().getImageMap("glycolysis pathway", pg.getUploadingDir() , pg.getDataDir()));
    }

    /**
     * Looks in the cache for a previously generated diagram of the pathway.  If a cache file
     * is not found, the pathway is created, and a cache file is written.
     *
     * The cache file is currently hard coded to be in /rgd_home/3.0/DATA/pathway/ on the server.
     * This should be made configurable in the future.
     * @param ontTermAcc
     * @param wwwDir
     *@param dataDir @return
     * @throws Exception
     * @return html image map of the given ontTermAcc
     */
    public String getImageMap(String ontTermAcc, String wwwDir, String dataDir) throws Exception{

        String pw = ontTermAcc.replaceAll("PW", "PW:");
        Pathway ontologyTerm = pwDao.getPathwayInfo(pw);
        Term pathwayterm = ontDao.getTermByAccId(ontologyTerm.getId());

        //modified by Pushkala 23rd October 2013.
        //String name = ontologyTerm.getName();
        String name = pathwayterm.getTerm();
        if(name.contains("/")){
            name = ontologyTerm.getName().replaceAll("/","-");
        }


        File cache = new File(dataDir+ontTermAcc+"/"+name+".html.cache");

        if (cache.exists()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(cache)));

            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            return sb.toString();
        } else {
            File dir = new File(dataDir+ontTermAcc);
            FileUtils fu = new FileUtils();
            fu.forceMkdir(dir);            
            String path = wwwDir+ontTermAcc+"/"+name+".html";




            String newPath = replaceFunnyChar(path);
            if(newPath.equals("")){
                return "";
            }else{
                Parser parser = new Parser(newPath);

                Node mapNode = parser.extractAllNodesThatMatch(new TagNameFilter("body")).elementAt(0);
                PathwayNodeVisitor pv = new PathwayNodeVisitor();
                pv.setAcc_id(ontTermAcc);
                mapNode.accept(pv);
                String html = mapNode.toHtml();

                FileOutputStream fos = new FileOutputStream(dir+"/"+name+".html.cache");

                fos.write(html.getBytes());
                fos.close();
                return html;
            }
        }
    }


    /**
     * Returns a thumbnail size image of the pathway
     *
     * @param ontTermAcc accession id for ontology term
     * @param wwwDir www dir on server where pathway files are found
     * @throws Exception
     */
    public String getSmallImage(String ontTermAcc, String wwwDir) throws Exception{
        Pathway pathway = pwDao.getPathwayInfo(ontTermAcc);

        if(ontTermAcc.contains(":")){
            ontTermAcc = ontTermAcc.replaceAll(":", "");
        }

        String name="";
        if( pathway!=null ) {
           name=pathway.getName();
            if(name.contains("/")){
                name = name.replaceAll("/","-");
            }
        }

        if (pathway!=null && new File(wwwDir+ontTermAcc+"/"+name+"/pwmap.png").exists()) {
            String html = "<body>";
            html +=  "/pathway/"+ontTermAcc+"/"+name+"/pwmap.png";
            html += "</body>";

            return html;
        }else {
            return "";
        }
    }


       /**
     * Returns a thumbnail size image of the pathway
     *
     * @param ontTermAcc accession id for ontology term
     * @param wwwDir www dir on server where pathway files are found
     * @throws Exception
     */
    public String getGPPFile(String ontTermAcc, String wwwDir) throws Exception{
        Pathway pathway = pwDao.getPathwayInfo(ontTermAcc);

        if(ontTermAcc.contains(":")){
            ontTermAcc = ontTermAcc.replaceAll(":", "");
        }

        String name="";
        if( pathway!=null ) {
           name=pathway.getName();
            if(name.contains("/")){
                name = name.replaceAll("/","-");
            }
        }

        if (pathway!=null && new File(wwwDir+ontTermAcc+"/"+name+"/"+name+".gpp").exists()) {
            String html = "<body>";
            html +=  "/pathway/"+ontTermAcc+"/"+name+"/"+name+".gpp";
            html += "</body>";

            return html;
        }else {
            return "";
        }
    }

    public String replaceFunnyChar(String f) throws Exception{

        File file = new File(f);
        if(file.exists()){
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line, oldtext = "";
            while((line = reader.readLine()) != null){
                oldtext += line + "\r\n";
            }
            reader.close();

             // replace a word in a file
            String newtext = oldtext.replaceAll("\\%5C", "/");


            FileWriter writer = new FileWriter(f);
            writer.write(newtext);
            writer.close();
            return f;
        }else{
            return "";
        }

    }
}
