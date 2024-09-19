package com;

import java.io.BufferedReader;
import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class FetchData {
    public static void DisplayNodeData() {
        APIRequest fetchData = new APIRequest();
        
        try {
            // Read API Key from text file
            BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\lekgo\\OneDrive\\Documents\\Groundwork\\GroundWork\\src\\main\\resources\\api_key.txt"));
            String apiKey = reader.readLine().trim();
            reader.close();

            // First endpoint: Get node list
            String nodeUrl = "https://prod.omly.co/rest/v3/reports/" + apiKey + "/gw/node/list";
            JSONArray nodesArray = fetchData.getApiResponseAsArray(nodeUrl);

            // If nodes are available, use the second endpoint to fetch data for the first node
            if (nodesArray != null && !nodesArray.isEmpty()) {
                // Get the first node ID (you can loop through nodes if needed
                for (Object node : nodesArray) {
                    // Second endpoint: Get data for the first node
                    String nodeId = node.toString();
                    String fromDateTime = "2023-01-01T00:00:00Z";
                    String toDateTime = "2023-12-31T23:59:59Z";  
                    String dataUrl = "https://prod.omly.co/rest/v3/reports/" + apiKey + "/gw/" + nodeId + "/export?from=" + fromDateTime + "&to=" + toDateTime;

                    JSONObject nodeData = fetchData.getApiResponseAsObject(dataUrl);

                    // Process node data
                    System.out.println("Node Data for ID " + nodeId + ": " + nodeData);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
