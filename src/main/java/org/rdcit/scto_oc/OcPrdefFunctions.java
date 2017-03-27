/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rdcit.scto_oc;

import static org.rdcit.tools.Statics.SUM_PATTERN_MATCHER;
import static org.rdcit.tools.DataType.isInteger;
import static org.rdcit.tools.Strings.*;
import org.rdcit.tools.Log;

/**
 *
 * @author sa841
 */
public class OcPrdefFunctions {
    
    static String decode(String initFunction, int readderRowNum) {
        try {
            String params[] = initFunction.split("if");
            String fieldName = extractFromPattern(params[1], "${", "}");
            System.out.println("filed name " + fieldName);
            String finalFunc = "func:decode(" + fieldName + ",";
            for (int i = 1; i < params.length; i++) {
                String[] tmp = params[i].split(",");
                finalFunc = finalFunc + format(tmp[1]) + "," + format(tmp[2]) + ",";
            }
            finalFunc = replaceLast(finalFunc, ",", "") + ")";
            System.out.println("Final Func = " + finalFunc);
            return finalFunc;
        } catch (Exception ex) {
            Log.LOGGER.debug("           " + ++readderRowNum + ":Decode function - SCTO : Badly formatted expression.");
            return "";
        }
    }
    
    static String range(String initFunction, int readderRowNum) {
        String newFunc = "func: range(";
        String[] params = initFunction.split("and");
        if (params.length == 2) {
            newFunc = newFunc + format(params[0]) + "," + format(params[1]) + ")";
        } else {
            Log.LOGGER.debug("           " + ++readderRowNum + ":Range function - SCTO : Should follow this format; \".>=min and .<=max\"");
        }
        return newFunc;
    }
    
    static String max(String initFunction, int readderRow) {
        //initFunction = "max(${f1},${f2})";
        String[] params = initFunction.split(",");
        String[] newParams = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            newParams[i] = extractFromPattern(params[i], "${", "}");
        }
        String finalFunc = "func:max(";
        for (String newParam : newParams) {
            finalFunc = finalFunc + newParam + ",";
        }
        finalFunc = replaceLast(finalFunc, ",", "").concat(")");
        return finalFunc;
    }
    
    static String min(String initFunction, int readderRow) {
        //initFunction = "max(${f1},${f2})";
        String[] params = initFunction.split(",");
        String[] newParams = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            newParams[i] = extractFromPattern(params[i], "${", "}");
        }
        String finalFunc = "func:min(";
        for (String newParam : newParams) {
            finalFunc = finalFunc + newParam + ",";
        }
        finalFunc = replaceLast(finalFunc, ",", "").concat(")");
        return finalFunc;
    }
    
    static boolean ifSum(String initFunction, int readderRow) {
        return patternTester(SUM_PATTERN_MATCHER, initFunction);
    }
    
    static String sum(String initFunction, int readderRow) {
        //initFunction = ${f1} + ${f2}; //finalFunc = "sum(f1,f2,f3)"
        String[] params = initFunction.split("\\+");
        String[] newParams = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            newParams[i] = extractFromPattern(params[i], "${", "}");
        }
        String finalFunc = "func:sum(";
        for (String newParam : newParams) {
            finalFunc = finalFunc + newParam + ",";
        }
        finalFunc = replaceLast(finalFunc, ",", "").concat(")");
        System.out.println("finalFunc = " + finalFunc);
        return finalFunc;
    }
    
    static boolean ifAvg(String initFunction, int readderRow) {
        boolean avg = false;
        String[] params = initFunction.split("div");
        if (params.length == 2) {
            String tmp = replaceLast(params[0].replaceFirst("\\(", ""), ")", "");
            if (ifSum(tmp, readderRow)) {
                int count = tmp.split("\\+").length;
                String fParam = format(params[1]);
                if (isInteger(fParam)) {
                    if (count == Integer.valueOf(fParam)) {
                        avg = true;
                    }
                }
            }
        }
        return avg;
    }
    
    static boolean ifAvgRG(String initFunction, int readderRow) {
        boolean avg = false;
        String[] params = initFunction.split("div");
        if (params.length == 2) {
            if ((params[0].replaceAll("\\s", "").startsWith("sum")) && (params[1].replaceAll("\\s", "").startsWith("count"))) {
                avg = true;
            }
        }
        return avg;
    }
    
    static String avg(String initFunction, int readderRow) {
        //initFunction = (${f1} + ${f2}) div  2; //finalFunc = "avg(f1,f2)"
        String[] params = initFunction.split("div")[0].split("\\+");
        String[] newParams = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            newParams[i] = extractFromPattern(params[i], "${", "}");
        }
        String finalFunc = "avg(";
        for (String newParam : newParams) {
            finalFunc = finalFunc + newParam + ",";
        }
        finalFunc = replaceLast(finalFunc, ",", "").concat(")");
        System.out.println("finalFunc = " + finalFunc);
        return finalFunc;
    }
    
    static String avgRG(String initFunction, int readderRow) {
        // initFunction = sum(${itemRG}) div count(${RG}) //output = avg(itemRG)
        String finalFunc = "avg(";
        String param = initFunction.split("div")[0].replace("sum", "");
        System.out.println("param = " + param);
        param = format(extractFromPattern(param, "${", "}"));
        finalFunc = finalFunc + param + ")";
        System.out.println("finalFunction = " + finalFunc);
        return finalFunc;
    }
    
    public static void main(String[] args) {
        boolean b = ifAvgRG("sum(${frg}) div count(${RG})", 0);
        System.out.println("b = " + b);
    }
    
}
