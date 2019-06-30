package com.test.model;

public class LearnItem {

    protected String traditional;
    protected String simplified;
    protected int learnTimes;

    public LearnItem(String traditional, String simplified, int learnTimes) {
        this.traditional = traditional;
        this.simplified = simplified;
        this.learnTimes = learnTimes;
    }

    public String getTraditional() {
        return traditional;
    }

    public String getSimplified() {
        return simplified;
    }

    public int getLearnTimes() {
        return learnTimes;
    }

    public void setLearnTimes(int learnTimes) {
        this.learnTimes = learnTimes;
    }

}
