package hu.prf.blog.bean;

import hu.prf.blog.bean.session.UserFacade;
import hu.prf.blog.entity.Visiting;
import hu.prf.blog.bean.util.JsfUtil;
import hu.prf.blog.bean.util.PaginationHelper;
import hu.prf.blog.bean.session.VisitingFacade;
import hu.prf.blog.entity.User;
import hu.prf.blog.util.DailyStat;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

@ManagedBean(name = "visitingController")
@SessionScoped
public class VisitingController implements Serializable {

    private Visiting current;
    private DataModel items = null;
    @EJB
    private hu.prf.blog.bean.session.VisitingFacade ejbFacade;
    @EJB
    private UserFacade userFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    
    private Calendar from;
    private Calendar to;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    private BarChartModel barModel;

    public VisitingController() {
        setCalendars();
    }
    
    public BarChartModel getBarModel() {
        return barModel;
    }
    
    private void setCalendars() {
        to = Calendar.getInstance();
        to.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        to.clear(Calendar.MINUTE);
        to.clear(Calendar.SECOND);
        to.clear(Calendar.MILLISECOND);
        to.set(Calendar.DAY_OF_MONTH, to.get(Calendar.DAY_OF_MONTH) + 1);
        
        from = (Calendar)to.clone();
        from.set(Calendar.MONTH, to.get(Calendar.MONTH) - 1);
    }
    
    public List<Visiting> getVisitingsInThisMonth() {
        return getFacade().getVisitingsInActualMonth2(from, to);
    }
    
    public List<DailyStat> dailyStats = new ArrayList<DailyStat>();
    
    public void loadDailyStats() {
        List<Visiting> visitingsInThisMonth = getVisitingsInThisMonth();
        
        User defaultUser = userFacade.getUnknownUser();
        
        Calendar newFrom = (Calendar)from.clone();
        while (newFrom.getTime().before(to.getTime())) {
            DailyStat ds = new DailyStat();
            ds.setDate(sdf.format(newFrom.getTime()));
            ds.setDay(newFrom.get(Calendar.DAY_OF_MONTH));
            
            for (Visiting visiting : visitingsInThisMonth) {
                if (visiting.getDate().getTime() > getDayStart(newFrom).getTime().getTime() && visiting.getDate().getTime() < getDayEnd(newFrom).getTime().getTime()) {
                    if (defaultUser.getId() == visiting.getUserid().getId())
                        ds.setUnknownCount(ds.getUnknownCount() + 1);
                    else
                        ds.setUserCount(ds.getUserCount() + 1);
                }
            }
            dailyStats.add(ds);
            newFrom.add(Calendar.DAY_OF_YEAR, 1);
        }
    }
    
    private BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();
 
        ChartSeries users = new ChartSeries();
        users.setLabel("Users");
        ChartSeries unknowns = new ChartSeries();
        unknowns.setLabel("Unknown Users");
        for (DailyStat ds : dailyStats) {
            users.set(ds.getDay(), ds.getUserCount());
            unknowns.set(ds.getDay(), ds.getUnknownCount());
        }
 
        model.addSeries(users);
        model.addSeries(unknowns);
         
        return model;
    }
    
    private void createBarModel() {
//        visitingsInActualMonth = visitingFacade.getVisitingsInActualMonth();
//        System.out.println("Visitings count: " + visitingsInActualMonth.size());
        
        barModel = initBarModel();
         
        barModel.setTitle("Látogatók statisztikája");
        barModel.setLegendPosition("ne");
         
        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Napok");
         
        int max = 0;
        for (DailyStat ds : dailyStats) {
            if (ds.getUnknownCount() > max)
                max = ds.getUnknownCount();
            if (ds.getUserCount() > max)
                max = ds.getUserCount();
        }
        
        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Felhasználók száma");
        yAxis.setMin(0);
        yAxis.setMax(max + 1);
    }
    
    private Calendar getDayStart(Calendar cal) {
        Calendar clone = (Calendar)cal.clone();
        
        clone.set(Calendar.HOUR_OF_DAY, 0);
        clone.set(Calendar.MINUTE, 0);
        clone.set(Calendar.SECOND, 0);
        clone.set(Calendar.MILLISECOND, 0);
        
        return clone;
    }
    
    private Calendar getDayEnd(Calendar cal) {
        Calendar clone = (Calendar)cal.clone();
        
        clone.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
        clone.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
        clone.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
        clone.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));
        
        return clone;
    }

    public Visiting getSelected() {
        if (current == null) {
            current = new Visiting();
            selectedItemIndex = -1;
        }
        return current;
    }

    private VisitingFacade getFacade() {
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
        return "List";
    }
    
    public String prepareStatistics() {
        recreateModel();
        loadDailyStats();
        createBarModel();
        return "/statistics/Visiting.xhtml";
    }

    public String prepareView() {
        current = (Visiting) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Visiting();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("VisitingCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Visiting) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("VisitingUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Visiting) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
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
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("VisitingDeleted"));
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

    @FacesConverter(forClass = Visiting.class)
    public static class VisitingControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            VisitingController controller = (VisitingController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "visitingController");
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
            if (object instanceof Visiting) {
                Visiting o = (Visiting) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Visiting.class.getName());
            }
        }

    }

}
