package com.stivenva.contentsharingtest.domain.model;

public enum Category {

    GAME("game"),
    VIDEO("video") ,
    ARTWORK("artwork"),
    MUSIC("music");

    private String name;

    Category(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
