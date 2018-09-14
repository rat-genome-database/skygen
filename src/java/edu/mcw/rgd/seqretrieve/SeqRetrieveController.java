package edu.mcw.rgd.seqretrieve;

import edu.mcw.rgd.dao.impl.MapDAO;
import edu.mcw.rgd.process.FastaParser;
import edu.mcw.rgd.web.HttpRequestFacade;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: pjayaraman
 * Date: 11/17/11
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeqRetrieveController implements Controller{

    MapDAO mdao = new MapDAO();



    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ArrayList error = new ArrayList();
        ArrayList warning = new ArrayList();
        ArrayList status = new ArrayList();

        HttpRequestFacade req = new HttpRequestFacade(request);
        //status.add("is this the Pathway you are talking about?");
        request.setAttribute("error", error);
        request.setAttribute("status", status);
        request.setAttribute("warn", warning);

        String process = req.getParameter("sequence");
        status.add("Process:"+process + "***");
        String org = req.getParameter("organism");
        String chr = req.getParameter("chrNum");
        String strt = req.getParameter("start").replaceAll(",","");
        String end = req.getParameter("end").replaceAll(",","");
        String submit = req.getParameter("submit");
        String dir="";

        if(process.equals("")){
            //System.out.println("no status!! ");
            Map<String, Integer> chrMapSize = new HashMap<String, Integer>();
            Map<Integer, Map<String, Integer>> mapKeyChrSizesMap = new HashMap<Integer, Map<String, Integer>>();
            List<Map<Integer, Map<String, Integer>>> mapChrSizesList = new ArrayList<Map<Integer, Map<String, Integer>>>();

            try{
                List<edu.mcw.rgd.datamodel.Map> mapLists = mdao.getPrimaryRefAssemblies();
                for(edu.mcw.rgd.datamodel.Map map:mapLists){
                    Map<String, Integer> chrMapSizes = mdao.getChromosomeSizes(map.getKey());
                    mapKeyChrSizesMap.put(map.getSpeciesTypeKey(), chrMapSizes);
                    mapChrSizesList.add(mapKeyChrSizesMap);
                }

                /*edu.mcw.rgd.datamodel.Map chrMap = mdao.getActiveMap(3);
                chrMapSize = mdao.getChromosomeSizes(chrMap.getKey());*/


            }catch (Exception e){
                 e.printStackTrace();
            }


            status.add("hello");

            ModelAndView mv = new ModelAndView("/WEB-INF/jsp/seqretrieve/retrieve.jsp");
            mv.addObject("chrMapSizeLists", mapChrSizesList);
            mv.addObject("chrMapSize", chrMapSize);
            return mv;
        }
        else{
            //System.out.println("processing status!! ");
            if(org.equals("3")){
                dir = "/rgd/gbrowse2/databases/meads_gff3/RatBNfasta/fasta/";

            }else if(org.equals("1")){
                dir =  "/rgd/gbrowse2/databases/meads_gff3/HumanFasta/human371_fasta/";
            }else if(org.equals("")){
                error.add("you need to pick an organism. Rat or Human");
            }
            FastaParser fp = new FastaParser(dir);
            //System.out.println("here is the chr: " + chr + " and start: " + strt + " and stop: " + end);
            String sequence = fp.getSequence(chr, Integer.parseInt(strt), Integer.parseInt(end));
            sequence = sequence.replaceAll(" ","");
            sequence = sequence.replaceAll("\n","");
            //System.out.println("in Controller: " + sequence);
            ModelAndView mvRes = new ModelAndView("/WEB-INF/jsp/seqretrieve/retrieve.jsp");
            mvRes.addObject("result", sequence);
            return mvRes;
        }
    }
}
