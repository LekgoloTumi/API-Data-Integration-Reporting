package com;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

public class FetchAndPlotData {

    public static final String PDF_DEST_STRING = "target/TablePage.pdf";

    public static void main(String[] args) {
        try {
            String apiKey, mapsApiKey;
            // Read API keys
            try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/api_key.txt"))) {
                apiKey = reader.readLine().trim();
            }
            
            try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/maps_api_key.txt"))) {
                mapsApiKey = reader.readLine().trim();
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
                            siteName = getStringValue(nodeData, "site-name");
                            location = getStringValue(nodeData, "location");

                            // Extracting data from the result array
                            JSONArray resultArray = (JSONArray) nodeData.get("result");
                            if (resultArray != null && !resultArray.isEmpty()) {
                                for (Object resultObj : resultArray) {
                                    JSONObject resultData = (JSONObject) resultObj;
                                    date = getStringValue(resultData, "date-time");
                                    coordinates = getCoordinates(resultData);
                                    latitude = getStringValue(resultData, "latitude");
                                    longitude = getStringValue(resultData, "longitude");
                                    status = "Online"; 

                                    // Extract readings
                                    waterHead = getStringValue(resultData, "water-head");
                                    porePressure = getStringValue(resultData, "pore-pressure");
                                    airtemp = getStringValue(resultData, "air-temp"); 

                                    // Collecting readings
                                    waterHeadSeries.add(waterHead.equals("N/A") ? Double.NaN : Double.parseDouble(waterHead));
                                    porePressureSeries.add(porePressure.equals("N/A") ? Double.NaN : Double.parseDouble(porePressure));
                                    temperatureSeries.add(airtemp.equals("N/A") ? Double.NaN : Double.parseDouble(airtemp));

                                    // Collecting time data for plotting
                                    timeSeries.add(Double.parseDouble(getHourFromDateTime(date)));
                                }

                                plotMapCoordinates(mapsApiKey, latitude, longitude, nodeId);
                            }
                        } catch (Exception e) {
                            System.out.println("Error fetching data for Node ID: " + nodeId + ". Mark this node as Offline.");
                        }

                        // Add node ID to the first cell
                        table.addCell(new Cell().add(new Paragraph(nodeId)));

                        // Create a formatted string for the details and add it to the second cell
                        String details = "Status: " + status + "\n" +
                                         "Site Name: " + siteName + "\n" +
                                         "Location: " + location + "\n" +
                                         "Coordinates: " + coordinates + "\n" +
                                         "Last Reading Date: " + date + "\n" +
                                         "Water Head: " + waterHead + "\n";

                        table.addCell(new Cell().add(new Paragraph(details)));

                        // Plotting the time series if data is available
                        if (!timeSeries.isEmpty()) {
                            if (!waterHeadSeries.isEmpty() && waterHeadSeries.stream().anyMatch(h -> !h.isNaN())) {
                                plotWaterHeadAndTemperature(nodeId, timeSeries, waterHeadSeries, temperatureSeries);
                            } else if (!porePressureSeries.isEmpty() && porePressureSeries.stream().anyMatch(p -> !p.isNaN())) {
                                plotPorePressureAndTemperature(nodeId, timeSeries, porePressureSeries, temperatureSeries);
                            }
                        }
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

    // Helper method to extract hour from date-time string
    public static String getHourFromDateTime(String dateTime) {
        // Assuming dateTime is in the format "2023-08-05T17:39:18Z"
        return dateTime.split("T")[1].split(":")[0]; // Extract hour as a string
    }

    // Method to plot Water Head and Temperature
    public static void plotWaterHeadAndTemperature(String nodeId, List<Double> timeSeries, List<Double> waterHeadSeries, List<Double> temperatureSeries) {
        XYSeries waterHeadSeriesPlot = new XYSeries("Water Head (cm)");
        XYSeries temperatureSeriesPlot = new XYSeries("Temperature (°C)");

        for (int i = 0; i < timeSeries.size(); i++) {
            if (!waterHeadSeries.get(i).isNaN()) {
                waterHeadSeriesPlot.add(timeSeries.get(i), waterHeadSeries.get(i));
            }
            if (!temperatureSeries.get(i).isNaN()) {
                temperatureSeriesPlot.add(timeSeries.get(i), temperatureSeries.get(i));
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(waterHeadSeriesPlot);
        dataset.addSeries(temperatureSeriesPlot);

        JFreeChart chart = ChartFactory.createXYLineChart(
            "Node: " + nodeId + " Water Head and Temperature vs Time",
            "Time (hours)",
            "Value (cm or °C)",
            dataset
        );

        // Save the chart as an image
        try {
            ChartUtils.saveChartAsPNG(new File("target/" + nodeId + "_water_head_temp_plot.png"), chart, 800, 600);
            System.out.println("Chart saved for node: " + nodeId + " (Water Head and Temperature)");
        } catch (IOException e) {
            System.out.println("Error saving chart for Node ID: " + nodeId);
        }
    }

    // Method to plot Pore Pressure and Temperature
    public static void plotPorePressureAndTemperature(String nodeId, List<Double> timeSeries, List<Double> porePressureSeries, List<Double> temperatureSeries) {
        XYSeries porePressureSeriesPlot = new XYSeries("Pore Pressure (kPa)");
        XYSeries temperatureSeriesPlot = new XYSeries("Temperature (°C)");

        for (int i = 0; i < timeSeries.size(); i++) {
            if (!porePressureSeries.get(i).isNaN()) {
                porePressureSeriesPlot.add(timeSeries.get(i), porePressureSeries.get(i));
            }
            if (!temperatureSeries.get(i).isNaN()) {
                temperatureSeriesPlot.add(timeSeries.get(i), temperatureSeries.get(i));
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(porePressureSeriesPlot);
        dataset.addSeries(temperatureSeriesPlot);

        JFreeChart chart = ChartFactory.createXYLineChart(
            "Node: " + nodeId + " Pore Pressure and Temperature vs Time",
            "Time (hours)",
            "Value (kPa or °C)",
            dataset
        );

        // Save the chart as an image
        try {
            ChartUtils.saveChartAsPNG(new File("target/" + nodeId + "_pore_pressure_temp_plot.png"), chart, 800, 600);
            System.out.println("Chart saved for node: " + nodeId + " (Pore Pressure and Temperature)");
        } catch (IOException e) {
            System.out.println("Error saving chart for Node ID: " + nodeId);
        }
    }

    // Method to plot map with coordinates
    public static void plotMapCoordinates(String mapsApiKey, String latitude, String longitude, String nodeId) {
        try {
            String mapUrl = "https://maps.googleapis.com/maps/api/staticmap?center=" + latitude + "," + longitude
                          + "&zoom=14&size=600x300&markers=color:red%7C" + latitude + "," + longitude
                          + "&key=" + mapsApiKey;

            BufferedImage image = ImageIO.read(new URL(mapUrl));
            ImageIO.write(image, "png", new File("target/" + nodeId + "_map_coordinates.png"));
            System.out.println("Map image saved for node: " + nodeId);
        } catch (IOException e) {
            System.out.println("Error fetching map for Node ID: " + nodeId);
        }
    }
}
