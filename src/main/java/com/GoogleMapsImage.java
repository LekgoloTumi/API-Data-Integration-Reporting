package com;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;

public class GoogleMapsImage {
    public static void main(String[] args) {
        try {
            // Replace YOUR_API_KEY with your Google Maps Static API key
            String apiKey = "AIzaSyDPouFcH_pZcK5W-eTs5hbdol2XqVYrPH0"; // Load this from a secure location
            String latitude = "-26.07959";
            String longitude = "28.11827";
            String mapUrl = String.format(
                    "https://maps.googleapis.com/maps/api/staticmap?center=%s,%s&zoom=15&size=600x300&markers=color:red|%s,%s&key=%s",
                    latitude, longitude, latitude, longitude, apiKey
            );
            
            // Fetch the map image from the URL
            URL url = new URL(mapUrl);
            BufferedImage image = ImageIO.read(url);
            
            // Save the image to a file
            File outputFile = new File("sensor_map.png");
            ImageIO.write(image, "png", outputFile);
            
            System.out.println("Map image saved successfully at " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
