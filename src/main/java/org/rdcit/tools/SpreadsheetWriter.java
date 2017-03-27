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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import static org.rdcit.tools.DataType.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author sa841
 */
public class SpreadsheetWriter {

    private File file;
    private FileInputStream fis;
    private Workbook workbook;

    public SpreadsheetWriter(File file) {
        this.file = file;
        open();
    }

    private void open() {
        try {
            fis = new FileInputStream(file);
            workbook = WorkbookFactory.create(fis);
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  " + fis.toString());
        } catch (IOException | InvalidFormatException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void appendNewRow(int sheetNum) {
        Sheet sheet = workbook.getSheetAt(sheetNum);
        sheet.createRow(sheet.getLastRowNum() + 1);
    }

    public void createNewRow(int sheetNum, int rowNum) {
        Sheet sheet = workbook.getSheetAt(sheetNum);
        if (rowNum <= sheet.getLastRowNum()) {
            sheet.shiftRows(rowNum, sheet.getLastRowNum(), 1);
            sheet.createRow(rowNum);
        } else {
            sheet.createRow(sheet.getLastRowNum() + 1);
        }
    }

    public int getLastRowNum(int sheetNum) {
        return workbook.getSheetAt(sheetNum).getLastRowNum();
    }

    // Returns the num of the last row containing the given label, 0 if not found
    public int fingLastLabelOcc(int sheetNum, int labelCellNum, String label) {
        int rowNumber = 0;
        try {
            Sheet sheet = workbook.getSheetAt(sheetNum);
            int stop = sheet.getLastRowNum();
            for (int i = stop; i != 1; i--) {
                Row row = sheet.getRow(i);
                if ((row.getCell(labelCellNum).getStringCellValue().equals(label))) {
                    rowNumber = i;
                    break;
                }
            }
        } catch (Exception ex) {
        }
        return rowNumber;
    }

    public void appendNewCell(int sheetNum, int cellNum, String cellContent) {
        Sheet sheet = workbook.getSheetAt(sheetNum);
        Row row = sheet.getRow(sheet.getLastRowNum());
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(cellContent);
    }

    public void writeNewCell(int sheetNum, int rowNum, int cellNum, String cellContent) {
        Sheet sheet = workbook.getSheetAt(sheetNum);
        Row row = sheet.getRow(rowNum);
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(cellContent);
    }

    public void updateCell(int sheetNum, int rowNum, int cellNum, String newValue) {
        workbook.getSheetAt(sheetNum).getRow(rowNum).getCell(cellNum).setCellValue(newValue);
    }

    public int ifCellValueExist(int sheetNum, int cellNum, String cellValue) {
        int rowNumber = 0;
        try {
            Sheet sheet = workbook.getSheetAt(sheetNum);
            int stop = getLastRowNum(sheetNum);
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

    public void close(File outputFile) {
        try {
            FileOutputStream fos = new FileOutputStream(outputFile);
            workbook.write(fos);
            fos.close();
            System.out.println("Done");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
