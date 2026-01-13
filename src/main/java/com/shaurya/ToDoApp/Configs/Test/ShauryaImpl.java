package com.shaurya.ToDoApp.Configs.Test;

public class ShauryaImpl extends Sandeep implements ShauryaInterface{
    private String friendsname;
    public ShauryaImpl(String friendsname){
        this.friendsname=friendsname;
    }
    @Override
    public String sayHello() {
        return "Hello to "+friendsname;
    }

}
