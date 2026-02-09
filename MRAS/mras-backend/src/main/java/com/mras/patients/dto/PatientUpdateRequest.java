package com.mras.patients.dto;

import java.time.LocalDate;

import com.mras.patients.model.Address;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public class PatientUpdateRequest {

    @Size(min = 2, max = 120)
    private String name;

    private LocalDate dob;

    @Pattern(regexp = "^(M|F|O|U)$", message = "gender must be one of M/F/O/U")
    private String gender;

    @Size(min = 6, max = 20)
    @Pattern(regexp = "^[0-9+\\- ]{6,20}$", message = "phone must be valid")
    private String phone;

    @Valid
    private Address address;

    private String linkedUserId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public String getLinkedUserId() { return linkedUserId; }
    public void setLinkedUserId(String linkedUserId) { this.linkedUserId = linkedUserId; }
}
