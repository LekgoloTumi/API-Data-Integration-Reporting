package com;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

public class FetchData {
    
    public static final String PDF_DEST_STRING = "C:\\Users\\lekgo\\OneDrive\\Documents\\Groundwork\\GroundWork\\target\\TablePage.pdf";

    public static void main(String[] args) {
        try {
            String apiKey;
            try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\lekgo\\OneDrive\\Documents\\Groundwork\\GroundWork\\src\\main\\resources\\api_key.txt"))) {
                apiKey = reader.readLine().trim();
            }

            String nodeUrl = "https://prod.omly.co/rest/v3/reports/" + apiKey + "/gw/node/list";
            JSONArray nodesArray = APIRequest.getApiResponseAsArray(nodeUrl);

            if (nodesArray != null && !nodesArray.isEmpty()) {
                PdfWriter writer = new PdfWriter(PDF_DEST_STRING);
                PdfDocument pdfDocument = new PdfDocument(writer);

                try (Document document = new Document(pdfDocument)) {
                    // Create table with 2 columns: Node ID and Details
                    Table table = new Table(2);
                    table.addHeaderCell(new Cell().add(new Paragraph("Node ID")));
                    table.addHeaderCell(new Cell().add(new Paragraph("Details")));
                    ZonedDateTime currentDate = ZonedDateTime.now();

                    // Loop through nodes
                    for (Object node : nodesArray) {
                        String nodeId = node.toString();
                        String fromDateTime = "2022-01-01T00:00:00Z";
                        String toDateTime = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));  
                        String dataUrl = "https://prod.omly.co/rest/v3/reports/" + apiKey + "/gw/" + nodeId + "/export?from=" + fromDateTime + "&to=" + toDateTime;

                        // Initialize variables for storing node data
                        String status = "Offline";  // Default to Offline
                        String siteName = "N/A";
                        String location = "N/A";
                        String waterHead = "N/A";
                        String date = "N/A";
                        String coordinates = "N/A";

                        // Fetch node data and handle potential exceptions
                        try {
                            JSONObject nodeData = APIRequest.getApiResponseAsObject(dataUrl);
                            
                            // Extract values from nodeData if fetch was successful
                            siteName = getStringValue(nodeData, "site-name");
                            location = getStringValue(nodeData, "location");

                            // Extracting data from the result array
                            JSONArray resultArray = (JSONArray) nodeData.get("result");
                            if (resultArray != null && !resultArray.isEmpty()) {
                                JSONObject resultData = (JSONObject) resultArray.get(0); // Assuming you want the first result
                                waterHead = getStringValue(resultData, "water-head");
                                date = getStringValue(resultData, "date-time");
                                coordinates = getCoordinates(resultData);
                                status = "Online";  // Node is online if data is available
                            }
                        } catch (Exception e) {
                            // If an error occurs, keep the node marked as "Offline"
                            System.out.println("Error fetching data for Node ID: " + nodeId + ". Marking as Offline.");
                        }

                        // Add node ID to the first cell
                        table.addCell(new Cell().add(new Paragraph(nodeId)));

                        // Create a formatted string for the details and add it to the second cell
                        String details = "Status: " + status + "\n" +
                                         "Site Name: " + siteName + "\n" +
                                         "Location: " + location + "\n" +
                                         "Coordinates: " + coordinates + "\n" +
                                         "Last Reading Date: " + date + "\n" +
                                         "Water Head: " + waterHead;

                        table.addCell(new Cell().add(new Paragraph(details)));
                    }

                    // Add the table to the PDF document
                    document.add(table);
                }

                System.out.println("PDF with node data created successfully.");
            } else {
                System.out.println("No nodes available.");
            }

        } catch (IOException e) {
            System.out.println("File reading error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
}
