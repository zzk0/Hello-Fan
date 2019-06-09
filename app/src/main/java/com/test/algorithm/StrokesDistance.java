/*
笔画距离接口，评估两个笔画序列的相似程度。
目前可以尝试的算法有：编辑距离。
*/

package com.test.algorithm;

public interface StrokesDistance {
    float DistanceBetween(String a, String b);
}

/*
public static int levenshteinDistance(String a, String b) {
    int dp[][] = new int[a.length() + 1][b.length() + 1];
    dp[0][0] = 0;
    for (int i = 1; i <= a.length(); i++) {
        dp[i][0] = i;
    }
    for (int j = 1; j <= b.length(); j++) {
        dp[0][j] = j;
    }
    for (int i = 1; i <= a.length(); i++) {
        for (int j = 1; j <= b.length(); j++) {
            if (a.charAt(i - 1) == b.charAt(j - 1)) {
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1]);
            }
            else {
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + 1);
            }
        }
    }
    return dp[a.length()][b.length()];
}
*/