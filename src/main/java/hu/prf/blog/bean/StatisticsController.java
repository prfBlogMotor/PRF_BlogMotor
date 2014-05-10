/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.prf.blog.bean;

import hu.prf.blog.bean.session.VisitingFacade;
import hu.prf.blog.entity.Visiting;
import hu.prf.blog.entity.Visiting_;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.DateAxis;


/**
 *
 * @author Bali
 */
@ManagedBean
@SessionScoped
public class StatisticsController {

    @EJB
    VisitingFacade visitingFacade;
    
    List<Visiting> visitingsInActualMonth;
    
    private BarChartModel barModel;
 
    public StatisticsController() {
        createBarModels();
    }
 
    public BarChartModel getBarModel() {
        return barModel;
    }
 
    private BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();
 
        ChartSeries boys = new ChartSeries();
        boys.setLabel("Boys");
        boys.set("2014-01-01", 51);
        boys.set("2014-01-06", 22);
        boys.set("2014-01-12", 65);
        boys.set("2014-01-18", 74);
        boys.set("2014-01-24", 24);
        boys.set("2014-01-30", 51);
 
        ChartSeries girls = new ChartSeries();
        girls.setLabel("Girls");
        girls.set("2014-01-01", 32);
        girls.set("2014-01-06", 73);
        girls.set("2014-01-12", 24);
        girls.set("2014-01-18", 12);
        girls.set("2014-01-24", 74);
        girls.set("2014-01-30", 62);
 
        model.addSeries(boys);
        model.addSeries(girls);
         
        return model;
    }
     
    private void createBarModels() {
        createBarModel();
    }
     
    private void createBarModel() {
//        visitingsInActualMonth = visitingFacade.getVisitingsInActualMonth();
//        System.out.println("Visitings count: " + visitingsInActualMonth.size());
        
        barModel = initBarModel();
         
        barModel.setTitle("Bar Chart");
        barModel.setLegendPosition("ne");
         
        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Gender");
         
        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Births");
        yAxis.setMin(0);
        yAxis.setMax(200);
    }
}
