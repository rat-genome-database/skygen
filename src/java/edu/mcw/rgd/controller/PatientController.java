package edu.mcw.rgd.controller;

import edu.mcw.rgd.dao.impl.SkyGenDAO;
import edu.mcw.rgd.dao.spring.StringMapQuery;
import edu.mcw.rgd.datamodel.Gene;
import edu.mcw.rgd.datamodel.skygen.Patient;
import edu.mcw.rgd.datamodel.skygen.Patient;
import edu.mcw.rgd.process.Utils;
import edu.mcw.rgd.process.mapping.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jdepons
 * Date: 5/7/14
 * Time: 2:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class PatientController implements Controller{

   public enum PATIENT_DATA_TYPE {
        PHENOTYPE, GENE};
    protected String mrn;
    static public String phenotype = "hypertension";
  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

      SkyGenDAO sdao = new SkyGenDAO();

      int pid = Integer.parseInt(request.getParameter("pid"));

      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      String name = auth.getName(); //get logged in username

      if (!sdao.hasPermissions(name, pid)) {
          sdao.log(name, pid, "user tried to view patient but does not have permission");
          throw new Exception("User does not have permission to view this patient");
      }

      String action = request.getParameter("a");
      String geneId = request.getParameter("geneId");

      ArrayList errorList = new ArrayList();

      try {

          Patient patient =  sdao.getPatient(pid);
          request.setAttribute("patient", patient);

          if (geneId != null && geneId.startsWith("RGD_GENE:")) {
              geneId = geneId.substring(9);
          }

          if (request.getParameter("a")==null) {

          } else if (action.equals("getPheno")) {
              List<StringMapQuery.MapPair> phenotypes = sdao.getPatientPhenotypeTerms(pid);
              request.setAttribute("phenotypes", phenotypes);
              return new ModelAndView("/WEB-INF/jsp/site/patient_phenotypes.jsp");
          } else if (action.equals("getGene")) {
              List<StringMapQuery.MapPair> genes = sdao.getPatientGeneSymbols(pid);
              request.setAttribute("genes", genes);
              return new ModelAndView("/WEB-INF/jsp/site/patient_genes.jsp");
          } else if (action.equals("addPheno")) {
              sdao.log(name, pid, "adding phenotype");
              delPhenotype(pid,request.getParameter("phenoId"));
              addPhenotype(pid, request.getParameter("phenoId"));
              List<StringMapQuery.MapPair> phenotypes = sdao.getPatientPhenotypeTerms(pid);
              request.setAttribute("phenotypes", phenotypes);
              return new ModelAndView("/WEB-INF/jsp/site/patient_phenotypes.jsp");
          } else if (action.equals("addGene")) {
              sdao.log(name, pid, "adding gene");
              addGene(pid, request.getParameter("geneId"));
              List<StringMapQuery.MapPair> genes = sdao.getPatientGeneSymbols(pid);
              request.setAttribute("genes", genes);
              return new ModelAndView("/WEB-INF/jsp/site/patient_genes.jsp");
          } else if (action.equals("delPheno")) {
              sdao.log(name, pid, "deleting phenotype" + request.getParameter("phenoId") );
              delPhenotype(pid,request.getParameter("phenoId"));
              List<StringMapQuery.MapPair> phenotypes = sdao.getPatientPhenotypeTerms(pid);
              request.setAttribute("phenotypes", phenotypes);
              return new ModelAndView("/WEB-INF/jsp/site/patient_phenotypes.jsp");
          } else if (action.equals("delGene")) {
              sdao.log(name, pid, "deleting gene" + geneId);
              delGene(pid, geneId);
              List<StringMapQuery.MapPair> genes = sdao.getPatientGeneSymbols(pid);
              request.setAttribute("genes", genes);
              return new ModelAndView("/WEB-INF/jsp/site/patient_genes.jsp");
          } else if (action.equals("searchOntomate")) {
              sdao.log(name, pid, "searching ontomate");
              if (request.getParameter("q").equals("allGenes")) {
                  List<StringMapQuery.MapPair> genes = sdao.getPatientGeneSymbols(pid);
                  request.setAttribute("genes", genes);
              }
              if (request.getParameter("q").equals("allPhenotypes")) {
                  List<StringMapQuery.MapPair> phenotypes = sdao.getPatientPhenotypeTerms(pid);
                  request.setAttribute("phenotypes", phenotypes);
              }
              if (request.getParameter("q").equals("all")) {
                  List<StringMapQuery.MapPair> phenotypes = sdao.getPatientPhenotypeTerms(pid);
                  request.setAttribute("phenotypes", phenotypes);
                  List<StringMapQuery.MapPair> genes = sdao.getPatientGeneSymbols(pid);
                  request.setAttribute("genes", genes);
              }
              return new ModelAndView("/WEB-INF/jsp/site/searchOntomate.jsp");
          } else if (action.equals("replacePheno")) {
              sdao.log(name, pid, "replacing phenotype " + request.getParameter("oldPhenoId") + " with " + request.getParameter("phenoId"));

              delPhenotype(pid,request.getParameter("oldPhenoId"));
              addPhenotype(pid, request.getParameter("phenoId"));
              List<StringMapQuery.MapPair> phenotypes = sdao.getPatientPhenotypeTerms(pid);
              request.setAttribute("phenotypes", phenotypes);
              return new ModelAndView("/WEB-INF/jsp/site/patient_phenotypes.jsp");
          }else if (action.equals("upload")) {
              sdao.log(name, pid, "uploading gene list");

              HashMap hm = new HashMap();

              List<StringMapQuery.MapPair> current = sdao.getPatientGeneSymbols(pid);

              for (StringMapQuery.MapPair gene : current) {
                hm.put(Integer.parseInt(gene.keyValue),null);

              }

              List<String> symbols = symbols= Utils.symbolSplit(request.getParameter("genes"));
              ObjectMapper om = new ObjectMapper();
              om.mapSymbols(symbols, 1);

              Iterator it = om.getMapped().iterator();
              while (it.hasNext()) {

                  Object obj = it.next();

                  if (obj instanceof Gene) {
                    Gene g = (Gene) obj;
                    if (!hm.containsKey(g.getRgdId())) {
                        addGene(pid, g.getRgdId() + "");
                        hm.put(g.getRgdId(),null);
                    }
                  }
              }

              request.setAttribute("log", om.getLog());

          }
      }catch (Exception e) {
          e.printStackTrace();
          errorList.add(e.getMessage());
      }

      request.setAttribute("error",errorList);


      return new ModelAndView("/WEB-INF/jsp/site/patient.jsp");
    }

    static public void addPhenotype(int pid, String phenotypeId) throws Exception{
 	    SkyGenDAO sgDao = new SkyGenDAO();
 	    sgDao.insertPatientPhenotype(pid, phenotypeId);
 	}

 	static public void delPhenotype(int pid, String phenotypeId) throws Exception{
     	SkyGenDAO sgDao = new SkyGenDAO();
     	sgDao.deletePatientPhenotype(pid, phenotypeId);
 	}

 	static public void addGene(int pid, String geneId) throws Exception{
    	SkyGenDAO sgDao = new SkyGenDAO();
 	    sgDao.insertPatientGene(pid, geneId);
 	}

 	private void delGene(int pid, String geneId) throws Exception{
     	SkyGenDAO sgDao = new SkyGenDAO();
 	    sgDao.deletePatientGene(pid, geneId);
 	}
}
