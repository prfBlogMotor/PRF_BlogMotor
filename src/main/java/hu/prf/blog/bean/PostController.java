package hu.prf.blog.bean;

import hu.prf.blog.bean.session.CommentFacade;
import hu.prf.blog.bean.session.EditingFacade;
import hu.prf.blog.bean.session.PostFacade;
import hu.prf.blog.bean.session.PosttaxonomyFacade;
import hu.prf.blog.bean.session.TaxonomyFacade;
import hu.prf.blog.bean.util.JsfUtil;
import hu.prf.blog.bean.util.PaginationHelper;
import hu.prf.blog.entity.Comment;
import hu.prf.blog.entity.Editing;
import hu.prf.blog.entity.Post;
import hu.prf.blog.entity.Posttaxonomy;
import hu.prf.blog.entity.Taxonomy;
import hu.prf.blog.entity.User;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.tagcloud.DefaultTagCloudItem;
import org.primefaces.model.tagcloud.DefaultTagCloudModel;
import org.primefaces.model.tagcloud.TagCloudModel;

@ManagedBean(name = "postController")
@SessionScoped
public class PostController implements Serializable {

    private Post current;
    private DataModel items = null;
    @EJB
    private hu.prf.blog.bean.session.PostFacade ejbFacade;
    @EJB
    private CommentFacade commentFacade;
    @EJB
    private TaxonomyFacade taxonomyFacade;
    @EJB
    private PosttaxonomyFacade postTaxonomyFacade;
    @EJB
    private EditingFacade editingFacade;

    private PaginationHelper pagination;
    private int selectedItemIndex;

    private Collection<Comment> comments;
    private Comment lastComment;

    private List<Taxonomy> taxonomies;
    private List<String> selectedTaxonomies;

    private String currentTaxonomy;
    private String taxonomyToCreate;

    public String getTaxonomyToCreate() {
        return taxonomyToCreate;
    }

    public void setTaxonomyToCreate(String taxonomyToCreate) {
        this.taxonomyToCreate = taxonomyToCreate;
    }
    public int currentViewId;

    public String changeToView() {
        //System.out.println("Redirecting to view...");
        current = (Post) getItems().getRowData();
        lastComment = new Comment();
        lastComment.setPostid(current);
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        //System.out.println("selected: " + selectedItemIndex);
        return "success";
    }

    public PostController() {
    }

    public Post getSelected() {
        if (current == null) {
            current = new Post();
            selectedItemIndex = -1;
        }
        return current;
    }

    public Comment getLastComment() {
        return lastComment;
    }

    public void setLastComment(Comment lastComment) {
        this.lastComment = lastComment;
    }

