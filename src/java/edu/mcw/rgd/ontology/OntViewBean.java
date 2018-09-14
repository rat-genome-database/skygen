package edu.mcw.rgd.ontology;

import edu.mcw.rgd.datamodel.ontologyx.Relation;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: Mar 30, 2011
 * Time: 12:40:38 PM
 */
public class OntViewBean extends OntBaseBean {

    private Collection<OntDagNode> ancestorTerms;
    private Collection<OntDagNode> siblingTerms;
    private Collection<OntDagNode> childTerms;
    private int scrollYPos; //
    private int strainRgdId;

    static public String getRelationImage(Relation rel) {
        String img;
        if( rel.equals(Relation.IS_A) )
            img = "is_a.gif";
        else
        if( rel.equals(Relation.PART_OF) )
            img = "part_of.gif";
        else
        if( rel.equals(Relation.NEGATIVELY_REGULATES) )
            img = "negatively_regulates.gif";
        else
        if( rel.equals(Relation.POSITIVELY_REGULATES) )
            img = "positively_regulates.gif";
        else
        if( rel.equals(Relation.REGULATES) )
            img = "regulates.gif";
        else

        if( rel.equals(Relation.DERIVES_FROM) || rel.equals(Relation.DEVELOPS_FROM) )
            img = "derives_from.gif";
        else
        if( rel.equals(Relation.VARIANT_OF) )
            img = "variant_of.png";
        else
        if( rel.equals(Relation.GUIDED_BY) )
            img = "guided_by.gif";
        else
        if( rel.equals(Relation.OVERLAPS) )
            img = "overlaps.gif";
        else
        if( rel.equals(Relation.NON_FUNCTIONAL_HOMOLOG_OF) )
            img = "non_functional_homolog_of.gif";
        else
        if( rel.equals(Relation.HAS_PART) )
            img = "has_part.jpeg";
        else
        if( rel.equals(Relation.HAS_COMPONENT) )
            img = "has_component.gif";
        else
        if( rel.equals(Relation.MEMBER_OF) )
            img = "member_of.jpeg";
        else
        if( rel.equals(Relation.CONTAINS) )
            img = "contains.jpeg";
        else
        if( rel.equals(Relation.ADJACENT_TO) )
            img = "adjacent_to.gif";
        else
            return null;
        return img;
    }

    public Collection<OntDagNode> getAncestorTerms() {
        return ancestorTerms;
    }

    public void setAncestorTerms(Collection<OntDagNode> ancestorTerms) {
        this.ancestorTerms = ancestorTerms;
    }

    public Collection<OntDagNode> getSiblingTerms() {
        return siblingTerms;
    }

    public void setSiblingTerms(Collection<OntDagNode> siblingTerms) {
        this.siblingTerms = siblingTerms;
    }

    public Collection<OntDagNode> getChildTerms() {
        return childTerms;
    }

    public void setChildTerms(Collection<OntDagNode> childTerms) {
        this.childTerms = childTerms;
    }

    public int getScrollYPos() {
        return scrollYPos;
    }

    public void setScrollYPos(int scrollYPos) {
        this.scrollYPos = scrollYPos;
    }

    public int getStrainRgdId() {
        return strainRgdId;
    }

    public void setStrainRgdId(int strainRgdId) {
        this.strainRgdId = strainRgdId;
    }
}
