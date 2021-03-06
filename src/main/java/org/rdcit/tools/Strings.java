/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rdcit.tools;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author sa841
 */
public class Strings {

    public static String replaceLast(String s, String reglex, String replacement) {
        int lastIndex = s.lastIndexOf(reglex);
        s = s.substring(0, lastIndex);
        s = s.concat(replacement);
        return s;
    }

    public static Object[] splitWithEscape(String s, String reglex, String escape) {
        String ar[] = s.split(reglex);
        ArrayList<String> arResult = new ArrayList<>();
        for (int i = 0; i < ar.length; i++) {
            String sTmp = ar[i];
            while (ar[i].endsWith(escape)) {
                sTmp = sTmp.replace(escape, "").concat("," + ar[i + 1].replace(escape, ""));
                i++;
            }
            arResult.add(sTmp);
        }

        return arResult.toArray();
    }

    public static String[] splitFirst(String s, String reglex) {
        String[] res = new String[2];
        int indexFirst = s.indexOf(reglex);
        res[0] = startTrim(s.substring(0, indexFirst));
        res[1] = startTrim(s.substring(indexFirst, s.length()).replaceFirst(reglex, ""));
        return res;
        }

    public static String replaceEscape(String s, String oldEscape, String newEscape) {
        if (s.contains(oldEscape)) {
            s = s.replaceAll(oldEscape, newEscape);
        }
        return s;
    }

    public static String endTrim(String s) {
        while (s.endsWith(" ")) {
            s = replaceLast(s, " ", "");
        }
        return s;
    }

    public static String startTrim(String s) {
        while (s.startsWith(" ")) {
            s = s.replaceFirst(" ", "");
}
        return s;
    }

    public static boolean notEmpty(String s) {
        return s.matches(".*\\w.*");
    }

    public static String extractFromPattern(String s, String beginPattern, String endPattern) {
        if (!s.startsWith(beginPattern)) {
            s = s.substring(s.indexOf(beginPattern), s.length());
        }
        s = s.substring(beginPattern.length(), s.length());
        if (!s.endsWith(endPattern)) {
            s = s.substring(0, s.indexOf(endPattern));
        } else {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    public static String putIntoPattern(String s, String beginPattern, String endPattern) {
        return beginPattern + s + endPattern;
    }
    
    public static boolean patternTester(String pattern, String s) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(s.replaceAll("\\s", ""));
        return m.matches();
    }

    public static String format(String s) {
        return s.replaceAll("[^\\w]", "");
    }

}
