/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rdcit.oc_scto;

import java.io.File;
import org.rdcit.tools.SpreadsheetReader;
import org.rdcit.tools.SpreadsheetWriter;
import static org.rdcit.tools.Statics.*;

/**
 *
 * @author sa841
 */
public class Settings {

    File inputFile;
    File outputFile;
    SpreadsheetReader inputFileReader;
    SpreadsheetWriter outputFileWriter;

    Settings(File inputFile, File outputFile, SpreadsheetReader inputFileReader, SpreadsheetWriter outputFileWriter) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.inputFileReader = inputFileReader;
        this.outputFileWriter = outputFileWriter;
    }

    void setSettingsSheet() {
        String crfame = inputFileReader.readCell(CRF_SHEET, 1, CRF_CRF_NAME_CELL);
        outputFileWriter.appendNewCell(SETTINGS_SHEET, SETTINGS_FORM_TITLE_CELL, crfame);
        outputFileWriter.appendNewCell(SETTINGS_SHEET, SETTINGS_FORM_ID_CELL, crfame + "_" + inputFileReader.readCell(CRF_SHEET, 1, CRF_CRF_VERSION_CELL));
    }
}
