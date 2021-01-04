package com.example.chatapp.Adapters;


public class UserClass {
    private String UserId,PhoneNo,ProfileImage,Status;

    public UserClass() {
    }


    public UserClass(String UserId,String PhoneNo,String ProfileImage,String Status){
        this.UserId= UserId;
        this.PhoneNo = PhoneNo;
        this.ProfileImage = ProfileImage;
        this.Status=Status;
    }

    public String getUserId() {
        return UserId;
    }


    public String getProfileImage() {
        return ProfileImage;
    }

    public String getStatus() { return Status; }

    public String getPhoneNo() {
        return PhoneNo;
    }
}
