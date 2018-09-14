package edu.mcw.rgd.controller;

import edu.mcw.rgd.clinvar.ClinVarSearchResult;
import edu.mcw.rgd.dao.impl.*;
import edu.mcw.rgd.dao.spring.StringMapQuery;
import edu.mcw.rgd.datamodel.Gene;
import edu.mcw.rgd.datamodel.GenomicElement;
import edu.mcw.rgd.datamodel.MapData;
import edu.mcw.rgd.datamodel.ontology.Annotation;
import edu.mcw.rgd.datamodel.ontology.SNVAnnotation;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jdepons
 * Date: 6/2/14
 * Time: 11:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class ClinVarController implements Controller {

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ArrayList errorList = new ArrayList();
        SkyGenDAO sdao = new SkyGenDAO();
        int pid = Integer.parseInt(request.getParameter("pid"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

            if (!sdao.hasPermissions(name, pid)) {
                sdao.log(name, pid, "user tried to view patient but does not have permission");
                throw new Exception("User does not have permission to view this patient");
            }


            String gList = request.getParameter("gList");
            String pList = request.getParameter("pList");

            boolean includeGenes=true;
            boolean includePhenotypes=true;
            if (gList !=null || pList !=null) {
                includeGenes=false;
                includePhenotypes=false;

                if (gList !=null && gList.equals("1")) {
                   includeGenes=true;
                }

                if (pList !=null && pList.equals("1")) {
                   includePhenotypes=true;
                }

            }

            ClinVarSearchResult result = new ClinVarSearchResult();

            AnnotationDAO adao = new AnnotationDAO();
            GeneDAO gdao = new GeneDAO();

            List<StringMapQuery.MapPair> phenotypes = new ArrayList<StringMapQuery.MapPair>();
            List<StringMapQuery.MapPair> genes = new ArrayList<StringMapQuery.MapPair>();

            if (includeGenes) {
                genes = sdao.getPatientGeneSymbols(pid);
            }

            if (includePhenotypes) {
                phenotypes = sdao.getPatientPhenotypeTerms(pid);
            }

            try {
                result.setGenesAndPhenotypes(genes,phenotypes);
            }catch (Exception e) {
                errorList.add(e.getMessage());
            }

            request.setAttribute("searchResult",result);

            if (request.getParameter("d") != null && request.getParameter("d").endsWith("1")) {
                sdao.log(name, pid, "Downloading clinvar variants for patient");

                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment; filename=variantAnnotation.xls");

                HSSFWorkbook workbook = new HSSFWorkbook();
                HSSFSheet sheet = workbook.createSheet("Variant Annotation");

                int rowCnt = 0;
                int colCnt=0;

                Row row = sheet.createRow(rowCnt++);

                Cell cell = row.createCell(colCnt++);
                cell.setCellValue("Term");
                cell = row.createCell(colCnt++);
                cell.setCellValue("Term Accession ID");
                cell = row.createCell(colCnt++);
                cell.setCellValue("Gene Symbol");
                cell = row.createCell(colCnt++);
                cell.setCellValue("Gene Name");
                cell = row.createCell(colCnt++);
                cell.setCellValue("Gene RGD ID");
                cell = row.createCell(colCnt++);
                cell.setCellValue("Variant");
                cell = row.createCell(colCnt++);
                cell.setCellValue("Variant RGD ID");
                cell = row.createCell(colCnt++);
                cell.setCellValue("Chromosome");
                cell = row.createCell(colCnt++);
                cell.setCellValue("Start");
                cell = row.createCell(colCnt++);
                cell.setCellValue("Stop");
                cell = row.createCell(colCnt++);
                cell.setCellValue("Assembly");
                colCnt=0;

                MapDAO mdao = new MapDAO();


                for (SNVAnnotation annot: result.getAnnotations() ) {
                    for (Gene thisGene: result.getGenes(annot.getTermAcc())) {
                        if (request.getParameter("dGene") != null && !request.getParameter("dGene").equals(thisGene.getRgdId()+ "")) {
                            continue;
                        }
                        for (GenomicElement ge: result.getGenomicElements(annot.getTermAcc(), thisGene)) {
                            row = sheet.createRow(rowCnt++);
                            cell = row.createCell(colCnt++);
                            cell.setCellValue(annot.getTerm());
                            cell = row.createCell(colCnt++);
                            cell.setCellValue(annot.getTermAcc());
                            cell = row.createCell(colCnt++);
                            cell.setCellValue(thisGene.getSymbol());
                            cell = row.createCell(colCnt++);
                            cell.setCellValue(thisGene.getName());
                            cell = row.createCell(colCnt++);
                            cell.setCellValue(thisGene.getRgdId());
                            cell = row.createCell(colCnt++);
                            cell.setCellValue(ge.getName());
                            cell = row.createCell(colCnt++);
                            cell.setCellValue(ge.getRgdId());

                            List<MapData> mdList = mdao.getMapData(ge.getRgdId(),17);

                            MapData md = mdList.get(0);

                            cell = row.createCell(colCnt++);
                            cell.setCellValue(md.getChromosome());

                            cell = row.createCell(colCnt++);
                            cell.setCellValue(md.getStartPos());

                            cell = row.createCell(colCnt++);
                            cell.setCellValue(md.getStopPos());

                            cell = row.createCell(colCnt++);
                            cell.setCellValue("Human Genome Assembly GRCh37");


                            colCnt=0;
                          }
                      }
                }

                //FileOutputStream out = new FileOutputStream(new File("C:\\new.xls"));
                workbook.write(response.getOutputStream());
                //out.close();
               return null;

            }

            request.setAttribute("error",errorList);

            if (request.getParameter("type") != null && request.getParameter("type").equals("i")) {
                sdao.log(name, pid, "viewing clinvar gene inspector for patient");
                return new ModelAndView("/WEB-INF/jsp/site/inspectList.jsp");
            }else if (request.getParameter("type") != null && request.getParameter("type").equals("g")) {
                sdao.log(name, pid, "viewing clinvar gene view for patient");
                return new ModelAndView("/WEB-INF/jsp/site/clinVarGenes.jsp");
            } else {
                sdao.log(name, pid, "viewing clinvar phenotype view for patient");
                return new ModelAndView("/WEB-INF/jsp/site/clinVar.jsp");
            }
    }

}
