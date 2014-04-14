/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.prf.blog.entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Bali
 */
@Entity
@Table(name = "taxonomy")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Taxonomy.findAll", query = "SELECT t FROM Taxonomy t"),
    @NamedQuery(name = "Taxonomy.findById", query = "SELECT t FROM Taxonomy t WHERE t.id = :id"),
    @NamedQuery(name = "Taxonomy.findByCategoryname", query = "SELECT t FROM Taxonomy t WHERE t.categoryname = :categoryname")})
public class Taxonomy implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 50)
    @Column(name = "categoryname")
    private String categoryname;
    @OneToMany(mappedBy = "taxonomyid")
    private Collection<Posttaxonomy> posttaxonomyCollection;

    public Taxonomy() {
    }

    public Taxonomy(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    @XmlTransient
    public Collection<Posttaxonomy> getPosttaxonomyCollection() {
        return posttaxonomyCollection;
    }

    public void setPosttaxonomyCollection(Collection<Posttaxonomy> posttaxonomyCollection) {
        this.posttaxonomyCollection = posttaxonomyCollection;
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
        if (!(object instanceof Taxonomy)) {
            return false;
        }
        Taxonomy other = (Taxonomy) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "hu.prf.blog.entity.Taxonomy[ id=" + id + " ]";
    }
    
}
