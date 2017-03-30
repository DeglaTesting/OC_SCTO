/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rdcit.oc_scto;

import java.util.ArrayList;
import java.util.List;
import org.rdcit.tools.Log;
import static org.rdcit.tools.Strings.*;

/**
 *
 * @author sa841
 */
public class SctoPrdefFunctions {

    static String decode(String initFunction, int readderRowNum) {
        try {
            initFunction = initFunction.replace("decode(", "");
            initFunction = replaceLast(initFunction, ")", "");
            String[] params = initFunction.split(",");
            String field = params[0];
            List<String> lCalculation = new ArrayList<>();
            for (int i = 1; i < params.length - 1; i++) {
                String tmp = "if(selected(${" + field + "}," + params[i] + "), '" + params[i + 1] + "'";
                i = i + 1;
                lCalculation.add(tmp);
            }
            String tail = "''";
            for (String lCalculation1 : lCalculation) {
                tail = tail + ")";
            }
            String sCalculation = "";
            for (String s : lCalculation) {
                sCalculation = sCalculation + s + ",";
            }
            return sCalculation + tail;
        } catch (Exception ex) {
            Log.LOGGER.debug("           " + ++readderRowNum + ":Decode function - OC : Badly formatted expression.");
            return "";
        }
    }

    static String range(String initFunction, int readderRowNum) {
        String newFunc = "";
        initFunction = extractFromPattern(initFunction, "(", ")");
        String[] params = initFunction.split(",");
        if (params.length == 2) {
            newFunc = ".>=" + params[0] + " and .<=" + params[1];
        } else {
            Log.LOGGER.debug("           " + ++readderRowNum + ":Range function - OC : Should follow this format; \"func: range(min,max)\"");
        }
        return newFunc;
    }

    static String max(String initFunction, int readderRow) {
        //initFunction = "max(f1,f2,f3)";
        String tmp = initFunction.replace("max", "");
        String[] params = tmp.split(",");
        String[] newParams = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            newParams[i] = putIntoPattern(format(params[i]), "${", "}");
        }
        String finalFunc = "max(";
        for (String newParam : newParams) {
            finalFunc = finalFunc + newParam + ",";
        }
        finalFunc = replaceLast(finalFunc, ",", "").concat(")");
        return finalFunc;
    }

    static String min(String initFunction, int readderRow) {
        //initFunction = "max(f1,f2,f3)";
        String tmp = initFunction.replace("min", "");
        String[] params = tmp.split(",");
        String[] newParams = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            newParams[i] = putIntoPattern(format(params[i]), "${", "}");
        }
        String finalFunc = "min(";
        for (String newParam : newParams) {
            finalFunc = finalFunc + newParam + ",";
        }
        finalFunc = replaceLast(finalFunc, ",", "").concat(")");
        return finalFunc;
    }

    static String sum(String initFunction, int readderRow) {
        //initFunction = "sum(f1,f2,f3)"; //finalFunc = ${f1} + ${f2}
        String tmp = initFunction.replace("sum", "");
        String[] params = tmp.split(",");
        String[] newParams = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            newParams[i] = putIntoPattern(format(params[i]), "${", "}");
        }
        String finalFunc = "";
        for (String newParam : newParams) {
            finalFunc = finalFunc + newParam + "+";
        }
        finalFunc = replaceLast(finalFunc, "+", "");
        return finalFunc;
    }

    static String avg(String initFunction, int readderRow) {
        //initFunction = avg(f1,f2); //finalFunc = (${f1} + ${f2}) div  2
        String tmp = initFunction.replace("avg", "");
        String[] params = tmp.split(",");
        String[] newParams = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            newParams[i] = putIntoPattern(format(params[i]), "${", "}");
        }
        String finalFunc = "(";
        for (String newParam : newParams) {
            finalFunc = finalFunc + newParam + "+";
        }
        finalFunc = replaceLast(finalFunc, "+", ") div ") + newParams.length;
        return finalFunc;
    }

    static String avgRG(String initFunction, String repeatingGroupName, int readderRow) {
        // initFunction = avg(itemRG) //output = sum(${itemRG}) div count(${RG})
        String finalFunc;
        String rgField = format(initFunction.replace("avg", ""));
        finalFunc = "sum(${" + rgField + "}) div count (${" + repeatingGroupName + "})";
        return finalFunc;
    }

    static String reglex(String initFunction, int readderRow) {
        //input = regexp: /^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/ output= regex(.,'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$')
        String output = "regex(.,'";
        initFunction = extractFromPattern(initFunction, "/", "/");
        return output + initFunction + "')";
    }

    static String greaterThan(String initFunction, int readderRow) {
        //input gt(data) // output .>= ${data}
        return "";
    }

    public static void main(String[] args) {
        System.out.println("" + reglex("regexp: /^/[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}/$/", 0));
    }

}
