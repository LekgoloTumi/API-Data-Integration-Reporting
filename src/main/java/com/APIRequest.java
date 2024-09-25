package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class APIRequest {

    // Make API request and create a JSONArray for the nodes
    public static JSONArray getApiResponseAsArray(String urlString) throws Exception {
        HttpsURLConnection conn = null;
        int retryCount = 3;  // Number of retries in case of server errors (500)
        int attempts = 0;
        
        while (attempts < retryCount) {
            attempts++;
            try {
                URL url = new URL(urlString);
                conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    // Read the response
                    StringBuilder response;
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                        String inputLine;
                        response = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                    }

                    // Parse response into a JSONArray
                    JSONParser parser = new JSONParser();
                    JSONObject jsonResponse = (JSONObject) parser.parse(response.toString());

                    // Extract the "nodes" array
                    return (JSONArray) jsonResponse.get("nodes");

                } else if (responseCode == 500) {
                    System.out.println("Server returned 500 error. Retrying... (Attempt " + attempts + ")");
                    Thread.sleep(2000);  // Wait 2 seconds before retrying
                } else {
                    throw new IOException("Error: Received HTTP response code " + responseCode + " from the server.");
                }

            } catch (MalformedURLException e) {
                throw new MalformedURLException("Error: The URL provided is malformed. Please check the URL: " + urlString);
            } catch (IOException e) {
                if (attempts >= retryCount) {
                    throw new IOException("Error: Problem in connecting to the API or reading the response after " + attempts + " attempts. Details: " + e.getMessage());
                }
            } catch (ParseException e) {
                throw new ParseException(ParseException.ERROR_UNEXPECTED_EXCEPTION, "Error: Failed to parse the JSON response. Ensure the format is correct.");
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
        throw new IOException("Failed to retrieve valid data from the API after " + retryCount + " attempts.");
    }

    // Make an API request and return a JSON Object
    public static JSONObject getApiResponseAsObject(String urlString) throws Exception {
        HttpsURLConnection conn = null;
        int retryCount = 3;  // Number of retries in case of server errors (500)
        int attempts = 0;

        while (attempts < retryCount) {
            attempts++;
            try {
                URL url = new URL(urlString);
                conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    // Read the response
                    StringBuilder response;
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                        String inputLine;
                        response = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                    }

                    // Parse response into a JSONObject
                    JSONParser parser = new JSONParser();
                    return (JSONObject) parser.parse(response.toString());

                } else if (responseCode == 500) {
                    System.out.println("Server returned 500 error. Retrying... (Attempt " + attempts + ")");
                    Thread.sleep(2000);  // Wait 2 seconds before retrying
                } else {
                    throw new IOException("Error: Received HTTP response code " + responseCode + " from the server.");
                }

            } catch (MalformedURLException e) {
                throw new MalformedURLException("Error: The URL provided is malformed. Please check the URL: " + urlString);
            } catch (IOException e) {
                if (attempts >= retryCount) {
                    throw new IOException("Error: Problem in connecting to the API or reading the response after " + attempts + " attempts. Details: " + e.getMessage());
                }
            } catch (ParseException e) {
                throw new ParseException(ParseException.ERROR_UNEXPECTED_EXCEPTION, "Error: Failed to parse the JSON response. Ensure the format is correct.");
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
        throw new IOException("Failed to retrieve valid data from the API after " + retryCount + " attempts.");
    }
}
