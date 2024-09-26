package com;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import io.github.cdimascio.dotenv.Dotenv;

public class SummarizeData {

    public static String PDF_DEST_STRING;

    public static void main(String[] args) {
        // Load environment variables
        Dotenv dotenv = Dotenv.load();

        String apiKey = dotenv.get("API_KEY");
        String mapsApiKey = dotenv.get("MAP_API_KEY");
        PDF_DEST_STRING = dotenv.get("DATA_SUMMARY_PAGE");

        // Validate essential environment variables
        if (apiKey == null || apiKey.isEmpty() || mapsApiKey == null || mapsApiKey.isEmpty()) {
            System.err.println("API keys are missing in the environment variables.");
            return;
        }

        // Fetch node URL from environment variable
        String nodeUrl = dotenv.get("NODE_URL").replace("${API_KEY}", apiKey);

        try {
            JSONArray nodesArray = APIRequest.getApiResponseAsArray(nodeUrl);

            if (nodesArray != null && !nodesArray.isEmpty()) {
                try (PdfWriter writer = new PdfWriter(PDF_DEST_STRING);
                     PdfDocument pdfDocument = new PdfDocument(writer);
                     Document document = new Document(pdfDocument)) {

                    // Create table with 2 columns: Node ID and Details
                    Table table = new Table(2);
                    table.addHeaderCell(new Cell().add(new Paragraph("Node ID")));
                    table.addHeaderCell(new Cell().add(new Paragraph("Details")));
                    ZonedDateTime currentDate = ZonedDateTime.now();

                    // For KMZ Path creation
                    List<String> coordinatesList = new ArrayList<>();
                    List<String> nodeIDs = new ArrayList<>();

                    // Loop through nodes
                    for (Object node : nodesArray) {
                        String nodeId = node.toString();
                        String fromDateTime = "2022-01-01T00:00:00Z";
                        String toDateTime = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));

                        // Construct the data URL using the template
                        String dataUrl = dotenv.get("DATA_URL")
                                .replace("${API_KEY}", apiKey)
                                .replace("${nodeId}", nodeId)
                                .replace("${fromDateTime}", fromDateTime)
                                .replace("${toDateTime}", toDateTime);

                        // Initialize variables for storing node data
                        String status = "Offline";
                        String siteName = "N/A";
                        String location = "N/A";
                        String waterHead = "N/A";
                        String porePressure = "N/A";
                        String airtemp = "N/A";
                        String date = "N/A";
                        String latitude = "N/A";
                        String longitude = "N/A";
                        String coordinates = "N/A";
                        List<Double> timeSeries = new ArrayList<>();
                        List<Double> waterHeadSeries = new ArrayList<>();
                        List<Double> porePressureSeries = new ArrayList<>();
                        List<Double> temperatureSeries = new ArrayList<>();

                        // Fetch node data and handle potential exceptions
                        try {
                            JSONObject nodeData = APIRequest.getApiResponseAsObject(dataUrl);

                            // Extract values from nodeData if fetch was successful
                            siteName = Getters.getStringValue(nodeData, "site-name");
                            location = Getters.getStringValue(nodeData, "location");

                            // Extracting data from the result array
                            JSONArray resultArray = (JSONArray) nodeData.get("result");
                            if (resultArray != null && !resultArray.isEmpty()) {
                                for (Object resultObj : resultArray) {
                                    JSONObject resultData = (JSONObject) resultObj;
                                    date = Getters.getStringValue(resultData, "date-time");
                                    coordinates = Getters.getCoordinates(resultData);
                                    latitude = Getters.getStringValue(resultData, "latitude");
                                    longitude = Getters.getStringValue(resultData, "longitude");
                                    status = "Online";

                                    // Extract readings
                                    waterHead = Getters.getStringValue(resultData, "water-head");
                                    porePressure = Getters.getStringValue(resultData, "pore-pressure");
                                    airtemp = Getters.getStringValue(resultData, "air-temp");

                                    // Collecting readings
                                    waterHeadSeries.add(waterHead.equals("N/A") ? Double.NaN : Double.parseDouble(waterHead));
                                    porePressureSeries.add(porePressure.equals("N/A") ? Double.NaN : Double.parseDouble(porePressure));
                                    temperatureSeries.add(airtemp.equals("N/A") ? Double.NaN : Double.parseDouble(airtemp));

                                    // Collecting time data for plotting
                                    timeSeries.add(Double.parseDouble(Getters.getHourFromDateTime(date)));
                                }

                                // Collecting coordinates for KMZ
                                coordinatesList.add(longitude + "," + latitude + ",0");

                                // Collect nodes for generation of pdfs
                                nodeIDs.add(nodeId);

                                VisualizeData.plotMapCoordinates(mapsApiKey, latitude, longitude, nodeId);
                            }
                        } catch (Exception e) {
                            System.out.println("Error fetching data for Node ID: " + nodeId + ". Mark this node as Offline.");
                        }

                        // Add node ID to the first cell
                        table.addCell(new Cell().add(new Paragraph(nodeId)));

                        // Create a formatted string for the details and add it to the second cell
                        StringBuilder details = new StringBuilder();
                        details.append("Status: ").append(status).append("\n")
                                .append("Site Name: ").append(siteName).append("\n")
                                .append("Location: ").append(location).append("\n")
                                .append("Coordinates: ").append(coordinates).append("\n")
                                .append("Last Reading Date: ").append(date).append("\n")
                                .append("Water Head: ").append(waterHead).append("\n");

                        table.addCell(new Cell().add(new Paragraph(details.toString())));

                        // Plotting the time series if data is available
                        if (!timeSeries.isEmpty()) {
                            if (!waterHeadSeries.isEmpty() && waterHeadSeries.stream().anyMatch(h -> !h.isNaN())) {
                                VisualizeData.plotWaterHeadAndTemperature(nodeId, timeSeries, waterHeadSeries, temperatureSeries);
                            } else if (!porePressureSeries.isEmpty() && porePressureSeries.stream().anyMatch(p -> !p.isNaN())) {
                                VisualizeData.plotPorePressureAndTemperature(nodeId, timeSeries, porePressureSeries, temperatureSeries);
                            }
                        }
                    }

                    // Add the table to the PDF document
                    document.add(table);

                    // Create KMZ file with coordinates path
                    GenerateFiles.createKMZFile(coordinatesList);

                    // Create PDFs for node data chart and node coordinates
                    GenerateFiles.generateTimeSeriesPDF(nodeIDs);
                    GenerateFiles.generateNodeCoordinatesPDF(nodeIDs);
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
}
