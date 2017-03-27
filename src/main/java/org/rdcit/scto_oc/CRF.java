/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rdcit.scto_oc;

import java.io.File;
import java.util.Random;
import org.rdcit.tools.SpreadsheetReader;
import org.rdcit.tools.SpreadsheetWriter;
import static org.rdcit.tools.Statics.*;

/**
 *
 * @author sa841
 */
public class CRF {

    File inputFile;
    File outputFile;
    SpreadsheetReader inputFileReader;
    SpreadsheetWriter outputFileWriter;
    
    
    CRF(File inputFile, File outputFile, SpreadsheetReader inputFileReader,SpreadsheetWriter outputFileWriter){
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.inputFileReader = inputFileReader;
        this.outputFileWriter = outputFileWriter;
    }

    void setCrfSheet() {
        outputFileWriter.appendNewRow(CRF_SHEET);
        outputFileWriter.appendNewCell(CRF_SHEET, CRF_CRF_NAME_CELL, inputFileReader.readCell(SETTINGS_SHEET, 1, SETTINGS_FORM_ID_CELL));
        Random rand = new Random();
        outputFileWriter.appendNewCell(CRF_SHEET, CRF_CRF_VERSION_CELL, String.valueOf(rand.nextInt(10)));
        outputFileWriter.appendNewCell(CRF_SHEET, CRF_VERSION_DESCRIPTION_CELL, "Generated through a Survey CTO Form");
        outputFileWriter.appendNewCell(CRF_SHEET, CRF_REVISION_NOTES_CELL, "The version number is generated randomly");
    }

}
