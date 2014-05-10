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
    private Visiting visiting;
    
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
        //System.out.println(" --- LOGIN: ");
        current = getFacade().GetUserIfAuthenticated(username, password);
        FacesContext context = FacesContext.getCurrentInstance();
        if (current != null) {
            context.addMessage(null, new FacesMessage(" Üdvözöllek, ", current.getUsername() + "!"));
            createVisiting();
            
            return "success";
        }
        context.addMessage(null, new FacesMessage(" Hiba ", "Nem megfelelő név vagy jelszó!"));
        return null;
    }
    
    public String register() {
        //System.out.println(" --- REGISTER: ");
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
        return "post/List?faces-redirect=true";
    }
    
    public String logout() {
        //System.out.println(" Logout: ");
        FacesContext context = FacesContext.getCurrentInstance();
        current = null;
        context.addMessage(null, new FacesMessage(" Kijelentkezve. "));
        return "success";
    }
    
    public boolean isUserLoggedIn() {
        User unknown = getFacade().getUnknownUser();
        
        if (current == null || current.getId() == unknown.getId()) {
            current = unknown;
            createVisiting();
            return false;
        }
        return true;
    }
    
    public String navigateLoginPage() {
        return "/Login?faces-redirect=true";
    }
    
    public String navigateRegisterPage() {
        return "/Register?faces-redirect=true";
    }
    
    private void createVisiting() {
        if (visiting == null) {
            visiting = new Visiting();
            visiting.setUserid(current);
            visiting.setDate(Calendar.getInstance().getTime());
            
            visitingFacade.create(visiting);
        } else {
            visiting.setUserid(current);
            visitingFacade.edit(visiting);
        }
        
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
