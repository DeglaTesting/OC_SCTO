/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rdcit.oc_scto;

import java.io.File;
import org.rdcit.tools.SpreadsheetReader;
import org.rdcit.tools.SpreadsheetWriter;
import static org.rdcit.tools.Statics.workingRepository;
import org.rdcit.tools.TemplateFactory;

/**
 *
 * @author sa841
 */
public class FromOcToScto {

    File inputFile;
    File outputFile;
    SpreadsheetReader inputFileReader;
    SpreadsheetWriter outputFileWriter;

    public FromOcToScto(File inputFile) {
        this.inputFile = inputFile;
    }

    void init() {
        String outputFileName = inputFile.getName().replace(".xls", ".xlsx");
        outputFile = new File(workingRepository + outputFileName);
        TemplateFactory.createSctoTemplate(outputFile);
        inputFileReader = new SpreadsheetReader(inputFile);
        outputFileWriter = new SpreadsheetWriter(outputFile);
    }

    void close() {
        outputFileWriter.close(outputFile);
        inputFileReader.close();
    }

    public File convert() {
        init();
        Settings settings = new Settings(inputFile, outputFile, inputFileReader, outputFileWriter);
        settings.setSettingsSheet();
        Survey survey = new Survey(inputFile, outputFile, inputFileReader, outputFileWriter);
        survey.setSurveySheet();
        close();
        return outputFile;
    }

    public static void main(String[] args) {
        FromOcToScto fromOcToScto = new FromOcToScto(new File(workingRepository + "60F_PMG_SUBJECTS_V11.xls"));
        fromOcToScto.convert();
    }
}
