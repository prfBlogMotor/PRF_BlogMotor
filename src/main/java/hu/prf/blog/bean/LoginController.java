/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.prf.blog.bean;

import hu.prf.blog.bean.session.UserFacade;
import hu.prf.blog.bean.session.VisitingFacade;
import hu.prf.blog.entity.User;
import hu.prf.blog.entity.Visiting;
import java.beans.Visibility;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Bali
 */
@ManagedBean(name = "loginController")
@SessionScoped
public class LoginController implements Serializable {
    private User current;
    private String username;
    private String password;
    
    @EJB
    private hu.prf.blog.bean.session.UserFacade ejbFacade;
    @EJB
    private VisitingFacade visitingFacade;
    
    public LoginController() {
    }
    
    private UserFacade getFacade() {
        return ejbFacade;
    }
    
    public String login() {
        System.out.println(" --- LOGIN: ");
        current = getFacade().GetUserIfAuthenticated(username, password);
        
        if (current != null) {
            System.out.println(" --- LOGIN: " + current.getUsername());
            createVisiting();
            return "posts/List?faces-redirect=true";
        }
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error", "Wrong user name or password!"));
        
        return null;
    }
    
    public String register() {
        System.out.println(" --- REGISTER: ");
        if (username.isEmpty() || password.isEmpty()) {
            FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error", "User name and password are required!"));
            return null;
        }
        current = new User();
        current.setUsername(username);
        current.setPassword(password);
        
        getFacade().create(current);
        
        createVisiting();
        
        return "posts/List?faces-redirect=true";
    }
    
    public String logout() {
        current = null;
        return "/Login?faces-redirect=true";
    }
    
    public String navigateLoginPage() {
        return "/Login?faces-redirect=true";
    }
    
    public String navigateRegisterPage() {
        return "/Register?faces-redirect=true";
    }
    
    private void createVisiting() {
        Visiting visiting = new Visiting();
        visiting.setUserid(current);
        visiting.setDate(Calendar.getInstance().getTime());
        visitingFacade.create(visiting);
    }
    
    public String navigateHomePage() {
        current = getFacade().getUnknownUser();
        createVisiting();
        return "posts/List?faces-redirect=true";
    }
    
    public boolean isRenderingLoginButton() {
        return current == null || UserFacade.isDefaultUser(current);
    }
    
    public boolean isRenderingLogoutButton() {
        return current != null || !UserFacade.isDefaultUser(current);
    }

    /**
     * @return the current
     */
    public User getCurrent() {
        return current;
    }

    /**
     * @param current the current to set
     */
    public void setCurrent(User current) {
        this.current = current;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    
}
