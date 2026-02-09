package com.mras.records.model;

public class Prescription {
    private String drug;
    private String dose;
    private String frequency;
    private Integer days;

    public String getDrug() { return drug; }
    public void setDrug(String drug) { this.drug = drug; }

    public String getDose() { return dose; }
    public void setDose(String dose) { this.dose = dose; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public Integer getDays() { return days; }
    public void setDays(Integer days) { this.days = days; }
}
