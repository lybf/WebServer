package org.lybf.http.utils;

import java.text.DecimalFormat;

public class Progress {
    private String suffix;
    private String prefix;

    private void reset() {
        System.out.print("\r");
    }

    private void finish() {
        System.out.print("\n");
    }

    private int barLen;

    private char showChar;

    private final DecimalFormat formater = new DecimalFormat("#.##%");

    public Progress(int barLen, char showChar) {
        this.barLen = barLen;
        this.showChar = showChar;
    }

    public void show(int value) {
        if (value < 0 || value > 100) {
            return;
        }
        reset();
        // 比例
        float rate = (float) (value * 1.0 / 100);
        // 比例*进度条总长度=当前长度
        draw(barLen, rate);
        if (value == 100L) {
            finish();
        }
    }

    private void draw(int barLen, float rate) {
        int len = (int) (rate * barLen);
        System.out.print(prefix != null ? prefix : "Progress: ");
        for (int i = 0; i < len; i++) {
            System.out.print(showChar);
        }
        for (int i = 0; i < barLen - len; i++) {
            System.out.print(" ");
        }
        System.out.print(" |" + format(rate) + suffix != null ? suffix : "");
    }


    public Progress setSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public Progress setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    private String format(float num) {
        return formater.format(num);
    }


}
