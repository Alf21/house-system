package me.alf21.housesystem;

/**
 * Created by Alf21 on 20.05.2015 in project house-system.
 * Copyright (c) 2015 Alf21. All rights reserved.
 **/
public enum HouseTypes {
    HOUSE("House"),
    OBJECT("Object"); //TODO: Or the Housetypes eg. for the price like villa or ghetto

    private String displayName;

    HouseTypes(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
