package com.bank.skminibank.model;

public class SuccessStory {
    private String name;
    private String role;
    private double ctc; // in LPA
    private String photoUrl;

    public SuccessStory(String name, String role, double ctc, String photoUrl) {
        this.name = name;
        this.role = role;
        this.ctc = ctc;
        this.photoUrl = photoUrl;
    }

    public String getName() { return name; }
    public String getRole() { return role; }
    public double getCtc() { return ctc; }
    public String getPhotoUrl() { return photoUrl; }
}
