/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rdcit.scto_oc;

import java.io.File;
import java.util.HashMap;
import java.util.Set;
import static org.rdcit.tools.DataType.*;
import org.rdcit.tools.Log;
import org.rdcit.tools.SpreadsheetReader;
import org.rdcit.tools.SpreadsheetWriter;
import static org.rdcit.tools.Statics.*;
import static org.rdcit.tools.Strings.*;

/**
 *
 * @author sa841
 */
public class Items {

    File inputFile;
    File outputFile;
    SpreadsheetReader inputFileReader;
    SpreadsheetWriter outputFileWriter;
    int maxSheetRow;

    Items(File inputFile, File outputFile, SpreadsheetReader inputFileReader, SpreadsheetWriter outputFileWriter) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.inputFileReader = inputFileReader;
        this.outputFileWriter = outputFileWriter;
        maxSheetRow = inputFileReader.getMaxLogicalRow(SURVEY_SHEET);
    }

    int getStartingRow() {
        int i = 1;
        while ((i < maxSheetRow) && (inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_LABEL_CELL)).equals("null")) {
            i++;
        }
        return i;
    }

    void setSections(String newSectionLabel, String newSectionTitle, String instruction) {
        if (outputFileWriter.ifCellValueExist(SECTIONS_SHEET, SECTIONS_SECTION_LABEL_CELL, newSectionLabel) == 0) {
            outputFileWriter.appendNewRow(SECTIONS_SHEET);
            outputFileWriter.appendNewCell(SECTIONS_SHEET, SECTIONS_SECTION_LABEL_CELL, newSectionLabel);
            outputFileWriter.appendNewCell(SECTIONS_SHEET, SECTIONS_SECTION_TITLE_CELL, newSectionTitle);
            if (!instruction.equals("null")) {
                outputFileWriter.appendNewCell(SECTIONS_SHEET, SECTIONS_INSTRUCTIONS_CELL, instruction);
            }
        }
    }

    void setGroups(String newGroupLabel, String layoutCell) {
        if (outputFileWriter.ifCellValueExist(GROUPS_SHEET, GROUPS_LABEL_CELL, newGroupLabel) == 0) {
            outputFileWriter.appendNewRow(GROUPS_SHEET);
            outputFileWriter.appendNewCell(GROUPS_SHEET, GROUPS_LABEL_CELL, newGroupLabel);
            outputFileWriter.appendNewCell(GROUPS_SHEET, GROUPS_GROUP_LAYOUT_CELL, layoutCell);
        }
    }

    void setItem(String itemName, String leftItemName, String itemType, String required, String sectionLabel, String groupLabel, String relevance, String hint, int inputFileRowNum, boolean rgFlag) {
        outputFileWriter.appendNewRow(ITEMS_SHEET);
        outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_ITEM_NAME_CELL, itemName);
        outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_DESCRIPTION_LABEL_CELL, itemName);
        outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_LEFT_ITEM_TEXT_CELL, leftItemName);
        outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_REQUIRED_CELL, required);
        outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_SECTION_LABEL_CELL, sectionLabel);
        outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_GROUP_LABEL_CELL, groupLabel);
        if (!inputFileReader.isEmptyRow(SURVEY_SHEET, inputFileRowNum, SURVEY_CALCULATION_CELL)) {
            setCalculationItem(inputFileRowNum);
        } else {
            setItemResponseType(itemType);
        }
        if (!relevance.equals("null")) {
            setRelevance(relevance, inputFileRowNum, rgFlag);
        }
        setRightItemText(hint);
    }

    void setCalculationItem(int inputFileRowNum) {
        outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_RESPONSE_TYPE_CELL, "calculation");
        setItemDataType(inputFileReader.readCell(SURVEY_SHEET, inputFileRowNum, SURVEY_TYPE_CELL)); //outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_DATA_TYPE_CELL, "ST");
        outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_RESPONSE_LABEL_CELL, "calc" + inputFileRowNum);
        outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_RESPONSE_OPTIONS_TEXT_CELL, "calc" + inputFileRowNum);
        outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_DISPLAY_STATUS_CELL, "HIDE");
        outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_RESPONSE_VALUE_OR_CALCULATIONS_CELL, getFunctionCalc(inputFileRowNum));
    }

    String getFunctionCalc(int inputFileRowNum) {
        String initFunction = inputFileReader.readCell(SURVEY_SHEET, inputFileRowNum, SURVEY_CALCULATION_CELL);
        // check if there is any other constrain in SCTO starting with "if(Selected..."
        if (initFunction.startsWith("if(selected")) {
            return decode(initFunction, inputFileRowNum);
        } else if (initFunction.startsWith("max")) {
            return max(initFunction, inputFileRowNum);
        } else if (initFunction.startsWith("min")) {
            return min(initFunction, inputFileRowNum);
        } else if (ifSum(initFunction, inputFileRowNum)) {
            return sum(initFunction, inputFileRowNum);
        } else if (ifAvg(initFunction, inputFileRowNum)) { 
            return avg(initFunction, inputFileRowNum);
        } else if (ifAvgRG(initFunction, inputFileRowNum)) { 
            return avgRG(initFunction, inputFileRowNum);
        } else {
            return "";
        }
    }

    void setItemResponseType(String responseType) {
        if (responseType.contains("select_multiple")) {
            outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_RESPONSE_TYPE_CELL, "checkbox");
            String choiceListName = responseType.split(" ")[1];
            outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_RESPONSE_LABEL_CELL, choiceListName);
            setChoicesList(choiceListName, getChoicesList(choiceListName));
        } else if (responseType.contains("select_one")) {
            String choiceListName = responseType.split(" ")[1];
            outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_RESPONSE_LABEL_CELL, choiceListName);
            setChoicesList(choiceListName, getChoicesList(choiceListName));
            outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_RESPONSE_TYPE_CELL, "radio");
        } else {
            String tResponseType = endTrim(responseType);
            outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_RESPONSE_TYPE_CELL, "text");
            setItemDataType(tResponseType);
        }
    }

    void setItemDataType(String responseType) {
        switch (responseType) {
            case "text":
                outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_DATA_TYPE_CELL, "ST");
                break;
            case "date":
                outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_DATA_TYPE_CELL, "DATE");
                break;
            case "integer":
                outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_DATA_TYPE_CELL, "INT");
                break;
            case "decimal":
                outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_DATA_TYPE_CELL, "REAL");
                break;
            case "file":
            case "audio":
            case "video":
            case "image":
                outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_RESPONSE_TYPE_CELL, "file");
                outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_DATA_TYPE_CELL, "FILE");
                outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_RESPONSE_LABEL_CELL, "FILE");
                break;
            default:
        }
    }

    HashMap getChoicesList(String choicesListName) {
        HashMap choiceList = new HashMap();
        int i = 1;
        while (i < inputFileReader.getMaxLogicalRow(CHOICES_SHEET)) {
            if (inputFileReader.readCell(CHOICES_SHEET, i, CHOICES_LIST_NAME_CELL).equals(choicesListName)) {
                while (inputFileReader.readCell(CHOICES_SHEET, i, CHOICES_LIST_NAME_CELL).equals(choicesListName)) {
                    choiceList.put(replaceEscape(inputFileReader.readCell(CHOICES_SHEET, i, CHOICES_LABEL_CELL), ",", "\\\\,"),
                            inputFileReader.readCell(CHOICES_SHEET, i, CHOICES_VALUE_CELL));
                    i++;
                }
                break;
            }
            i++;
        }
        inputFileReader.readCell(SURVEY_SHEET, i, ITEMS_ITEM_NAME_CELL);
        return choiceList;
    }

    void setChoicesList(String choiceListName, HashMap choiceList) {
        Set<String> choicesLabel = choiceList.keySet();
        String sItemResponseLabel = "";
        String sItemResponseOption = "";
        for (String s : choicesLabel) {
            sItemResponseLabel = sItemResponseLabel.concat(s + ",");
            sItemResponseOption = sItemResponseOption.concat((String) choiceList.get(s) + ",");
        }
        sItemResponseOption = sItemResponseOption.substring(0, sItemResponseOption.length() - 1);
        sItemResponseLabel = sItemResponseLabel.substring(0, sItemResponseLabel.length() - 1);
        setChoiseListDataType((String) choiceList.values().iterator().next());
        outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_RESPONSE_OPTIONS_TEXT_CELL, sItemResponseLabel);
        outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_RESPONSE_VALUE_OR_CALCULATIONS_CELL, sItemResponseOption);
        outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_RESPONSE_LABEL_CELL, choiceListName);
    }

    String setRequiredField(String required) {
        if (required.equals("yes")) {
            return "1";
        } else {
            return "0";
        }
    }

    void setChoiseListDataType(String s) {
        if (isInteger(s)) {
            outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_DATA_TYPE_CELL, "INT");
        } else if (isReal(s)) {
            outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_DATA_TYPE_CELL, "REAL");
        } else {
            outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_DATA_TYPE_CELL, "ST");
        }
    }

    void setRelevance(String s, int inputFileRowNum, boolean rgFlag) {
        if (!rgFlag) {
            String[] relevance = s.split("and");
            if (relevance.length > 1) {
                Log.LOGGER.error(inputFile + ":" + ++inputFileRowNum + ":Relevance - OC : Simple Conditional Display supports only one condition for each field.");
            } else if (relevance[0].startsWith("selected")) {
                String[] conditionDisplay = relevance[0].split(",");
                if (conditionDisplay.length == 2) {
                    int filedNameStartIndex = relevance[0].indexOf('{') + 1;
                    int filedNameLastIndex = relevance[0].lastIndexOf('}');
                    String fieldName = relevance[0].substring(filedNameStartIndex, filedNameLastIndex);
                    int fieldValueBeginIndex = relevance[0].indexOf('\'') + 1;
                    int fieldValueEndIndex = relevance[0].lastIndexOf('\'');
                    String fieldValue = relevance[0].substring(fieldValueBeginIndex, fieldValueEndIndex);
                    String choiceListName = inputFileReader.readCell(SURVEY_SHEET, inputFileReader.getRowNum(SURVEY_SHEET, SURVEY_NAME_CELL, fieldName),
                            SURVEY_TYPE_CELL).split(" ")[1];
                    String fieldResponse = findCorrespondingResponseLabel(choiceListName, fieldValue);
                    outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_DISPLAY_STATUS_CELL, "HIDE");
                    outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_SIMPLE_CONDITION_CELL, fieldName + "," + fieldValue + ",only show if the answer is " + fieldResponse);
                } else {
                    Log.LOGGER.error(inputFile.getName() + ":" + ++inputFileRowNum + ":Relevance - OC : Simple Conditional Display supports only the equal operator.");
                }
            } else {
                Log.LOGGER.error(inputFile.getName() + ":" + ++inputFileRowNum + ":Relevance - OC : Simple Conditional Display works only with single-select data.");
            }
        } else {
            Log.LOGGER.error(inputFile.getName() + ":" + ++inputFileRowNum + ":Relevance - OC : Simple Conditional Display does not work inside repeating-group.");
        }
    }

    String findCorrespondingResponseLabel(String cellValue1, String cellValue2) {
        int row = inputFileReader.twoColumnsJunction(CHOICES_SHEET, CHOICES_LIST_NAME_CELL, cellValue1, CHOICES_VALUE_CELL, cellValue2);
        return inputFileReader.readCell(CHOICES_SHEET, row, CHOICES_LABEL_CELL);
    }

    void setRightItemText(String hint) {
        if (!hint.equals("null")) {
            outputFileWriter.appendNewCell(ITEMS_SHEET, ITEMS_RIGHT_ITEM_TEXT_CELL, hint);
        }
    }

    void setItemsSheet() {
        int i = getStartingRow();
        while (i <= maxSheetRow) {
            if (!inputFileReader.isEmptyRow(SURVEY_SHEET, i, SURVEY_TYPE_CELL)) {
                if (inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_TYPE_CELL).equals("begin group")
                        && (inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_APPEARANCE_CELL).equals("field-list"))) {
                    setSections(inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_NAME_CELL), inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_LABEL_CELL),
                            inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_HINT_CELL));
                    setGroups("defaultGroup", "NON_REPEATING");
                    String tmpSectionLabel = inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_NAME_CELL);
                    i++;
                    while (!inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_TYPE_CELL).equals("end group")) {
                        if (!GROUP_KW_LIST.contains(inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_TYPE_CELL))) {
                            setItem(inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_NAME_CELL), inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_LABEL_CELL),
                                    inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_TYPE_CELL),
                                    setRequiredField(inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_REQUIRED_CELL)),
                                    tmpSectionLabel, "defaultGroup", inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_RELEVANCE_CELL),
                                    inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_HINT_CELL), i, false);
                        } else {
                            break;
                        }
                        i++;
                    }
                }
                if (inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_TYPE_CELL).equals("begin group")) {
                    setSections("defaultSection", "Default Section", "");
                    setGroups(inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_NAME_CELL), "NON_REPEATING");
                    String tmpGroupLabel = inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_NAME_CELL);
                    i++;

                    while (!inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_TYPE_CELL).equals("end group")) {
                        if (!GROUP_KW_LIST.contains(inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_TYPE_CELL))) {
                            setItem(inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_NAME_CELL), inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_LABEL_CELL),
                                    inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_TYPE_CELL), setRequiredField(inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_REQUIRED_CELL)),
                                    "defaultSection", tmpGroupLabel, inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_RELEVANCE_CELL),
                                    inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_HINT_CELL), i, false);
                        } else {
                            break;
                        }
                        i++;
                    }
                }
                if (inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_TYPE_CELL).equals("begin repeat")) {
                    setGroups(inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_NAME_CELL), "GRID");
                    setSections("defaultSection", "Default Section", "");
                    String tmpGroupLabel = inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_NAME_CELL);
                    i++;
                    while (!inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_TYPE_CELL).equals("end repeat")) {
                        if (!GROUP_KW_LIST.contains(inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_TYPE_CELL))) {
                            setItem(inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_NAME_CELL), inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_LABEL_CELL),
                                    inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_TYPE_CELL), setRequiredField(inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_REQUIRED_CELL)),
                                    "defaultSection", tmpGroupLabel, inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_RELEVANCE_CELL),
                                    inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_HINT_CELL), i, true);
                        } else {
                            break;
                        }
                        i++;
                    }
                }
                if (!GROUP_KW_LIST.contains(inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_TYPE_CELL))) {
                    setGroups("defaultGroup", "NON_REPEATING");
                    setSections("defaultSection", "Default Section", "");
                    setItem(inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_NAME_CELL), inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_LABEL_CELL),
                            inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_TYPE_CELL), setRequiredField(inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_REQUIRED_CELL)),
                            "defaultSection", "defaultGroup", inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_RELEVANCE_CELL),
                            inputFileReader.readCell(SURVEY_SHEET, i, SURVEY_HINT_CELL), i, false);
                }
            }
            i++;
        }
    }
}
