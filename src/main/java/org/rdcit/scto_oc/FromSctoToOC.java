/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rdcit.scto_oc;

import java.io.File;
import org.rdcit.tools.Log;
import org.rdcit.tools.SpreadsheetReader;
import org.rdcit.tools.SpreadsheetWriter;
import static org.rdcit.tools.Statics.workingRepository;
import org.rdcit.tools.TemplateFactory;

/**
 *
 * @author sa841
 */
public class FromSctoToOC {

    File inputFile;
    File outputFile;
    SpreadsheetReader inputFileReader;
    SpreadsheetWriter outputFileWriter;
    
    
    public FromSctoToOC(File inputFile){
    this.inputFile = inputFile;}

    void init() {
        String outputFileName = inputFile.getName().replace(".xlsx", ".xls");
        System.out.println("name = " + outputFileName);
        outputFile = new File(workingRepository +outputFileName);
        TemplateFactory.createCrfTemplate(outputFile);
        inputFileReader = new SpreadsheetReader(inputFile);
        outputFileWriter = new SpreadsheetWriter(outputFile);
        Log.LOGGER.info("   Begin converting "+inputFile.getName()+ " to "+outputFile.getName()+" ####");
    }

    void close() {
        outputFileWriter.close(outputFile);
        inputFileReader.close();
    }

   public File convert() {
        init();
        CRF crf = new CRF(inputFile, outputFile, inputFileReader, outputFileWriter);
        Items items = new Items(inputFile, outputFile, inputFileReader, outputFileWriter);
        crf.setCrfSheet();
        items.setItemsSheet();
        close();
        Log.LOGGER.info("   End converting "+inputFile.getName()+ " to "+outputFile.getName()+" ####");
        return outputFile;
    }

    public static void main(String[] args) {
        FromSctoToOC fromSctoToOC = new FromSctoToOC(new File(workingRepository +"Dummy1.xlsx"));
        fromSctoToOC.convert();
    }
}