    public String saveLastComment(User user) {
        lastComment.setDate(Calendar.getInstance().getTime());
        lastComment.setUserid(user);
        lastComment.setPostid(current);
        try {
            commentFacade.create(lastComment);
            if (comments != null) {
                comments.add(lastComment);
            }
            lastComment = new Comment();
            System.out.println(" Comment elmentve");
            return "post/View?faces-redirect=true";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String saveComment(Comment comment) {
        //System.out.println(" *** SAVE COMMENT");
        comment.setDate(Calendar.getInstance().getTime());
        try {
            commentFacade.edit(comment);
            return null;
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String removeComment(Comment comment) {
        //System.out.println(" *** REMOVE COMMENT");
        try {
            commentFacade.remove(comment);
            if (comments != null) {
                comments.remove(comment);
            }
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Komment törölve!"));
            return null;
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    private PostFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List?faces-redirect=true";
    }

    public String prepareView() {
        //System.out.println("Changing view...");
        current = (Post) getItems().getRowData();
        lastComment = new Comment();
        lastComment.setPostid(current);
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        System.out.println("@PostController/prepareCreate");
        current = new Post();
        selectedItemIndex = -1;
        return "Create";
    }

    public String prepareAfterCreate() {
        System.out.println("@PostController/prepareAfterCreate");
        current = new Post();
        selectedItemIndex = -1;
        return "navigate_to_list";
    }

    public String create() {
        current.setDate(Calendar.getInstance().getTime());
        try {
            System.out.println("---- POST CREATE");
            getFacade().create(current);
            System.out.println("---- POST CREATE - SAVE TAXES");
            saveTaxonomies();
            System.out.println("---- POST CREATE - SAVE EDITING");
            

            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PostCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public void saveEditing() {
        Editing editing = new Editing();
        editing.setPostid(current);
        editing.setDate(Calendar.getInstance().getTime());

        editingFacade.create(editing);
    }

    public String create(User user) {
        current.setDate(Calendar.getInstance().getTime());
        current.setUserid(user);
        try {

//            saveTaxonomies();
            System.out.println("@PostController/create");
            getFacade().create(current);
            System.out.println("@PostController/create - tax save");
            addTaxonomies();
            
            saveEditing();
//            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PostCreated"));
//            return prepareCreate();

            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Az új poszt létrehozva", current.getTitle()));
//            return "success";
            return prepareList();
        } catch (Exception e) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("Hiba: " + ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured")));
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public Map<String, Long> taxonomiesAsMap() {
        List<Taxonomy> tags = taxonomyFacade.findAll();
        Map<String, Long> mapForCheckboxMenu = new HashMap<String, Long>();

        for (Taxonomy tax : tags) {
            mapForCheckboxMenu.put(tax.getCategoryname(), tax.getId());
        }
        return mapForCheckboxMenu;
    }

    private void addTaxonomies() {
        System.out.println("Taxonomy hozzaadasa");
        Collection<Posttaxonomy> posttaxonomies = postTaxonomyFacade.findAllPostTaxonomiesByPost(current);
        for (String tax : selectedTaxonomies) {
            boolean isContains = false;
            for (Posttaxonomy posttaxonomy : posttaxonomies) {
                if (Long.parseLong(tax) == (posttaxonomy.getTaxonomyid().getId())) {
                    System.out.println("+++ A " + posttaxonomy.getTaxonomyid().getCategoryname() + " már szerepelt!");
                    isContains = true;
                }
            }
            
            if (isContains) continue;
            Taxonomy t = taxonomyFacade.findById(Long.parseLong(tax));
            System.out.println(t.getCategoryname());
            Posttaxonomy pt = new Posttaxonomy();
            pt.setPostid(current);
            pt.setTaxonomyid(t);
            postTaxonomyFacade.create(pt);
        }
    }

    public void makeTaxonomy(String asd) {
        if (taxonomyToCreate.isEmpty()) {
            return;
        }

        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Az új tag bekerült a listába"));

        System.out.println("@TaxCreate " + taxonomyToCreate);
        Taxonomy tax = new Taxonomy();

        tax.setCategoryname(taxonomyToCreate);
        System.out.println("@TaxCreate/set");
        taxonomyFacade.create(tax);
        System.out.println("@TaxCreate/create");

    }

    private void saveTaxonomies() {
        if (taxonomies == null) {
            taxonomies = new ArrayList<Taxonomy>();
        }

        Collection<Taxonomy> tags = getTaxonomiesCollection(current);

        String[] splitted = currentTaxonomy.split(",");
        for (String string : splitted) {
            boolean isPersisted = false;
            for (Taxonomy taxonomy : tags) {
                String tag = taxonomy.getCategoryname();
                if (string.equalsIgnoreCase(tag)) {
                    isPersisted = true;
                    break;
                }
            }

            // TODO, ha más post cimkéjét adjuk meg, akkor azokat is járja be és hozzon létre új posttaxonomy-kat...
            if (!isPersisted) {
                Taxonomy tag = new Taxonomy();
                tag.setCategoryname(string.trim());
                taxonomies.add(tag);
                taxonomyFacade.create(tag);

                Posttaxonomy pt = new Posttaxonomy();
                pt.setPostid(current);
                pt.setTaxonomyid(tag);
                postTaxonomyFacade.create(pt);
            }
        }
    }
    
    public String getLastModifiedDate(Post post) {
        List<Editing> editings = editingFacade.findEditingByPost(post.getId());
        if (editings == null || editings.isEmpty())
            return post.getDate().toString();
        return editings.get(editings.size() - 1).getDate().toString();
    }
    
    public String getLastModifiedBy(Post post) {
        List<Editing> editings = editingFacade.findEditingByPost(post.getId());
        if (editings == null || editings.isEmpty())
            return post.getUserid().getUsername();
        return editings.get(editings.size() - 1).getPostid().getUserid().getUsername();
    }
    
    public String getPostModificationCount(Post post) {
        List<Editing> editings = editingFacade.findEditingByPost(post.getId());
        return (editings == null) ? "0" : String.valueOf(editings.size() - 1);
    }
    
    public boolean isModificationInfosVisible(Post post) {
        List<Editing> editings = editingFacade.findEditingByPost(post.getId());
        if (editings == null)
            return false;
        if (editings.size() <= 1)
            return false;
        return true;
    }

    public String prepareEdit() {
        current = (Post) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();

        List<String> selectedTaxonomiesList = new ArrayList<String>();
        for (Taxonomy taxonomy : getTaxonomiesCollection(current)) {
            selectedTaxonomiesList.add(taxonomy.getCategoryname());
        }
        setSelectedTaxonomies(selectedTaxonomiesList);

        return "Edit";
    }

    public String update() {
        try {
            System.out.println("+++ UPDATE: before texonomies saving");
            addTaxonomies();
            System.out.println("+++ UPDATE: before editing saving");
            saveEditing();
            System.out.println("+++ UPDATE: before post saving");
            getFacade().edit(current);
            System.out.println("+++ UPDATE: success");
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PostUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Post) getItems().getRowData();
        for (Editing editing : editingFacade.findEditingByPost(current.getId())) {
            editingFacade.remove(editing);
        }
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List?faces-redirect=true";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            Collection<Posttaxonomy> postTaxonomies = current.getPosttaxonomyCollection();
            Collection<Taxonomy> taxonomiesCollection = new ArrayList<Taxonomy>();
            for (Posttaxonomy posttaxonomy : postTaxonomies) {
                taxonomiesCollection.add(posttaxonomy.getTaxonomyid());
                postTaxonomyFacade.remove(posttaxonomy);
            }

            for (Taxonomy taxonomy : taxonomiesCollection) {
                if (postTaxonomyFacade.findAllPostTaxonomiesByTaxonomy(taxonomy).size() <= 1) {
                    taxonomyFacade.remove(taxonomy);
                }
            }

            current.setPosttaxonomyCollection(null);
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PostDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        if (newValue != null && !newValue.equals(oldValue)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public String getTaxonomiesForView(int id) {
        Collection<Posttaxonomy> posttaxonomies = current.getPosttaxonomyCollection();
        if (posttaxonomies == null || posttaxonomies.isEmpty()) {
            return "";
        }

        //System.out.println("2..." + posttaxonomies.toString());
        StringBuilder sb = null;
        for (Posttaxonomy posttaxonomy : posttaxonomies) {
            if (posttaxonomy.getTaxonomyid() == null
                    || posttaxonomy.getTaxonomyid().getCategoryname() == null) {
                continue;
            }
            if (sb == null) {
                sb = new StringBuilder();
                sb.append(posttaxonomy.getTaxonomyid().getCategoryname());
            } else {
                sb.append(", ");
                sb.append(posttaxonomy.getTaxonomyid().getCategoryname());
            }
        }
        //System.out.println("-------------" + sb.toString());
        return sb.toString();
    }

    public String getTaxonomies(long id) {
        Post currentPost = (Post)getFacade().find(id);
        if (currentPost == null) {
            return "";
        }
        //System.out.println("1...");
        Collection<Posttaxonomy> posttaxonomies = postTaxonomyFacade.findAllPostTaxonomiesByPost(currentPost);
        if (posttaxonomies == null || posttaxonomies.size() == 0) {
            return "";
        }
        //System.out.println("2...");
        StringBuilder sb = null;
        for (Posttaxonomy posttaxonomy : posttaxonomies) {
            if (posttaxonomy.getTaxonomyid() == null
                    || posttaxonomy.getTaxonomyid().getCategoryname() == null) {
                continue;
            }
            if (sb == null) {
                sb = new StringBuilder();
                sb.append(posttaxonomy.getTaxonomyid().getCategoryname());
            } else {
                sb.append(", ");
                sb.append(posttaxonomy.getTaxonomyid().getCategoryname());
            }
        }
        //System.out.println("-------------" + sb.toString());
        return sb.toString();
    }

    public Collection<Taxonomy> getTaxonomiesCollection(Post post) {
        Collection<Posttaxonomy> posttaxonomies = post.getPosttaxonomyCollection();
        if (posttaxonomies == null) {
            return new ArrayList<Taxonomy>();
        }

        Collection<Taxonomy> result = new ArrayList<Taxonomy>();
        for (Posttaxonomy posttaxonomy : posttaxonomies) {
            result.add(posttaxonomy.getTaxonomyid());
        }
        return result;
    }

    public TagCloudModel getTagsModel(Post post) {
        Collection<Posttaxonomy> posttaxonomies = post.getPosttaxonomyCollection();

        TagCloudModel model = new DefaultTagCloudModel();
        for (Posttaxonomy posttaxonomy : posttaxonomies) {
            model.addTag(new DefaultTagCloudItem(posttaxonomy.getTaxonomyid().getCategoryname(), 1));
        }
        return model;
    }

    public Collection<Comment> getComments(Post post) {
        comments = post.getCommentCollection();
        return comments;
    }

    public void handleSelect(SelectEvent event) {
        if (taxonomies == null) {
            taxonomies = new ArrayList<Taxonomy>();
        }
        String tag = event.getObject().toString();
        //System.out.println("*-*-*-*-: TTAAGGG: " + tag);

        Taxonomy taxonomy = taxonomyFacade.findByCategoryName(tag);
        taxonomies.add(taxonomy);
        currentTaxonomy = null;
    }

    public List<String> getAllTaxonomyText() {
        List<Taxonomy> tags = taxonomyFacade.findAll();

        List<String> result = new ArrayList<String>();
        for (Taxonomy taxonomy : tags) {
            //System.out.println("*-*-*- Taxonomy: " + taxonomy.getCategoryname());
            result.add(taxonomy.getCategoryname());
        }
        return result;
    }

    public List<String> complete(String query) {
        //System.out.println(" CCOOMMPPLLEETT " + query);
        List<String> results = new ArrayList<String>();

        for (String tag : getAllTaxonomyText()) {
            if (tag.startsWith(query)) {
                results.add(tag);
            }
        }

        return results;
    }

    public void addNewTaxonomy(ActionEvent event) {
        //System.out.println(" -------- ADDNEWTAG --------: " + event.toString());
        if (taxonomies == null) {
            taxonomies = new ArrayList<Taxonomy>();
        }

        String value = FacesContext.getCurrentInstance().
                getExternalContext().getRequestParameterMap().get(":rightForm:acSimple");
        //System.out.println("WWWW: " + value);

        Taxonomy tag = new Taxonomy();
        tag.setCategoryname(event.getSource().toString());
        taxonomies.add(tag);
        currentTaxonomy = null;
    }

    public void getText(String text) {
        String value = FacesContext.getCurrentInstance().
                getExternalContext().getRequestParameterMap().get(":rightForm:acSimple");
        //System.out.println("WWWW: " + value);
        currentTaxonomy = value;
    }

    /**
     * @return the taxonomies
     */
    public List<Taxonomy> getTaxonomies() {
        return taxonomies;
    }

    /**
     * @param taxonomies the taxonomies to set
     */
    public void setTaxonomies(List<Taxonomy> taxonomies) {
        this.taxonomies = taxonomies;
    }

    /**
     * @return the currentTaxonomy
     */
    public String getCurrentTaxonomy() {
        return currentTaxonomy;
    }

    /**
     * @param currentTaxonomy the currentTaxonomy to set
     */
    public void setCurrentTaxonomy(String currentTaxonomy) {
        this.currentTaxonomy = currentTaxonomy;
    }

    public List<String> getSelectedTaxonomies() {
        return selectedTaxonomies;
    }

    public void setSelectedTaxonomies(List<String> selectedTaxonomies) {
        this.selectedTaxonomies = selectedTaxonomies;
    }

    @FacesConverter(forClass = Post.class)
    public static class PostControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PostController controller = (PostController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "postController");
            return controller.ejbFacade.find(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Post) {
                Post o = (Post) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Post.class.getName());
            }
        }

    }

}
