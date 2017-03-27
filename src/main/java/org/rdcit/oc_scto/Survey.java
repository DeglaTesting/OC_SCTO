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
import static org.rdcit.tools.Strings.*;
import static org.rdcit.oc_scto.SctoPrdefFunctions.*;

/**
 *
 * @author sa841
 */
public class Survey {

    File inputFile;
    File outputFile;
    SpreadsheetReader inputFileReader;
    SpreadsheetWriter outputFileWriter;

    Survey(File inputFile, File outputFile, SpreadsheetReader inputFileReader, SpreadsheetWriter outputFileWriter) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.inputFileReader = inputFileReader;
        this.outputFileWriter = outputFileWriter;
    }

    void startImportingSection(int rowNum) {
        outputFileWriter.appendNewRow(SURVEY_SHEET);
        outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_TYPE_CELL, "begin group");
        outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_NAME_CELL, inputFileReader.readCell(ITEMS_SHEET, rowNum, ITEMS_SECTION_LABEL_CELL));
        String[] sectionParams = setSectionFields(rowNum);
        outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_LABEL_CELL, sectionParams[0]);
        outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_HINT_CELL, sectionParams[1]);
        outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_APPEARANCE_CELL, "field-list");
    }

    String[] setSectionFields(int rowNum) {
        String[] sectionParams = new String[2];
        String sSectionLabel = inputFileReader.readCell(ITEMS_SHEET, rowNum, ITEMS_SECTION_LABEL_CELL);
        int sectionNumber = inputFileReader.getMaxLogicalRow(SECTIONS_SHEET);
        for (int i = 1; i <= sectionNumber; i++) {
            if (sSectionLabel.equals(inputFileReader.readCell(SECTIONS_SHEET, i, SECTIONS_SECTION_LABEL_CELL))) {
                sectionParams[0] = inputFileReader.readCell(SECTIONS_SHEET, i, SECTIONS_SECTION_TITLE_CELL);
                sectionParams[1] = inputFileReader.readCell(SECTIONS_SHEET, i, SECTIONS_INSTRUCTIONS_CELL);
                break;
            }
        }
        return sectionParams;
    }

    void endImportingSection(int newSectionRowNum, String sectionName) {
        outputFileWriter.appendNewRow(SURVEY_SHEET);
        outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_TYPE_CELL, "end group");
        outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_NAME_CELL, sectionName);
    }

    void importingNewItem(int readerRowNum, int writterRowNum) {
        String itemName = inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_ITEM_NAME_CELL);
        outputFileWriter.createNewRow(SURVEY_SHEET, writterRowNum);
        setResponseTypeCell(inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_RESPONSE_TYPE_CELL), itemName, readerRowNum, writterRowNum);
        outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_NAME_CELL, inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_ITEM_NAME_CELL));
        outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_LABEL_CELL, inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_LEFT_ITEM_TEXT_CELL));
        String questionNumber = inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_QUESTION_NUMBER_CELL);
        if (!questionNumber.equals("null")) {
            String label = questionNumber + " " + inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_LEFT_ITEM_TEXT_CELL);
            outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_LABEL_CELL, label);
        }
        String rightItemText = inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_RIGHT_ITEM_TEXT_CELL);
        if (!rightItemText.equals("null")) {
            outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_HINT_CELL, rightItemText);
        }
        setRequiredCell(readerRowNum, writterRowNum);
        setRelevanceCell(readerRowNum, writterRowNum);
    }

    void setResponseTypeCell(String itemResponseType, String itemName, int readderRowNum, int writterRowNum) {
        if (itemResponseType.equals("text")) {
            setTextResponseType(inputFileReader.readCell(ITEMS_SHEET, readderRowNum, ITEMS_DATA_TYPE_CELL), writterRowNum);
        } else if (itemResponseType.equals("textarea")) {
            outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_TYPE_CELL, "text");
        } else if (itemResponseType.equals("file")) {
            outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_TYPE_CELL, "file");
        } else if (itemResponseType.equals("calculation")) {
            setTextResponseType(inputFileReader.readCell(ITEMS_SHEET, readderRowNum, ITEMS_DATA_TYPE_CELL), writterRowNum);
            outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_READ_ONLY_CELL, "yes");
            setCalculationCell(readderRowNum, writterRowNum);
        } else if (itemResponseType.equals("group-calculation")) {
            setTextResponseType(inputFileReader.readCell(ITEMS_SHEET, readderRowNum, ITEMS_DATA_TYPE_CELL), writterRowNum);
            outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_READ_ONLY_CELL, "yes");
            setCalculationRGCell(readderRowNum, writterRowNum);
        } else if (itemResponseType.equals("single-select") || itemResponseType.equals("radio")) {
            outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_TYPE_CELL, "select_one ".concat(itemName));
            setChoicesSheet(itemName, readderRowNum);
        } else if (itemResponseType.equals("multi-select") || itemResponseType.equals("checkbox")) {
            outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_TYPE_CELL, "select_multiple ".concat(itemName));
            setChoicesSheet(itemName, readderRowNum);
        }
    }

    void setTextResponseType(String dataType, int writterRowNum) {
        switch (dataType) {
            case "INT":
                outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_TYPE_CELL, "integer ");
                break;
            case "REAL":
                outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_TYPE_CELL, "decimal ");
                break;
            case "DATE":
                outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_TYPE_CELL, "date");
                break;
            case "ST":
                outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_TYPE_CELL, "text");
                break;
            default:
                break;
        }
    }

    void setCalculationCell(int readderRowNum, int writterRowNum) {
        String initFunc = inputFileReader.readCell(ITEMS_SHEET, readderRowNum, ITEMS_RESPONSE_VALUE_OR_CALCULATIONS_CELL);
        String[] params = splitFirst(initFunc, ":");
        if (params.length == 2) {
            String controller = params[1];
            if (controller.startsWith("decode")) {
                outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_CALCULATION_CELL, decode(controller, readderRowNum));
            } else if (controller.startsWith("range")) {
                outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_CONSTRAINT_CELL, range(controller, readderRowNum));
            } else if (controller.startsWith("max")) {
                outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_CONSTRAINT_CELL, max(controller, readderRowNum));
            } else if (controller.startsWith("min")) {
                outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_CONSTRAINT_CELL, min(controller, readderRowNum));
            } else if (controller.startsWith("sum")) {
                outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_CONSTRAINT_CELL, sum(controller, readderRowNum));
            }
        } else if (params.length < 2) {
            String controller = params[0];
            if (controller.startsWith("avg")) {
                // need to check if avg or avgRG
                outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_CONSTRAINT_CELL, avg(controller, readderRowNum));
            }
        }
    }

    void setCalculationRGCell(int readderRowNum, int writterRowNum) {
        String initFunc = inputFileReader.readCell(ITEMS_SHEET, readderRowNum, ITEMS_GROUP_LABEL_CELL);
        if (initFunc.startsWith("avg")) {
            String repeatingGroupName = inputFileReader.readCell(ITEMS_SHEET, readderRowNum, ITEMS_RESPONSE_OPTIONS_TEXT_CELL);
            outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_CALCULATION_CELL, avgRG(initFunc, repeatingGroupName, readderRowNum));
        }
    }

    void setChoicesSheet(String itemName, int readerRowNum) {
        Object[] optionsText = splitWithEscape(inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_RESPONSE_OPTIONS_TEXT_CELL), ",", "\\");
        Object[] optionsValue = splitWithEscape(inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_RESPONSE_VALUE_OR_CALCULATIONS_CELL), ",", "\\");
        for (int i = 0; i < optionsText.length; i++) {
            String sOptionText = optionsText[i].toString();
            if (sOptionText.length() > 0) {
                outputFileWriter.appendNewRow(CHOICES_SHEET);
                outputFileWriter.appendNewCell(CHOICES_SHEET, CHOICES_LIST_NAME_CELL, itemName);
                outputFileWriter.appendNewCell(CHOICES_SHEET, CHOICES_LABEL_CELL, optionsText[i].toString());
                if (notEmpty(optionsValue[i].toString())) {
                    outputFileWriter.appendNewCell(CHOICES_SHEET, CHOICES_VALUE_CELL, optionsValue[i].toString());
                } else {
                    outputFileWriter.appendNewCell(CHOICES_SHEET, CHOICES_VALUE_CELL, "null");
                }
            }
        }
    }

    void setRequiredCell(int readerRowNum, int writterRowNum) {
        if (inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_REQUIRED_CELL).equals("1")) {
            outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_REQUIRED_CELL, "yes");
        } else {
            outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_REQUIRED_CELL, "no");
        }
    }

    void setRelevanceCell(int readerRowNum, int writterRowNum) {
        String relevance = "selected(${";
        String sConditionDisplay = inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_SIMPLE_CONDITION_CELL);
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  sConditionDisplay " + sConditionDisplay);
        if (notEmpty(sConditionDisplay) && !sConditionDisplay.equals("null")) {
            String[] arConditiondisplay = sConditionDisplay.split(",");
            relevance = relevance.concat(arConditiondisplay[0] + "},\'" + arConditiondisplay[1] + "\')");
            outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_RELEVANCE_CELL, relevance);
        }
    }

    void importGroup(int readerRowNum, boolean repeatingFg) {
        String grouptTypeBegin;
        String groupTypeEnd;
        if (repeatingFg) {
            grouptTypeBegin = "begin repeat";
            groupTypeEnd = "end repeat";
        } else {
            grouptTypeBegin = "begin group";
            groupTypeEnd = "end group";
        }
        String groupLabel = inputFileReader.readCell(GROUPS_SHEET, readerRowNum, GROUPS_LABEL_CELL);
        String groupHeader = inputFileReader.readCell(GROUPS_SHEET, readerRowNum, GROUP_GROUP_HEADER_CELL);
        outputFileWriter.appendNewRow(SURVEY_SHEET);
        outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_TYPE_CELL, grouptTypeBegin);
        outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_NAME_CELL, groupLabel);
        if (!groupHeader.equals("null")) {
            outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_LABEL_CELL, groupHeader);
        }
        outputFileWriter.appendNewRow(SURVEY_SHEET);
        outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_TYPE_CELL, groupTypeEnd);
        outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_NAME_CELL, groupLabel);
        if (!groupHeader.equals("null")) {
            outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_LABEL_CELL, groupHeader);
        }
    }

    void importingGroups() {
        int maxGroupsRows = inputFileReader.getMaxLogicalRow(GROUPS_SHEET);
        int i = 1;
        while (i <= maxGroupsRows) {
            if (!inputFileReader.isEmptyRow(GROUPS_SHEET, i, GROUPS_LABEL_CELL)) {
                importGroup(i, isRepeating(i));
            }
            i++;
        }
    }

    boolean isRepeating(int groupsRowNum) {
        String groupLayout = inputFileReader.readCell(GROUPS_SHEET, groupsRowNum, GROUPS_GROUP_LAYOUT_CELL);
        return groupLayout.equals("GRID");
    }

    void setSurveySheet() {
        int maxItems = inputFileReader.getMaxLogicalRow(ITEMS_SHEET);
        int readerRowNum = 1;
        importingGroups();
        while (readerRowNum <= maxItems) {
            if (!inputFileReader.isEmptyRow(ITEMS_SHEET, readerRowNum, ITEMS_ITEM_NAME_CELL)) {
                String itemGroup = inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_GROUP_LABEL_CELL);
                int writterRowNum = outputFileWriter.fingLastLabelOcc(SURVEY_SHEET, SURVEY_NAME_CELL, itemGroup);
                if (writterRowNum != 0) {
                    importingNewItem(readerRowNum, writterRowNum);
                } else {
                    importingNewItem(readerRowNum, outputFileWriter.getLastRowNum(SURVEY_SHEET) + 1);
                }
            }
            readerRowNum++;
        }
    }
}
