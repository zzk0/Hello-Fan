package com.test.Bean;

public class WordBean {

    private int id;
    private String simplified;
    private String traditional;

    public WordBean( String simplified, String traditional) {
        this.simplified = simplified;
        this.traditional = traditional;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSimplified() {
        return simplified;
    }

    public void setSimplified(String simplified) {
        this.simplified = simplified;
    }

    public String getTraditional() {
        return traditional;
    }

    public void setTraditional(String traditional) {
        this.traditional = traditional;
    }
}
