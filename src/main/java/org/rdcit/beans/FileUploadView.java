/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rdcit.beans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;
import static org.rdcit.tools.Statics.workingRepository;

/**
 *
 * @author sa841
 */
@ManagedBean(name = "FileUploadView")
@SessionScoped
public class FileUploadView {

    private final String destination = workingRepository;
    boolean disableConvertBt = true;

    public void upload(FileUploadEvent event) {
        try {
            String method = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("calledMethod").toString();
            FacesMessage message;
            Object[] valid = validate(method, event.getFile().getFileName());
            if (!(boolean) valid[0]) {
                message = new FacesMessage("Error", (String) valid[1]);
            } else {
                copyFile(event.getFile().getFileName(), event.getFile().getInputstream());
                message = new FacesMessage(event.getFile().getFileName(), "Your file is successflly uploaded");
            }
            FacesContext.getCurrentInstance().addMessage("", message);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void copyFile(String fileName, InputStream in) {
        try {
            OutputStream out = new FileOutputStream(new File(destination + fileName));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("sourceFile", fileName);
            disableConvertBt = false;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public Object[] validate(String method, String file) {
        Object[] result = new Object[2];
        switch (method) {
            case "OC_SCTO":
                result[0] = file.endsWith(".xls");
                result[1] = "Wrong file format, \".xls file expected.";
                break;
            case "SCTO_OC":
                result[0] = file.endsWith(".xlsx");
                result[1] = "Wrong file format, \".xlsx file expected.";
                break;
            case "":
                result[0] = false;
                result[1] = "Please choose a procedure first. ";
                break;
            default:
                result[0] = false;
                result[1] = "Sorry, something wrong occured ";
                break;
        }
        return result;
    }

    public boolean isDisableConvertBt() {
        return disableConvertBt;
    }

    public void setDisableConvertBt(boolean disableConvertBt) {
        this.disableConvertBt = disableConvertBt;
    }

    public FileUploadView() {
    }
    
    
}
