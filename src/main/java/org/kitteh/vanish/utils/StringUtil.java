package org.kitteh.vanish.utils;

public class StringUtil {
    public static String capitalizeFirstLetter(String s) {
        char[] cap = s.toCharArray();
        boolean capitalizeNext = true;
        for (int i = 0; i < cap.length; i++) {
            char c = cap[i];
            if (c == '_') {
                c = ' ';
                capitalizeNext = true;
            } else if (c >= '0' && c <= '9' || c == '(' || c == ')') {
                capitalizeNext = true;
            } else {
                c = capitalizeNext ? Character.toUpperCase(c) : Character.toLowerCase(c);
                capitalizeNext = false;
            }
            cap[i] = c;
        }
        return new String(cap);
    }
}
