/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rdcit.tools;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author sa841
 */
public class Statics {

    public static final String workingRepository;
    static{
    workingRepository = Statics.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    //workingRepository = Statics.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("Statics.class", "");
    }
    public static final File OC_DESIGN_TEMPLATE = new File(workingRepository+"OC_Design_Template.xls");
    public static final File SCTO_DESIGN_TEMPLATE = new File(workingRepository+"SCTO_Design_Template.xlsx");

    public static final int SURVEY_SHEET = 0;
    public static final int SURVEY_TYPE_CELL = 0;
    public static final int SURVEY_NAME_CELL = 1;
    public static final int SURVEY_LABEL_CELL = 2;
    public static final int SURVEY_HINT_CELL = 3;
    public static final int SURVEY_APPEARANCE_CELL = 5;
    public static final int SURVEY_CONSTRAINT_CELL = 6;
    public static final int SURVEY_RELEVANCE_CELL = 8;
    public static final int SURVEY_REQUIRED_CELL = 10;
    public static final int SURVEY_READ_ONLY_CELL = 12;
    public static final int SURVEY_CALCULATION_CELL = 13;

    public static final int CHOICES_SHEET = 1;
    public static final int CHOICES_LIST_NAME_CELL = 0;
    public static final int CHOICES_VALUE_CELL = 1;
    public static final int CHOICES_LABEL_CELL = 2;

    public static final int SETTINGS_SHEET = 2;
    public static final int SETTINGS_FORM_TITLE_CELL = 0;
    public static final int SETTINGS_FORM_ID_CELL = 1;
   // public static final int SETTINGS_FORM_VERSION = 2;

    public static final int CRF_SHEET = 0;
    public static final int CRF_CRF_NAME_CELL = 0;
    public static final int CRF_CRF_VERSION_CELL = 1;
    public static final int CRF_VERSION_DESCRIPTION_CELL = 2;
    public static final int CRF_REVISION_NOTES_CELL = 3;

    public static final int SECTIONS_SHEET = 1;
    public static final int SECTIONS_SECTION_LABEL_CELL = 0;
    public static final int SECTIONS_SECTION_TITLE_CELL = 1;
    public static final int SECTIONS_INSTRUCTIONS_CELL = 3;

    public static final int ITEMS_SHEET = 3;
    public static final int ITEMS_ITEM_NAME_CELL = 0;
    public static final int ITEMS_DESCRIPTION_LABEL_CELL = 1;
    public static final int ITEMS_LEFT_ITEM_TEXT_CELL = 2;
    public static final int ITEMS_RIGHT_ITEM_TEXT_CELL = 4;
    public static final int ITEMS_SECTION_LABEL_CELL = 5;
    public static final int ITEMS_GROUP_LABEL_CELL = 6;
    public static final int ITEMS_QUESTION_NUMBER_CELL = 12;
    public static final int ITEMS_RESPONSE_TYPE_CELL = 13;
    public static final int ITEMS_RESPONSE_LABEL_CELL = 14;
    public static final int ITEMS_RESPONSE_OPTIONS_TEXT_CELL = 15;
    public static final int ITEMS_RESPONSE_VALUE_OR_CALCULATIONS_CELL = 16;
    public static final int ITEMS_DATA_TYPE_CELL = 19;
    public static final int ITEMS_REQUIRED_CELL = 24;
    public static final int ITEMS_DISPLAY_STATUS_CELL = 25;
    public static final int ITEMS_SIMPLE_CONDITION_CELL = 26;
    
    public static final int GROUPS_SHEET = 2;
    public static final int GROUPS_LABEL_CELL = 0;
    public static final int GROUPS_GROUP_LAYOUT_CELL = 1;
    public static final int GROUP_GROUP_HEADER_CELL = 2;
    
    public static final List<String> GROUP_KW_LIST = Arrays.asList("begin repeat", "begin group", "end repeat", "end group");

    public static final String SUM_PATTERN_MATCHER = "\\$\\{\\w+\\}[\\+\\$\\{\\w+\\}]+";

}
