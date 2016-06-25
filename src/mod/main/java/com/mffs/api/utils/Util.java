package com.mffs.api.utils;

import codechicken.lib.vec.Vector3;

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

    /**
     * Gets the distance between 2 vectors.
     *
     * @param vec1
     * @param vec2
     * @return
     */
    public static int getDist(Vector3 vec1, Vector3 vec2) {
        double x = Math.pow(-vec1.x + vec2.x, 2);
        double y = Math.pow(-vec1.y + vec2.y, 2);
        double z = Math.pow(-vec1.z + vec2.z, 2);
        return (int) Math.floor(Math.sqrt(x + y + z));
    }
}
