/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rdcit.beans;

import java.io.File;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.rdcit.oc_scto.FromOcToScto;
import org.rdcit.scto_oc.FromSctoToOC;
import static org.rdcit.tools.Statics.workingRepository;

/**
 *
 * @author sa841
 */
@ManagedBean(name = "Converter")
@SessionScoped
public class Converter {

    File resultConvert;
    boolean disableDownloadBt = true;
    
    public Converter(){}
    

    public void convert() {
        String method = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("calledMethod").toString();
        String sourceFile = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sourceFile").toString();
        File inputFile = new File(workingRepository + sourceFile);
        if (method.equals("OC_SCTO")) {
            FromOcToScto fromOcToScto = new FromOcToScto(inputFile);
            resultConvert = fromOcToScto.convert();
        } else if (method.equals("SCTO_OC")) {
            FromSctoToOC fromSctoToOC = new FromSctoToOC(inputFile);
            resultConvert = fromSctoToOC.convert();
        }
       FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("outputFile", resultConvert);
       disableDownloadBt = false;
    }
    
    public boolean isDisableDownloadBt() {
        return disableDownloadBt;
    }
}
