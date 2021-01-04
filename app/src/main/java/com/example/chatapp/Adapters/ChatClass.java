package com.example.chatapp.Adapters;

public class ChatClass {

    private String Sender,Receiver,Message,Time;

    public ChatClass(String Receiver, String Sender, String Message,String Time) {
        this.Sender = Sender;
        this.Receiver = Receiver;
        this.Message = Message;
        this.Time=Time;
    }

    public ChatClass() {
    }


    public String getSender() {
        return Sender;
    }


    public String getReceiver() { return Receiver;}


    public String getMessage() { return Message; }

    public String getTime() {
        return Time;
    }
}


