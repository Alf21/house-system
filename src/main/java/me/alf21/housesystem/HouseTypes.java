package me.alf21.housesystem;

/**
 * Created by marvin on 15.05.15 in project weapon_system.
 * Copyright (c) 2015 Marvin Haschker. All rights reserved.
 */
public enum HouseTypes {
    NORMALHOUSE("NormalHouse");

    private String displayName;

    HouseTypes(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
