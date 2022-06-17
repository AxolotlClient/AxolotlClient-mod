package io.github.axolotlclient.modules.hypixel;

/**
 * Port of
 * https://github.com/Plancke/hypixel-php/blob/c6d346c0366180bcd0a3f1b769f08500a31d283f/src/util/games/skywars/ExpCalculator.php
 * to Java from PHP
 * License: MIT
 */

public class ExpCalculator {

    private static final int[] EASY_LEVEL_EXP = {
            0, // Level 1
            20, //20
            50, //70
            80, //150
            100,//250
            250,//500
            500,//1000
            1000,//2000
            1500,//3500
            2500,//6000
            4000,//10000
            5000//15000
    };
    private static final int EXP_PER_LEVEL = 10000;

    public static float getProgressCurrentLevel(int exp) {
        float level = getLevelForExp(exp);
        int levelExp = getTotalExpForLevel(level);
        return exp - levelExp;
    }

    public static float getLevelForExp(int exp) {
        int easyLevelsCount = EASY_LEVEL_EXP.length;

        int easyLevelExp = 0;
        for (int i = 1; i <= easyLevelsCount; i++) {
            int expPerLevel = getExpForLevel(i);
            easyLevelExp += expPerLevel;
            if (exp < easyLevelExp) {
                return i - 1;//57965
            }
        }
        int extraLevels = (exp - easyLevelExp) / EXP_PER_LEVEL;
        return easyLevelsCount + extraLevels;
    }

    public static int getExpForLevel(int level) {
        if (level <= EASY_LEVEL_EXP.length) {
            return EASY_LEVEL_EXP[level - 1];
        }

        return EXP_PER_LEVEL;
    }

    public static int getTotalExpForLevel(float level) {
        int easyLevelsCount = EASY_LEVEL_EXP.length;

        int totalExp = 0;
        float easyLevels = Math.min(level, easyLevelsCount);
        for (int i = 0; i < easyLevels; i++) {
            totalExp += EASY_LEVEL_EXP[i];
        }

        if (level > easyLevelsCount) {
            float extraLevels = level - easyLevelsCount;
            totalExp += (extraLevels * EXP_PER_LEVEL);
        }
        return totalExp;
    }
}
