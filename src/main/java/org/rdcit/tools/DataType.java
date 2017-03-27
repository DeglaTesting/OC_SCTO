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
public class DataType {

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isReal(String s) {
        try {
            Float.parseFloat(s);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static String isInteger(Number doubleNum) {
        boolean is = false;
        String sConvertedValue = "";
        String sOriginValue = "";
        if (doubleNum instanceof Double) {
            Double dOriginNum = doubleNum.doubleValue();
            sOriginValue = String.valueOf(dOriginNum);
            Integer dConvertedNum = dOriginNum.intValue();
            sConvertedValue = String.valueOf(dConvertedNum);
            if (Double.parseDouble(sOriginValue) == Double.parseDouble(sConvertedValue)) {
                is = true;
            }
        }
        if (is) {
            return sConvertedValue;
        } else {
            return sOriginValue;
        }
    }
}
