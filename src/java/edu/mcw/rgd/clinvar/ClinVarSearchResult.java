package edu.mcw.rgd.clinvar;

import edu.mcw.rgd.dao.impl.AnnotationDAO;
import edu.mcw.rgd.dao.impl.GeneDAO;
import edu.mcw.rgd.dao.impl.GenomicElementDAO;
import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.dao.spring.StringMapQuery;
import edu.mcw.rgd.datamodel.Gene;
import edu.mcw.rgd.datamodel.GenomicElement;
import edu.mcw.rgd.datamodel.ontology.Annotation;
import edu.mcw.rgd.datamodel.ontology.SNVAnnotation;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import org.apache.log4j.net.ZeroConfSupport;
import sun.jdbc.odbc.ee.ConnectionPool;


import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: jdepons
 * Date: 6/26/14
 * Time: 10:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class ClinVarSearchResult {

    //termAcc-geneRGDId
    private List<String> termToGeneXref = new ArrayList<String>();
    //rgdId
    private List<String> annotatedGeneXref = new ArrayList<String>();

    private HashMap<String,List<Term>> rdoParentToChildXref = new HashMap<String,List<Term>>();
    private HashMap<String,List<Term>> rdoToHPConversions = new HashMap<String,List<Term>>();
    private HashMap<String,List<Term>> hpToRDOConversions = new HashMap<String,List<Term>>();

    private boolean annotatedGenesSorted=false;
    private boolean annotatedTermsSorted=false;
    private LinkedHashMap<String,Term>  annotatedTerms = new LinkedHashMap<String,Term>();
    private LinkedHashMap<String,Gene>  annotatedGenes = new LinkedHashMap<String,Gene>();

    private List<String> globalGenomicElementsXref = new ArrayList<String>();

    private LinkedHashMap<String,List<Gene>> termToGenes = new LinkedHashMap<String, List<Gene>>();
    private LinkedHashMap<String,List<Term>> geneToTerms = new LinkedHashMap<String, List<Term>>();
    private LinkedHashMap<String,List<GenomicElement>> geneToElement = new LinkedHashMap<String, List<GenomicElement>>();
    private List<StringMapQuery.MapPair> patientGenes;
    private List<StringMapQuery.MapPair> patientPheontypes;
    private List<SNVAnnotation> annotations = new ArrayList();

    private LinkedHashMap<String,List<GenomicElement>> genomicElements = new LinkedHashMap<String,List<GenomicElement>>();

    private GeneDAO gdao = new GeneDAO();
    private GenomicElementDAO gedao = new GenomicElementDAO();
    private OntologyXDAO odao = new OntologyXDAO();
    private AnnotationDAO adao = new AnnotationDAO();

    //key=accid of phenotype term
    public HashMap<String,List<Term>> getHpToRDOXConversions() {
        return this.rdoToHPConversions;
    }

    public LinkedHashMap<String,Term> getAnnotatedTerms() {
        if (!this.annotatedTermsSorted) {
            this.sortAnnotatedTerms();
        }

        return this.annotatedTerms;
    }


    private void sortAnnotatedTerms() {

        List<Term> terms = new ArrayList<Term>();

        Iterator it = this.annotatedTerms.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            Term value = (Term) this.annotatedTerms.get(key);

            terms.add(value);

        }

        //sort the term list
        Collections.sort(terms, new Comparator<Term> () {
              public int compare(Term x, Term y) {


                int xScore = (getGeneCount(x.getAccId()) * 10000) + getVariantCount(x.getAccId());

                int yScore = (getGeneCount(y.getAccId()) * 10000) + getVariantCount(y.getAccId());

                if (xScore > yScore) {
                    return -1;
                }
                if (yScore > xScore) {
                    return 1;
                }

                return 0;
          }

        });
        this.annotatedTerms = new LinkedHashMap<String, Term>();

        for (Term t: terms) {

           this.annotatedTerms.put(t.getAccId(), t);
        }

        this.annotatedTermsSorted = true;

    }

    private void sortAnnotatedGenes() {

        List<Gene> genes = new ArrayList<Gene>();

        Iterator it = this.annotatedGenes.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            Gene value = (Gene) this.annotatedGenes.get(key);

            genes.add(value);

        }

        Collections.sort(genes, new Comparator<Gene> () {
              public int compare(Gene x, Gene y) {

                int xScore = (getTermCount(x.getRgdId() + "") * 10000) + getVariantCountForGene(x.getRgdId() + "");
                int yScore = (getTermCount(y.getRgdId() + "") * 10000) + getVariantCountForGene(y.getRgdId() + "");

                if (xScore > yScore) {
                    return -1;
                }
                if (yScore > xScore) {
                    return 1;
                }

                return 0;
          }

        });

        this.annotatedGenes= new LinkedHashMap<String,Gene>();

        for (Gene g: genes) {
            this.annotatedGenes.put(g.getRgdId() + "", g);
        }

        this.annotatedGenesSorted = true;

    }

    public LinkedHashMap<String,Gene> getAnnotatedGenes() {

        if (!this.annotatedGenesSorted) {
            this.sortAnnotatedGenes();
        }

        return this.annotatedGenes;

    }

    public List<StringMapQuery.MapPair> getUnannotatedPatientGenes() {
        List<StringMapQuery.MapPair> unannotatedGenes = new ArrayList<StringMapQuery.MapPair>();

        for (StringMapQuery.MapPair gene : patientGenes) {
            if (!annotatedGeneXref.contains(gene.keyValue + "")) {
                unannotatedGenes.add(gene);
            }
        }
        return unannotatedGenes;
    }

    public List<StringMapQuery.MapPair> getPatientGenes() {
       return this.patientGenes;
    }

    public List<StringMapQuery.MapPair> getPatientPhenotypes() {
        return this.patientPheontypes;
    }

    public List<Term> getTerms (String geneRgdId) {
        return this.geneToTerms.get(geneRgdId);

    }


    public void setGenesAndPhenotypes(List<StringMapQuery.MapPair> genes, List<StringMapQuery.MapPair> phenotypes) throws Exception {
        this.patientPheontypes = phenotypes;
        this.patientGenes = genes;

        List<String> hpAccIds = new ArrayList();
        List<String> rdoAccIds = new ArrayList();

        //<String,List<Annotation>> conversions = new HashMap();
        List<Integer> rgdIds = new ArrayList();

        if (genes.size() > 0) {
            for (StringMapQuery.MapPair mp: genes) {
                rgdIds.add(Integer.parseInt(mp.keyValue));
            }
        }

        if (phenotypes.size() > 0) {
            for (StringMapQuery.MapPair mp: phenotypes) {
                if (mp.keyValue.startsWith("HP")) {
                    hpAccIds.add(mp.keyValue);
                }
                if (mp.keyValue.startsWith("RDO")) {
                    rdoAccIds.add(mp.keyValue);

                    List<Term> terms = odao.getEquivilentPhenotypeTermsForDisease(mp.keyValue);
                    for (Term t: terms) {
                        hpAccIds.add(t.getAccId());
                    }


                }
            }

            if (hpAccIds.size() > 0) {
                List<Annotation> hpAnnotList = adao.getAnnotationList(hpAccIds, rgdIds, 1);

                for (Annotation hpAnnot: hpAnnotList) {
                    if (hpAnnot.getRelativeTo() == null) {
                        continue;
                    }

                    Term phenoTerm = odao.getTerm(hpAnnot.getTermAcc());
                    Term diseaseTerm = odao.getTerm(hpAnnot.getRelativeTo());

                    if (this.rdoToHPConversions.containsKey(diseaseTerm.getAccId())) {
                        this.rdoToHPConversions.get(diseaseTerm.getAccId()).add(phenoTerm);
                    }else {
                        ArrayList<Term> hpAl = new ArrayList<Term>();
                        hpAl.add(phenoTerm);
                        this.rdoToHPConversions.put(diseaseTerm.getAccId(), hpAl);
                    }

                    if (this.hpToRDOConversions.containsKey(phenoTerm.getAccId())) {
                        this.hpToRDOConversions.get(phenoTerm.getAccId()).add(diseaseTerm);
                    }else {
                        ArrayList<Term> hpAl = new ArrayList<Term>();
                        hpAl.add(diseaseTerm);
                        this.rdoToHPConversions.put(phenoTerm.getAccId(), hpAl);
                    }

                    rdoAccIds.add(diseaseTerm.getAccId());
                }
            }

            if (rdoAccIds.size() == 0) {
                throw new Exception("Zero Disease Annotations found for Phenotype List.  OMIM does not have a mapping.");
            }


        }

        List<SNVAnnotation> annotList = adao.getSNVAnnotationList(rdoAccIds, rgdIds, "ClinVar");
       // this.rdoToHPConversions=conversions;

        Iterator it = rdoAccIds.iterator();
        while (it.hasNext()) {
            String vel = (String) it.next();
        }

        this.addAnnotations(annotList);
    }


    public List<SNVAnnotation> getAnnotations() {
        return this.annotations;
    }


    public HashMap<String,List<Term>> getRelatedDiseases() {
        return rdoParentToChildXref;
    }


    public void addAnnotations(List<SNVAnnotation> annots) throws Exception {
        int count=0;
        boolean add = true;

        List<Term> terms = new ArrayList<Term>();

        for (SNVAnnotation a: annots) {
            if (a.getConcreteTermAccId() !=null && !a.getTermAcc().equals(a.getConcreteTermAccId())) {
                List<Term> related;

                if (this.rdoParentToChildXref.containsKey(a.getTermAcc())) {
                    related=rdoParentToChildXref.get(a.getTermAcc());
                }else {
                    related = new ArrayList<Term>();
                }

                if (!rdoParentToChildXref.containsKey(a.getTermAcc() + "-" + a.getConcreteTermAccId())) {
                    related.add(odao.getTerm(a.getConcreteTermAccId()));
                    rdoParentToChildXref.put(a.getTermAcc() + "-" + a.getConcreteTermAccId(),null);
                }
                rdoParentToChildXref.put(a.getTermAcc(), related);
            }

            for (SNVAnnotation a1: this.annotations) {
                if (a1.getTermAcc().equals(a.getTermAcc())) {
                    add=false;
                }
            }

            if (add) {
                this.annotations.add(a);

            }

            Term t =  odao.getTerm(a.getTermAcc());

            terms.add(t);

            this.addAnnotatedGene(a, t);
            this.addGenomicElement(a);
            add=true;

            this.annotatedTerms.put(t.getAccId(), t);
        }



    }

    public List<Gene> getGenes(String termAcc) {
        return this.termToGenes.get(termAcc);

    }

    public int getTermCount(String geneRgdId) {

        if (this.geneToTerms.get(geneRgdId) == null) {
            return 0;
        }

        return this.geneToTerms.get(geneRgdId).size();
    }


    public int getGeneCount(String termAcc) {

        if (this.termToGenes.get(termAcc) == null) {
            return 0;
        }

        return this.termToGenes.get(termAcc).size();
    }

    public int getVariantCount(String termAcc) {

        int variantCount = 0;
        List<Gene> genes = getGenes(termAcc);

        for (Gene g: genes) {
            variantCount += this.getGenomicElements(termAcc,g).size();
        }

        return variantCount;
    }

    public int getVariantCountForGene(String geneRgdId) {
        return this.geneToElement.get(geneRgdId).size();


    }



    public List<GenomicElement> getGenomicElements(String termAcc, Gene g) {
        String id= termAcc + "-" + g.getRgdId();
        return this.genomicElements.get(id);
    }

    private void addAnnotatedGene(SNVAnnotation annot, Term t) throws Exception {

        if (this.termToGeneXref.contains(annot.getTermAcc() + "-" + annot.getGeneRgdId()))  {
            return;
        }

        termToGeneXref.add(annot.getTermAcc() + "-" + annot.getGeneRgdId());

        if (!this.annotatedGeneXref.contains(annot.getGeneRgdId()))  {
            this.annotatedGeneXref.add(annot.getGeneRgdId() + "");
        }

        Gene g = this.gdao.getGene(annot.getGeneRgdId());
        this.annotatedGenes.put(g.getRgdId() + "", g);


        List<Gene> geneList = null;

        if (this.termToGenes.containsKey(annot.getTermAcc())) {
            geneList = this.termToGenes.get(annot.getTermAcc());
        } else {
            geneList = new ArrayList<Gene>();
        }

        geneList.add(g);
        this.termToGenes.put(annot.getTermAcc(), geneList);


        List<Term> termList = null;
        if (this.geneToTerms.containsKey(g.getRgdId())) {
            termList = this.geneToTerms.get(g.getRgdId());
        } else {
            termList = new ArrayList<Term>();
        }

        termList.add(t);

        this.geneToTerms.put(g.getRgdId() + "", termList);








    }

    private void addGenomicElement(SNVAnnotation annot) throws Exception {
        String id= annot.getTermAcc() + "-" + annot.getGeneRgdId();

        if (this.globalGenomicElementsXref.contains(id + "-" + annot.getAnnotatedObjectRgdId())) {
            return;
        }else {
            this.globalGenomicElementsXref.add(id + "-" + annot.getAnnotatedObjectRgdId());
        }

        List<GenomicElement> geList = null;
        if (this.genomicElements.containsKey(id)) {
            geList = this.genomicElements.get(id);
        }else {
            geList = new ArrayList<GenomicElement>();
        }

        GenomicElement ge = gedao.getElement(annot.getAnnotatedObjectRgdId());
        geList.add(ge);
        this.genomicElements.put(id, geList);


        id = annot.getGeneRgdId() + "";
        if (this.geneToElement.containsKey(id)) {
            geList = this.geneToElement.get(id);
        }else {
            geList = new ArrayList<GenomicElement>();
        }

        geList.add(ge);
        this.geneToElement.put(id, geList);


    }

}

