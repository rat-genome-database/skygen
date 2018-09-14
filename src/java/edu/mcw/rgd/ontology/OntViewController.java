package edu.mcw.rgd.ontology;

import edu.mcw.rgd.dao.impl.PathwayDAO;
import edu.mcw.rgd.datamodel.ontologyx.Relation;
import edu.mcw.rgd.datamodel.ontologyx.TermSynonym;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: Mar 30, 2011
 * Time: 11:45:28 AM
 */
public class OntViewController implements Controller {
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // if mode=popup, redirect to jsp page for ont browser popup
        String mode = request.getParameter("mode");
        if( mode!=null && mode.equals("popup") ) {
            return new ModelAndView("/WEB-INF/jsp/ontology/tree_popup.jsp");
        }
        if( mode!=null && mode.equals("iframe") ) {
            return new ModelAndView("/WEB-INF/jsp/ontology/tree_iframe.jsp");
        }

        ModelAndView mv = new ModelAndView("/WEB-INF/jsp/ontology/tree.jsp");

        String accId = request.getParameter("acc_id");
        String ypos = request.getParameter("ypos");

        OntViewBean bean = new OntViewBean();
        populateBean(accId, ypos, bean);
        mv.addObject("bean", bean);

        // handle missing or unknown acc_id
        if( bean.getAccId()==null || bean.getTerm()==null ) {
            // create error message
            List<String> errors = (List<String>) request.getAttribute("error");
            if( errors==null ) {
                errors = new ArrayList<String>();
                request.setAttribute("error", errors);
            }
            if( bean.getAccId()==null )
                errors.add("Missing term accession id (parameter acc_id");
            else
                errors.add("Term accession id ["+bean.getAccId()+"] is invalid.");

            // redirect to general search
            OntGeneralSearchController searchController = new OntGeneralSearchController();
            if( bean.getAccId()!=null )
                request.setAttribute("term", bean.getAccId());
            return searchController.handleRequest(request, response);
        }

        return mv;
    }

    public static void populateBean(String accId, String ypos, OntViewBean bean) throws Exception {

        bean.setAccId(accId);

        OntDagUtils utils = new OntDagUtils();

        // get the term itself
        bean.setTerm(utils.dao.getTermWithStats(accId, null, 0));

        // handle invalid term acc id
        if( bean.getTerm()==null ) {
            return; // invalid acc id -- nothing more to do
        }

        // load term synonyms
        List<TermSynonym> synonyms = utils.dao.getTermSynonyms(accId);
        bean.setTermSynonyms(synonyms);
        // sort synonyms by type and name
        sortSynonyms(synonyms);

        // load term ancestors
        Map<String, Relation> ancestors = utils.dao.getTermAncestors(accId);

        // to reuse OntDagUtils.fillTerms() function we need to create auxiliary collections
        List<OntDagNode> ancestorList = new ArrayList<OntDagNode>(ancestors.size());
        for( Map.Entry<String, Relation> entry: ancestors.entrySet() ) {
            ancestorList.add(new OntDagNode(entry.getKey(), entry.getValue()));            
        }
        List<List<OntDagNode>> listOfAncestorLists = new ArrayList<List<OntDagNode>>(1);
        listOfAncestorLists.add(ancestorList);

        utils.fillTerms(ancestors.keySet(), listOfAncestorLists);
        Collections.sort(ancestorList);
        bean.setAncestorTerms(ancestorList);

        // child terms for the selected term
        List<OntDagNode> childs = utils.getChildTerms(accId);
        Collections.sort(childs);
        utils.loadStats(childs);
        bean.setChildTerms(childs);


        // get child terms for parent terms -- sibling terms
        //
        Set<OntDagNode> siblings = new TreeSet<OntDagNode>();
        // case 1: there are no parent terms
        if( ancestorList.isEmpty() ) {
            OntDagNode node = new OntDagNode(accId);
            node.setTerm(bean.getTerm().getTerm());
            node.setChildCount(childs.size());
            siblings.add(node);
        }
        // case 2: there are parent terms
        else {
            for( OntDagNode node: ancestorList ) {
                childs = utils.getChildTerms(node.getTermAcc());
                siblings.addAll(childs);
            }
        }
        // load annotation stats for all nodes
        utils.loadStats(siblings);
        bean.setSiblingTerms(siblings);

        // load strain rgd id if applicable
        bean.setStrainRgdId(utils.dao.getRgdIdForStrainOntId(accId));

        // load diagram paths for pathways
        if( accId.startsWith("PW") ) {
            loadPathwayDiagramUrls(bean);
        }

        // parse scroll y pos - the proposed vertical scroll pos for the document in the browser
        int scrollYPos = -1; // undefined
        if( ypos!=null ) {
            try { scrollYPos = Integer.parseInt(ypos); } catch(NumberFormatException e) {}
        }
        bean.setScrollYPos(scrollYPos);
    }

    private static void loadPathwayDiagramUrls(OntViewBean bean) throws Exception {

        PathwayDAO dao = new PathwayDAO();

        loadPathwayDiagramUrls(bean.getAncestorTerms(), dao);
        loadPathwayDiagramUrls(bean.getSiblingTerms(), dao);
        loadPathwayDiagramUrls(bean.getChildTerms(), dao);
    }

    private static void loadPathwayDiagramUrls(Collection<OntDagNode> nodes, PathwayDAO dao) throws Exception {

        if( nodes.isEmpty() )
            return;

        // build a list of pathway accession ids
        List<String> accIds = new ArrayList<String>(nodes.size());
        for( OntDagNode node: nodes ) {
            accIds.add(node.getTermAcc());
        }

        // limit pathway accession ids to curated pathways (the ones with diagrams)
        List<String> curatedAccIds = dao.getCuratedPathways(accIds);

        // for faster lookup, convert list to set
        Set<String> curatedAccIdSet = new HashSet<String>(curatedAccIds);

        // construct diagram paths
        for( OntDagNode node: nodes ) {
            if( curatedAccIdSet.contains(node.getTermAcc()) ) {
                // load diagram info
                node.setHasPathwayDiagram(true);
            }
            node.setCountOfPathwayDiagramsForTermChilds(dao.getCountOfCuratedPathwaysForTermDescendants(node.getTermAcc()));
        }
    }

    // sort synonyms by name and type
    static public void sortSynonyms(List<TermSynonym> synonyms) {

        // custom sort of synonyms by type, then by name
        // the type order was approved by Jennifer
        Collections.sort(synonyms, new Comparator<TermSynonym>(){
            public int compare(TermSynonym o1, TermSynonym o2) {
                // first compare by type rank
                int diff = getTypeRank(o1.getType()) - getTypeRank(o2.getType());
                if( diff!=0 )
                    return diff;
                // if types are same, compare by name
                return o1.getName().compareTo(o2.getName());
            }

            // type rank approved by Jennifer
            private int getTypeRank(String synonymType) {
                if( synonymType.startsWith("exact") )
                    return 10;
                else if( synonymType.startsWith("synonym") )
                    return 20;
                else if( synonymType.startsWith("narrow") )
                    return 30;
                else if( synonymType.startsWith("broad") )
                    return 40;
                else if( synonymType.startsWith("related") )
                    return 50;
                else if( synonymType.startsWith("primary_id") )
                    return 60;
                else if( synonymType.startsWith("alt_id") )
                    return 70;
                else if( synonymType.startsWith("omim") )
                    return 80;
                else if( synonymType.startsWith("xref_analog") )
                    return 90;
                else if( synonymType.startsWith("xref") )
                    return 100;
                else
                    return 110;
            }
        });
    }
}