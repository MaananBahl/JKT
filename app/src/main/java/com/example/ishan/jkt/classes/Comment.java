package com.example.ishan.jkt.classes;


public class Comment {
    public String sender, senderuid, comment, date;

    public Comment(String s, String senderuid, String c, String d){
        this.sender=s;
        this.comment=c;
        this.date=d;
        this.senderuid=senderuid;
    }

    public Comment(){ }

    public String getSender() { return sender; }

    public String getDate() { return date; }

    public String getComment() { return comment; }

    public String getSenderuid() {
        return senderuid;
    }
}
