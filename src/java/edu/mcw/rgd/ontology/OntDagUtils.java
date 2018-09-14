package edu.mcw.rgd.ontology;

import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.datamodel.ontologyx.TermWithStats;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: Mar 30, 2011
 * Time: 12:41:58 PM
 */
public class OntDagUtils {

    OntologyXDAO dao = new OntologyXDAO();

    void build(List<List<OntDagNode>> paths, List<OntDagNode> path, OntDagNode node, Map<String, List<Edge>> nodes) {

        // add the node to the path
        path.add(0, node);

        // examine all parent edges
        List<Edge> parentEdges = nodes.get(node.getTermAcc());
        // if there are no parent edges, we reached the top node
        // we have the complete path!
        if( parentEdges==null || parentEdges.isEmpty() ) {
            // add the parent node if not exists yet among nodes
            if( parentEdges==null ) {
                nodes.put(node.getTermAcc(), new LinkedList<Edge>());
            }
            paths.add(path);
            return;
        }

        // follow every parent edge
        List<OntDagNode> clonedPath;
        for( int i=0; i<parentEdges.size(); i++ ) {
            // every parent edge must have a separate path clone
            if( i < parentEdges.size()-1 ) {
                clonedPath = new LinkedList<OntDagNode>(path);
            }
            else {
                // the last parent edge will be using the original path
                clonedPath = path;
            }

            OntDagNode dagNode = new OntDagNode(parentEdges.get(i).parentTermAcc);
            // set the ontology relation id for this edge
            clonedPath.get(0).setOntRelId(parentEdges.get(i).ontRelId);
            build(paths, clonedPath, dagNode, nodes);
        }
    }

    /**
     * get term names and stats from database for all path nodes
     */
    void fillTerms(Set<String> termAccSet, List<List<OntDagNode>> paths) throws Exception  {
        // validate parameters
        if( termAccSet.isEmpty() )
            return;

        // get term names for all terms used in paths
        StringBuffer inClause = new StringBuffer();
        for( String node: termAccSet ) {
            if( inClause.length()>0 )
                inClause.append(',');
            inClause.append('\'').append(node).append('\'');
        }
        inClause.append(')');
        String sql = "SELECT t.term_acc, t.term, "+
                "rat_annots_for_term, "+
                "nvl(rat_annots_for_term,0)+nvl(human_annots_for_term,0)+nvl(mouse_annots_for_term,0), " +
                "nvl(rat_annots_with_children,0)+nvl(human_annots_with_children,0)+nvl(mouse_annots_with_children,0), "+
                "child_term_count, t.is_obsolete "+
                "FROM ont_terms t,ont_term_stats s WHERE s.term_acc=t.term_acc AND t.term_acc IN("+inClause;

        final Map<String,Object[]> map = new HashMap<String, Object[]>();
        MappingSqlQuery q = new MappingSqlQuery(dao.getDataSource(), sql) {
            @Override
            protected Object mapRow(ResultSet rs, int i) throws SQLException {
                map.put(rs.getString(1), new Object[]{rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getInt(7)});
                return null;
            }
        };
        q.compile();
        q.execute();

        // now go through all paths and fill the term info
        for( List<OntDagNode> path: paths ) {
            for( OntDagNode node: path ) {
                Object[] vals = map.get(node.getTermAcc());
                if( vals==null )
                    continue; // no stats for given term? continue to the next node

                // species specific term stats
                node.setTerm(vals[0].toString());

                node.setRatAnnotCountForTerm((Integer)(vals[1]));
                node.setAnnotCountForTerm((Integer)(vals[2]));
                node.setAnnotCountForTermAndChilds((Integer)(vals[3]));
                node.setChildCount((Integer)(vals[4]));
                node.setObsolete(((Integer)(vals[5]))>0);
                node.setTerm(vals[0].toString());
            }
        }
        map.clear();
    }

