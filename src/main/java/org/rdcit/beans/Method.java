/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rdcit.beans;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import org.rdcit.tools.TemplateFactory;

/**
 *
 * @author sa841
 */
@ManagedBean(name = "Method")
@SessionScoped
public class Method implements Serializable {

    private static final long serialVersionUID = 1L;
    String calledMethod;

    boolean disableUploadBt = true;
    
    public Method(){}

    public String getCalledMethod() {
        return calledMethod;
    }

    public void setCalledMethod(String calledMethod) {
        this.calledMethod = calledMethod;
    }

    public void valueChangeMethod(ValueChangeEvent e) {
        if (e.getNewValue() == null) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Please choose the appropriate procedure!");
            FacesContext.getCurrentInstance().addMessage(null, message);
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().clear();
            TemplateFactory.deleteTempFiles();
            this.calledMethod = e.getNewValue().toString();
            disableUploadBt = false;
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("calledMethod", this.calledMethod);
        }
    }

    public boolean isDisableUploadBt() {
        return disableUploadBt;
    }

    public void setDisableUploadBt(boolean disableUploadBt) {
        this.disableUploadBt = disableUploadBt;
    }

}
