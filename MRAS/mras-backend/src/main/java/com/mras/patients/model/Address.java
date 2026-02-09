package com.mras.patients.model;

import jakarta.validation.constraints.Size;

public class Address {

    @Size(max = 200)
    private String line1;

    @Size(max = 80)
    private String city;

    @Size(max = 80)
    private String state;

    @Size(max = 12)
    private String pincode;

    public String getLine1() { return line1; }
    public void setLine1(String line1) { this.line1 = line1; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
}
