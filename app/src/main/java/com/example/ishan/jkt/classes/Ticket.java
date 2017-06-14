package com.example.ishan.jkt.classes;

public class Ticket {
    String heading, priority, status, create_date_time, message, department, sender, userid, update_date_time;
    public String key;

    public Ticket(String heading,String priority, String status,String create_date_time,String message, String department, String sender, String userid, String update_date_time){
        this.heading = heading;
        this.department = department;
        this.priority = priority ;
        this.status = status;
        this.create_date_time = create_date_time;
        this.message = message;
        this.sender = sender;
        this.userid = userid;
        this.update_date_time = update_date_time;
    }

    public Ticket(){

    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreate_date_time() {
        return create_date_time;
    }

    public void setCreate_date_time(String create_date_time) {
        this.create_date_time = create_date_time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUpdate_date_time() {
        return update_date_time;
    }

    public void setUpdate_date_time(String update_date_time) {
        this.update_date_time = update_date_time;
    }
}
