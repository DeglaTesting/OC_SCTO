/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rdcit.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author sa841
 */
@ManagedBean(name = "FileDownloadView")
@SessionScoped
public class FileDownloadView implements Serializable {

    private static final long serialVersionUID = 1L;
    private StreamedContent file;

    @ManagedProperty(value = "#{Converter}")
    Converter converter;
    
    @ManagedProperty(value = "#{FileUploadView}")
    FileUploadView fileUploadView;

    public FileDownloadView() {
    }

    public StreamedContent download() {
        StreamedContent download = null;
        try {
            download = new DefaultStreamedContent();
            File out = (File) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("outputFile");
            InputStream input = new FileInputStream(out);
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            download = new DefaultStreamedContent(input, externalContext.getMimeType(out.getName()), out.getName());
            reset();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return download;
    }

    public StreamedContent getFile() {
        return file;
    }
    
    public void reset() {
      converter.disableDownloadBt = true;
      fileUploadView.disableConvertBt = true;
    }

    public Converter getConverter() {
        return converter;
    }

    public void setConverter(Converter converter) {
        this.converter = converter;
    }

    public FileUploadView getFileUploadView() {
        return fileUploadView;
    }

    public void setFileUploadView(FileUploadView fileUploadView) {
        this.fileUploadView = fileUploadView;
    }
    
    public void clear(File inputFile, File outputFile){
        try {
            Files.delete(Paths.get(inputFile.toURI()));
            Files.delete(Paths.get(outputFile.toURI()));
        } catch (IOException ex) {
            Logger.getLogger(FileDownloadView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
