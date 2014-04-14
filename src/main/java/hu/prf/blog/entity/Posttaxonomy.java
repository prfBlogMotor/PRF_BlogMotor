/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.prf.blog.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Bali
 */
@Entity
@Table(name = "posttaxonomy")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Posttaxonomy.findAll", query = "SELECT p FROM Posttaxonomy p"),
    @NamedQuery(name = "Posttaxonomy.findById", query = "SELECT p FROM Posttaxonomy p WHERE p.id = :id")})
public class Posttaxonomy implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @JoinColumn(name = "taxonomyid", referencedColumnName = "id")
    @ManyToOne
    private Taxonomy taxonomyid;
    @JoinColumn(name = "postid", referencedColumnName = "id")
    @ManyToOne
    private Post postid;

    public Posttaxonomy() {
    }

    public Posttaxonomy(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Taxonomy getTaxonomyid() {
        return taxonomyid;
    }

    public void setTaxonomyid(Taxonomy taxonomyid) {
        this.taxonomyid = taxonomyid;
    }

    public Post getPostid() {
        return postid;
    }

    public void setPostid(Post postid) {
        this.postid = postid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Posttaxonomy)) {
            return false;
        }
        Posttaxonomy other = (Posttaxonomy) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "hu.prf.blog.entity.Posttaxonomy[ id=" + id + " ]";
    }
    
}
