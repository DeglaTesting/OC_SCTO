/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rdcit.tools;

/**
 *
 * @author sa841
 */
import java.io.File;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import static org.rdcit.tools.DataType.*;

public class SpreadsheetReader {

    private File file;
    private Workbook workbook;

    public SpreadsheetReader(File file) {
        this.file = file;
        open();
    }

    private void open() {
        try {
            workbook = WorkbookFactory.create(file);
        } catch (IOException | InvalidFormatException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public String readCell(int sheetNum, int rowNum, int cellNum) {
        String cellValue = "";
        Sheet sheet = workbook.getSheetAt(sheetNum);
        Row row = sheet.getRow(rowNum);
        try {
            Cell cell = row.getCell(cellNum);
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    cellValue = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    cellValue = String.valueOf(cell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    cellValue = String.valueOf(isInteger(cell.getNumericCellValue()));
                    break;
                case Cell.CELL_TYPE_BLANK:
                    cellValue = "";
                    break;
            }
            return cellValue;
        } catch (Exception ex) {
            return "null";
        }
    }

    public int getMaxLogicalRow(int sheetNum) {
        return workbook.getSheetAt(sheetNum).getLastRowNum();
    }

    public int twoColumnsJunction(int sheetNum, int cellNum1, String cellValue1, int cellNum2, String cellValue2) {
        int rowNumber = 0;
        try {
            Sheet sheet = workbook.getSheetAt(sheetNum);
            int stop = sheet.getLastRowNum();
            for (int i = 1; i <= stop; i++) {
                Row row = sheet.getRow(i);
                String sCellValue1 = getCellValueAsString(row.getCell(cellNum1));
                String sCellValue2 = getCellValueAsString(row.getCell(cellNum2));
                if ((sCellValue1.equals(cellValue1)) && (sCellValue2.equals(cellValue2))) {
                    rowNumber = i;
                    break;
                }
            }
        } catch (Exception ex) {
        }
        return rowNumber;
    }

    public int getRowNum(int sheetNum, int cellNum, String cellValue) {
        int rowNumber = 0;
        try {
            Sheet sheet = workbook.getSheetAt(sheetNum);
            int stop = sheet.getLastRowNum();
            for (int i = 1; i <= stop; i++) {
                Row row = sheet.getRow(i);
                if ((row.getCell(cellNum).getStringCellValue().equals(cellValue))) {
                    rowNumber = i;
                    break;
                }
            }
        } catch (Exception ex) {
        }
        return rowNumber;
    }

    public String getCellValueAsString(Cell cell) {
        String cellContent = "";
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    cellContent = String.valueOf(cell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    cellContent = String.valueOf(isInteger(cell.getNumericCellValue()));
                    break;
                case Cell.CELL_TYPE_STRING:
                    cellContent = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_BLANK:
                case Cell.CELL_TYPE_ERROR:
                case Cell.CELL_TYPE_FORMULA:
                    break;
            }
        }
        return cellContent;
    }

    // Return true if the principalCell (in the sheet(sheetNum) in the row(rowNum)) contains a valid value
    public boolean isEmptyRow(int sheetNum, int rowNum, int principalCell) {
        return  (readCell(sheetNum, rowNum, principalCell).equals("null") || (readCell(sheetNum, rowNum, principalCell).equals(""))); 
    }

    public void close() {
        try {
            workbook.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
