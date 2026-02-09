package com.mras.records.model;

public class Vitals {
    private String bp;
    private Double tempC;
    private Integer pulse;
    private Integer spo2;

    public String getBp() { return bp; }
    public void setBp(String bp) { this.bp = bp; }

    public Double getTempC() { return tempC; }
    public void setTempC(Double tempC) { this.tempC = tempC; }

    public Integer getPulse() { return pulse; }
    public void setPulse(Integer pulse) { this.pulse = pulse; }

    public Integer getSpo2() { return spo2; }
    public void setSpo2(Integer spo2) { this.spo2 = spo2; }
}
