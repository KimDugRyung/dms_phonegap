package com.plugin.utils;


public enum Constants {
	SERVER_URL_PROPERTY("serverUrl"),
	REGPUSH_METHOD("registerPush"),
	UNREGPUSH_METHOD("unregisterPush"),
	SENDID_METHOD("sendDeviceId"),
	FILEPROPERTY_NAME("dms.property");

    private String name;

    private Constants(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Constants getConst(String value) {
        for(Constants appconst : values()) {
            if(appconst.getName().equalsIgnoreCase(value)) {
                return appconst;
            }
        }
        return null;
    }
}
