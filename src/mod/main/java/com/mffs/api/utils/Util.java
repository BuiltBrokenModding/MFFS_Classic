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
     *
     * @param s
     * @param count
     * @return
     */
    public static List<String> sepString(String s, int count) {
        List<String> list = new ArrayList<>();
        Pattern regex = Pattern.compile(".{1," + count + "}(?:\\s|$)", Pattern.DOTALL);
        Matcher regexMatcher = regex.matcher(s);
        while (regexMatcher.find()) {
            list.add(regexMatcher.group());
        }
        return list;
    }

    /**
     * @author Calclavia
     */
    public static List<String> splitStringPerWord(String string, int wordsPerLine) {
        String[] words = string.split(" ");
        List<String> lines = new ArrayList();
        for (int lineCount = 0; lineCount < Math.ceil(words.length / wordsPerLine); lineCount++) {
            String stringInLine = "";
            for (int i = lineCount * wordsPerLine; i < Math.min(wordsPerLine + lineCount * wordsPerLine, words.length); i++) {
                stringInLine = stringInLine + words[i] + " ";
            }
            lines.add(stringInLine.trim());
        }
        return lines;
    }
}
