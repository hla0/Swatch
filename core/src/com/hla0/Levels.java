package com.hla0;

import com.hla0.util.Constants;

import java.util.Collection;
import java.util.regex.Matcher;

public class Levels {

    //TODO add different moves for each level
    public static int getNumberMoves(int level) {
        if (level == 0) {
            return -1;
        }
        if (level < 10)
            return 30;
        return 20;
    }

    //TODO add different objectives for each level
    public static int[] getColorObjectives(int level) {
        int[] colorObjectives = new int[Constants.NUMBER_COLORS];
        if (level == 0) {
            return colorObjectives;
        }
        if (level < 10) {
            for (int i = 0; i < Constants.NUMBER_COLORS; i++) {
                if (i > 1) {
                    colorObjectives[i] = 5;
                }
            }
        }
        else {
            for (int i = 0; i < Constants.NUMBER_COLORS; i++) {
                if (i > 1) {
                    colorObjectives[i] = 7;
                }
            }
        }
        return colorObjectives;
    }

    public static int[] getAnchorObjectives(int level) {
        //includes clear which counts all
        int[] anchorObjectives = new int[Constants.NUMBER_COLORS + 1];
        if (level == 0) {
            return anchorObjectives;
        }
        if (level < Constants.ANCHOR_LEVEL) {
            for (int i = 0; i < anchorObjectives.length; i++) {
                anchorObjectives[i] = 0;
            }
        }
        return anchorObjectives;
    }

    //based on moves from level
    //TODO decide how to reward stars
    public static String numStars(int score, int level) {
        int levelBoost = 0;
        if (level < 10) {
            levelBoost = 3000;
        }
        if (score + levelBoost > getNumberMoves(level) * 1100) {
            return "3";
        }
        if (score + levelBoost > getNumberMoves(level) * 800) {
            return "2";
        }
        else {
            return "1";
        }
    }

    //returns a random available color
    public static int availableColor(int level) {
        if (level > 10) {
            return (int)(Math.random() * 6) + 2;
        }
        return (int)(Math.random() * 8);
    }

    public static int availableType(int level) {
        if (level == 0) {
            if (Math.random() * 10 > 9) {
                //anchor type
                return 1;
            }
        }
        if (level > Constants.ANCHOR_LEVEL) {
            if (Math.random() * 10 > 9) {
                //anchor type
                return 1;
            }
        }
        return 0;
    }

    //TODO create different level maps
    //left is bottom
    public static int[][] getLevelMap(int level) {
        int[][] randomLevelMap = new int[Constants.GRID_SIZE][Constants.GRID_SIZE];
        switch (level) {
            case 1:
                int[][] levelMap1 =
                        {{0,0,0,0,0,0,0,0}
                        ,{0,0,0,0,0,0,0,0}
                        ,{0,0,0,0,0,0,0,0}
                        ,{0,0,0,0,0,0,0,0}
                        ,{0,0,0,0,0,0,0,0}
                        ,{0,0,0,0,0,0,0,0}
                        ,{0,0,0,0,0,0,0,0}
                        ,{0,0,0,0,0,0,0,0}};
                return levelMap1;
            case 2:
                int[][] levelMap2 =
                        {{0,0,0,0,0,0,0,0}
                        ,{0,0,0,0,0,0,0,0}
                        ,{0,0,0,0,0,0,0,0}
                        ,{0,0,0,0,0,0,0,0}
                        ,{0,0,0,0,0,0,0,0}
                        ,{0,0,0,0,0,0,0,0}
                        ,{0,0,0,0,0,0,0,0}
                        ,{0,0,0,0,0,0,0,0}};
                return levelMap2;
            case 4:
                int[][] levelMap4 =
                        {{1,0,0,0,0,0,0,1}
                        ,{0,1,0,0,0,0,1,0}
                        ,{0,0,1,0,0,1,0,0}
                        ,{0,0,0,1,1,0,0,0}
                        ,{0,0,0,1,1,0,0,0}
                        ,{0,0,1,0,0,1,0,0}
                        ,{0,1,0,0,0,0,1,0}
                        ,{1,0,0,0,0,0,0,1}};
                return levelMap4;
            case 5:
                int[][] levelMap5 =
                        {{0,0,1,0,0,1,0,0}
                        ,{0,0,1,0,0,1,0,0}
                        ,{1,1,1,1,1,1,1,1}
                        ,{0,0,1,0,0,1,0,0}
                        ,{0,0,1,0,0,1,0,0}
                        ,{1,1,1,1,1,1,1,1}
                        ,{0,0,1,0,0,1,0,0}
                        ,{0,0,1,0,0,1,0,0}};
                return levelMap5;
            case 9:
                int[][] levelMap9 =
                        {{1,1,1,1,1,1,1,1}
                        ,{1,1,1,1,1,1,1,1}
                        ,{1,1,0,0,0,0,1,1}
                        ,{1,1,0,0,0,0,1,1}
                        ,{1,1,0,0,0,0,1,1}
                        ,{1,1,0,0,0,0,1,1}
                        ,{1,1,1,1,1,1,1,1}
                        ,{1,1,1,1,1,1,1,1}};
                return levelMap9;
        }
        //temporary
        if (level >= 0) {
            //set levelMap up then generate
            for (int i = 0; i < Constants.GRID_SIZE; i++) {
                for (int j = 0; j < Constants.GRID_SIZE; j++) {
                    randomLevelMap[i][j] = (int) (Math.random() * 10) / 9;
                }
            }
        }

        return randomLevelMap;
    }


}
