package com.hla0;

import com.hla0.util.Constants;

import java.util.Collection;

public class Levels {
    //TODO create different level maps
    public static int[][] getLevelMap(int level) {
        int[][] levelMap = new int[Constants.GRID_SIZE][Constants.GRID_SIZE];
        //temporary
        if (level > 0) {
            //set levelMap up then generate
            for (int i = 0; i < Constants.GRID_SIZE; i++) {
                for (int j = 0; j < Constants.GRID_SIZE; j++) {
                    levelMap[i][j] = (int) (Math.random() * 10) / 9;
                }
            }
        }
        return levelMap;
    }
    //TODO add different moves for each level
    public static int getNumberMoves(int level) {
        if (level < 10)
            return 30;
        return 20;
    }

    //TODO add different objectives for each level
    public static int[] getColorObjectives(int level) {
        int[] colorObjectives = new int[Constants.NUMBER_COLORS];
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


}
