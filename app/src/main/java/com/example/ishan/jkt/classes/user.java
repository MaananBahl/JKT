package com.example.ishan.jkt.classes;

public class user {

    public String name, email, pic, designation, role;

    public user(String name, String email, String pic){
        this.name = name;
        this.email = email;
        this.pic = pic;
    }

    public user(){  }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String getPic(){
        return pic;
    }

    public String getDesignation(){return designation; }

    public String getRole() {return role;  }

}
