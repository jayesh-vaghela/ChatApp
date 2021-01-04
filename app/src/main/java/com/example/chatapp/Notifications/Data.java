package com.example.chatapp.Notifications;

public class Data {
    private String user,title,sented,body;
    private int icon;

    public Data() {
    }
    public Data(String user,int icon,String body,String title,String sented){
        this.user=user;
        this.icon=icon;
        this.body=body;
        this.title=title;
        this.sented=sented;
    }

    public String getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getSented() {
        return sented;
    }

    public String getBody() {
        return body;
    }

    public int getIcon() {
        return icon;
    }
}