    public List<OntDagNode> getChildTerms(String termAcc) throws Exception {

        // retrieve immediate childs of the term
        String sql = "SELECT t.term_acc,term,ont_rel_id,is_obsolete,s.child_term_count "+
                "FROM ont_terms t,ont_dag,ont_term_stats s "+
                "WHERE parent_term_acc=? AND child_term_acc=t.term_acc AND t.term_acc=s.term_acc(+) "+
                "ORDER BY term";

        final List<OntDagNode> list = new LinkedList<OntDagNode>();
        MappingSqlQuery q = new MappingSqlQuery(dao.getDataSource(), sql) {
            @Override
            protected Object mapRow(ResultSet rs, int i) throws SQLException {
                OntDagNode node = new OntDagNode(rs.getString(1));
                node.setTerm(rs.getString(2));
                node.setOntRelId(rs.getString(3));
                node.setObsolete(rs.getInt(4)>0);
                node.setChildCount(rs.getInt(5));
                list.add(node);
                return null;
            }
        };
        q.declareParameter(new SqlParameter(Types.VARCHAR));
        q.compile();
        q.execute(new Object[]{termAcc});
        return list;
    }

    // loads stats for collection of OntDagNode objects
    public void loadStats(Collection<OntDagNode> nodes) throws Exception {

        // validate input parameters
        if( nodes==null || nodes.isEmpty() )
            return; // nothing to do

        // gather stats in batches of up to 1000 nodes
        final int batchSize = 1000;
        OntDagNode[] nodeArr = new OntDagNode[batchSize];
        int pos = 0;

        for( OntDagNode node: nodes ) {

            nodeArr[pos++] = node;

            // flush the batch?
            if( pos == batchSize ) {
                loadStats(nodeArr);
                pos = 0; // start new batch
            }
        }

        // finish incomplete batch
        if( pos>0 ) {
            for( int i=pos; i<batchSize; i++ ) {
                nodeArr[i] = null;
            }
            loadStats(nodeArr);
        }
    }

    void loadStats(OntDagNode[] nodes) throws Exception {

        // temporary map to speed up matching the data from db with the nodes in collection
        final Map<String, OntDagNode> map = new HashMap<String, OntDagNode>();

        // build inClause consisting of accession ids
        StringBuffer inClause = new StringBuffer();
        for( OntDagNode node: nodes ) {

            if( node==null )
                continue;

            if( inClause.length()>0 )
                inClause.append(',');
            inClause.append('\'').append(node.getTermAcc()).append('\'');
            map.put(node.getTermAcc(), node);
        }
        inClause.append(')');

        String sql = "select TERM_ACC, CHILD_TERM_COUNT, "+
                "rat_annots_for_term, "+
                "nvl(rat_annots_for_term,0)+nvl(human_annots_for_term,0)+nvl(mouse_annots_for_term,0), " +
                "nvl(rat_annots_with_children,0)+nvl(human_annots_with_children,0)+nvl(mouse_annots_with_children,0) "+
                "from ONT_TERM_STATS where TERM_ACC IN("+inClause;
        MappingSqlQuery q = new MappingSqlQuery(dao.getDataSource(), sql) {
            @Override
            protected Object mapRow(ResultSet rs, int i) throws SQLException {

                String termAcc = rs.getString(1);
                OntDagNode node = map.get(termAcc);

                node.setChildCount(rs.getInt(2));
                node.setRatAnnotCountForTerm(rs.getInt(3));
                node.setAnnotCountForTerm(rs.getInt(4));
                node.setAnnotCountForTermAndChilds(rs.getInt(5));
                return null;
            }
        };
        q.compile();
        q.execute();
    }

    public void loadTermStats(final TermWithStats term) throws Exception {

        // load stats
        String sql = "select child_term_count,"+
                "rat_annots_for_term, "+
                "nvl(rat_annots_for_term,0)+nvl(human_annots_for_term,0)+nvl(mouse_annots_for_term,0), " +
                "nvl(rat_annots_with_children,0)+nvl(human_annots_with_children,0)+nvl(mouse_annots_with_children,0) "+
                "from ONT_TERM_STATS where TERM_ACC=?";
        MappingSqlQuery q = new MappingSqlQuery(dao.getDataSource(), sql) {
            @Override
            protected Object mapRow(ResultSet rs, int i) throws SQLException {

                term.setChildTermCount(rs.getInt(1));
                term.setRatAnnotCountForTerm(rs.getInt(2));
                term.setAnnotCountForTerm(rs.getInt(3));
                term.setAnnotCountForTermAndChilds(rs.getInt(4));
                return null;
            }
        };
        q.declareParameter(new SqlParameter(Types.VARCHAR));
        q.compile();
        q.execute(new Object[]{term.getAccId()});

    }

    class Edge {
        public String parentTermAcc;
        public String childTermAcc;
        public String ontRelId;
    }

}