package com.mffs.api.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pwaln on 5/31/2016.
 */
public class Util {

    /**
     * Gets a string and seperates it based on the count parameter.
     * @param s
     * @param count
     * @return
     */
    public static List<String> sepString(String s, int count) {
        List<String> list = new ArrayList<>();
        Pattern regex = Pattern.compile(".{1,"+count+"}(?:\\s|$)", Pattern.DOTALL);
        Matcher regexMatcher = regex.matcher(s);
        while (regexMatcher.find()) {
            list.add(regexMatcher.group());
        }
        return list;
    }
}
