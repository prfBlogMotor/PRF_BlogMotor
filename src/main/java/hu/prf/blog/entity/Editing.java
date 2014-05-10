/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.prf.blog.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Bali
 */
@Entity
@Table(name = "editing")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Editing.findAll", query = "SELECT e FROM Editing e"),
    @NamedQuery(name = "Editing.findById", query = "SELECT e FROM Editing e WHERE e.id = :id"),
    @NamedQuery(name = "Editing.findByDate", query = "SELECT e FROM Editing e WHERE e.date = :date")})
public class Editing implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "date")
    @Temporal(TemporalType.TIME)
    private Date date;
    @JoinColumn(name = "postid", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Post postid;

    public Editing() {
    }

    public Editing(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
        if (!(object instanceof Editing)) {
            return false;
        }
        Editing other = (Editing) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "hu.prf.blog.entity.Editing[ id=" + id + " ]";
    }
    
}
