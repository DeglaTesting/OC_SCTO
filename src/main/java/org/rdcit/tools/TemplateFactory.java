/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rdcit.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.rdcit.tools.Statics.*;

/**
 *
 * @author sa841
 */
public class TemplateFactory {

    public static void setWorkingRepository() {
    }

    public static void createCrfTemplate(File outputFile) {
        try {
            Files.copy(Paths.get(OC_DESIGN_TEMPLATE.toURI()), Paths.get(outputFile.toURI()), REPLACE_EXISTING);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void createSctoTemplate(File outputFile) {
        try {
            Files.copy(Paths.get(SCTO_DESIGN_TEMPLATE.toURI()), Paths.get(outputFile.toURI()), REPLACE_EXISTING);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void deleteTempFiles() {
        File directory = new File(workingRepository);
        for (File f : directory.listFiles()) {
            if((!f.getName().equals(OC_DESIGN_TEMPLATE.getName())) && (!f.getName().equals(SCTO_DESIGN_TEMPLATE.getName())))
            if (f.getName().endsWith(".xls") || f.getName().endsWith(".xlsx") ) {
                f.delete();
            }
        }
    }
}
