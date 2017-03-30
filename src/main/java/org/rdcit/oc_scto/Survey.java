/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rdcit.oc_scto;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
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
    List<String> lChoices = new ArrayList<String>();

    Survey(File inputFile, File outputFile, SpreadsheetReader inputFileReader, SpreadsheetWriter outputFileWriter) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.inputFileReader = inputFileReader;
        this.outputFileWriter = outputFileWriter;
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
            String choiceListName = inputFileReader.readCell(ITEMS_SHEET, readderRowNum, ITEMS_RESPONSE_LABEL_CELL);
            outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_TYPE_CELL, "select_one ".concat(choiceListName));
            setChoicesSheet(choiceListName, readderRowNum);
        } else if (itemResponseType.equals("multi-select") || itemResponseType.equals("checkbox")) {
            String choiceListName = inputFileReader.readCell(ITEMS_SHEET, readderRowNum, ITEMS_RESPONSE_LABEL_CELL);
            outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_TYPE_CELL, "select_multiple ".concat(choiceListName));
            setChoicesSheet(choiceListName, readderRowNum);
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

    void setConstraint(int readderRowNum, int writterRowNum) {
        String constraint = inputFileReader.readCell(ITEMS_SHEET, readderRowNum, ITEMS_VALIDATION_CELL);
        if (!constraint.equals("null")) {
            if (constraint.startsWith("regexp")) {
                outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_CONSTRAINT_CELL, reglex(constraint, readderRowNum));
            }
        }
        String constraintMsg = inputFileReader.readCell(ITEMS_SHEET, readderRowNum, ITEMS_VALIDATION_ERROR_MSG_CELL);
        if (!constraintMsg.equals("null")) {
            outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_CONSTRAINT_MSG_CELL, constraintMsg);
        }
    }

    void setChoicesSheet(String responseLabel, int readerRowNum) {
        if (!lChoices.contains(responseLabel)) {
            lChoices.add(responseLabel);
            Object[] optionsText = splitWithEscape(inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_RESPONSE_OPTIONS_TEXT_CELL), ",", "\\");
            Object[] optionsValue = splitWithEscape(inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_RESPONSE_VALUE_OR_CALCULATIONS_CELL), ",", "\\");
            for (int i = 0; i < optionsText.length; i++) {
                String sOptionText = optionsText[i].toString();
                if (sOptionText.length() > 0) {
                    if (notEmpty(optionsValue[i].toString())) {
                        outputFileWriter.appendNewRow(CHOICES_SHEET);
                        outputFileWriter.appendNewCell(CHOICES_SHEET, CHOICES_LIST_NAME_CELL, responseLabel);
                        outputFileWriter.appendNewCell(CHOICES_SHEET, CHOICES_VALUE_CELL, optionsValue[i].toString());
                        outputFileWriter.appendNewCell(CHOICES_SHEET, CHOICES_LABEL_CELL, optionsText[i].toString());
                    }
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
        if (notEmpty(sConditionDisplay) && !sConditionDisplay.equals("null")) {
            String[] arConditiondisplay = sConditionDisplay.split(",");
            relevance = relevance.concat(arConditiondisplay[0] + "},\'" + arConditiondisplay[1] + "\')");
            outputFileWriter.writeNewCell(SURVEY_SHEET, writterRowNum, SURVEY_RELEVANCE_CELL, relevance);
        }
    }

    boolean isRepeating(String groupLabel) {
        int maxGroupsRow = inputFileReader.getMaxLogicalRow(GROUPS_SHEET);
        int readerRowNum = 1;
        while (readerRowNum <= maxGroupsRow) {
            String itemGroup = inputFileReader.readCell(GROUPS_SHEET, readerRowNum, GROUPS_LABEL_CELL);
            if (itemGroup.equals(groupLabel)) {
                String groupFlag = inputFileReader.readCell(GROUPS_SHEET, readerRowNum, GROUPS_GROUP_LAYOUT_CELL);
                if (groupFlag.equals("GRID")) {
                    return true;
                }
            }
            readerRowNum++;
        }
        return false;
    }

    Hashtable<String, String> setSections() {
        Hashtable<String, String> sectionsLN = new Hashtable<>();
        int maxItems = inputFileReader.getMaxLogicalRow(SECTIONS_SHEET);
        int readerRowNum = 1;
        while (readerRowNum <= maxItems) {
            sectionsLN.put(inputFileReader.readCell(SECTIONS_SHEET, readerRowNum, SECTIONS_SECTION_LABEL_CELL),
                    inputFileReader.readCell(SECTIONS_SHEET, readerRowNum, SECTIONS_SECTION_TITLE_CELL));
            readerRowNum++;
        }
        return sectionsLN;
    }

    void startNewImportingSection(String sectionLabel, String sectionName) {
        outputFileWriter.appendNewRow(SURVEY_SHEET);
        outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_TYPE_CELL, "begin group");
        outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_NAME_CELL, sectionLabel + "_SL");
        outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_LABEL_CELL, sectionName);
    }

    void endNewImportingSection(String sectionLabel) {
        outputFileWriter.appendNewRow(SURVEY_SHEET);
        outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_TYPE_CELL, "end group");
        outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_NAME_CELL, sectionLabel + "_SL");
    }

    void startImportingGroup(String groupLabel, boolean rgFlag) {
        outputFileWriter.appendNewRow(SURVEY_SHEET);
        if (rgFlag) {
            outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_TYPE_CELL, "begin repeat");
        } else {
            outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_TYPE_CELL, "begin group");
        }
        outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_NAME_CELL, groupLabel + "_GL");
    }

    void endImportingGroup(String groupLabel, boolean rgFlag) {
        outputFileWriter.appendNewRow(SURVEY_SHEET);
        if (rgFlag) {
            outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_TYPE_CELL, "end repeat");
        } else {
            outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_TYPE_CELL, "end group");
        }
        outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_NAME_CELL, groupLabel + "_GL");
    }

    void setSurveySheet() {
        int maxItems = inputFileReader.getMaxLogicalRow(ITEMS_SHEET);
        int readerRowNum = 1;
        Hashtable<String, String> sectionsLN = setSections();
        List<String> lSection = new ArrayList<>();
        while (readerRowNum <= maxItems) {
            if (!inputFileReader.isEmptyRow(ITEMS_SHEET, readerRowNum, ITEMS_ITEM_NAME_CELL)) {
                String sectionLabel = inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_SECTION_LABEL_CELL);
                String sectionName = sectionsLN.get(sectionLabel);
                if (lSection.contains(sectionName)) {
                    insertItemIntoSection(readerRowNum, sectionName);
                    readerRowNum++;
                } else {
                    lSection.add(sectionName);
                    startNewImportingSection(sectionLabel, sectionName);
                    while (sectionLabel.equals(inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_SECTION_LABEL_CELL))) {
                        String groupLabel = inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_GROUP_LABEL_CELL);
                        if (!groupLabel.equals("null") && groupLabel.length() > 1) {
                            boolean repeatingGroupFlag = isRepeating(groupLabel);
                            startImportingGroup(groupLabel, repeatingGroupFlag);
                            while (groupLabel.equals(inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_GROUP_LABEL_CELL))) {
                                int writterRowNum = outputFileWriter.getLastRowNum(SURVEY_SHEET) + 1;
                                importingNewItem(readerRowNum, writterRowNum);
                                readerRowNum++;
                            }
                            endImportingGroup(groupLabel, repeatingGroupFlag);
                        } else {
                            int writterRowNum = outputFileWriter.getLastRowNum(SURVEY_SHEET) + 1;
                            importingNewItem(readerRowNum, writterRowNum);
                            readerRowNum++;
                        }
                    }
                    endNewImportingSection(sectionLabel);
                }
            }
        }
    }

    void importingNewItem(int readerRowNum, int writterRowNum) {
        String itemName = inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_ITEM_NAME_CELL);
        outputFileWriter.appendNewRow(SURVEY_SHEET);
        setResponseTypeCell(inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_RESPONSE_TYPE_CELL), itemName, readerRowNum, writterRowNum);
        outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_NAME_CELL, inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_ITEM_NAME_CELL));
        outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_LABEL_CELL, inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_LEFT_ITEM_TEXT_CELL));
        String questionNumber = inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_QUESTION_NUMBER_CELL);
        if ((!questionNumber.equals("null")) && (questionNumber.length() > 1)) {
            String label = questionNumber + ") " + inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_LEFT_ITEM_TEXT_CELL);
            outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_LABEL_CELL, label);
        }
        String rightItemText = inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_RIGHT_ITEM_TEXT_CELL);
        if (!rightItemText.equals("null")) {
            outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_HINT_CELL, rightItemText);
        }
        setRequiredCell(readerRowNum, writterRowNum);
        setRelevanceCell(readerRowNum, writterRowNum);
        setConstraint(readerRowNum, writterRowNum);
    }

    void insertItemIntoSection(int readerRowNum, String sectionName) {
        if (!inputFileReader.isEmptyRow(ITEMS_SHEET, readerRowNum, ITEMS_ITEM_NAME_CELL)) {
            int writterRowNum = outputFileWriter.fingLastLabelOcc(SURVEY_SHEET, SURVEY_NAME_CELL, sectionName + "_SL");
            if (writterRowNum != 0) {
                String itemName = inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_ITEM_NAME_CELL);
                outputFileWriter.appendNewRow(SURVEY_SHEET);
                setResponseTypeCell(inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_RESPONSE_TYPE_CELL), itemName, readerRowNum, writterRowNum);
                outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_NAME_CELL, inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_ITEM_NAME_CELL));
                outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_LABEL_CELL, inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_LEFT_ITEM_TEXT_CELL));
                String questionNumber = inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_QUESTION_NUMBER_CELL);
                if ((!questionNumber.equals("null")) && (questionNumber.length() > 1)) {
                    String label = questionNumber + ") " + inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_LEFT_ITEM_TEXT_CELL);
                    outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_LABEL_CELL, label);
                }
                String rightItemText = inputFileReader.readCell(ITEMS_SHEET, readerRowNum, ITEMS_RIGHT_ITEM_TEXT_CELL);
                if (!rightItemText.equals("null")) {
                    outputFileWriter.appendNewCell(SURVEY_SHEET, SURVEY_HINT_CELL, rightItemText);
                }
                setRequiredCell(readerRowNum, writterRowNum);
                setRelevanceCell(readerRowNum, writterRowNum);
                setConstraint(readerRowNum, writterRowNum);
            }
        }
    }
}
