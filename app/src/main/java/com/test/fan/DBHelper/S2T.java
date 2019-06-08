package com.test.fan.DBHelper;

public class S2T {

    private int id;
    private String s;
    private String ts;
    private String rt;

    public S2T(int i,String a,String b,String c)
    {
        this.id=i;
        this.s=a;
        this.ts=b;
        this.rt=c;
    }
    public void setId(int i)
    {
        this.id=i;
    }
    public void setS(String a)
    {
        this.s=a;
    }
    public void setTs(String b)
    {
        this.ts=b;
    }
    public void setRt(String c)
    {
        this.rt=c;
    }

    public int getId()
    {
        return id;
    }
    public String getS()
    {
        return s;
    }
    public String getTs()
    {
        return ts;
    }
    public String getRt()
    {
        return rt;
    }
}
