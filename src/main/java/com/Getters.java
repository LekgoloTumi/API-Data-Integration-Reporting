package com;

import org.json.simple.JSONObject;

public class Getters {
    // Helper method to safely get string values
    public static String getStringValue(JSONObject jsonObject, String key) {
        return (jsonObject.get(key) != null) ? jsonObject.get(key).toString() : "N/A";
    }

    // Helper method to get coordinates as a formatted string
    public static String getCoordinates(JSONObject resultData) {
        String latitude = getStringValue(resultData, "latitude");
        String longitude = getStringValue(resultData, "longitude");
        return latitude + ", " + longitude;
    }

    // Helper method to extract hour from date-time string
    public static String getHourFromDateTime(String dateTime) {
        // Assuming dateTime is in the format "2023-08-05T17:39:18Z"
        return dateTime.split("T")[1].split(":")[0]; // Extract hour as a string
    }
}
