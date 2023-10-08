package com.pastebin.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShortURLValueGenerator {
    private static final char[] CHARS = "abcdefghijklmonpqrstuvwxyz".toCharArray();
    private static List<char[]> res;

    public static List<char[]> generate(String lastSequence, int amount) {
        res = new ArrayList<>();
        generateNextSequence(lastSequence, amount);

        return res;
    }

    private static void generateNextSequence(String lastSequence, int amount) {
        char[] lastSeq = lastSequence.toCharArray();

        for (int i = 0; i < amount; i++) {
            lastSeq = getNextSequence(Arrays.copyOf(lastSeq, lastSeq.length));
            res.add(lastSeq);
        }
    }

    private static char[] getNextSequence(char[] lastSeq) {
        int lastSeqLength = lastSeq.length - 1;

        while (lastSeqLength >= 0 && lastSeq[lastSeqLength] == CHARS[CHARS.length - 1]) {
            lastSeq[lastSeqLength] = CHARS[0];
            lastSeqLength--;
        }
        if (lastSeqLength == -1) {
            lastSeq = new char[lastSeq.length + 1];
            Arrays.fill(lastSeq, CHARS[0]);
        } else {
            for (int i = 0; i < CHARS.length; i++) {
                if (lastSeq[lastSeqLength] == CHARS[i]) {
                    lastSeq[lastSeqLength] = CHARS[i + 1];
                    break;
                }
            }
        }
        return lastSeq;
    }
}
