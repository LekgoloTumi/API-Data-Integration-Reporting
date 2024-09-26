package com;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

import io.github.cdimascio.dotenv.Dotenv;

public class GenerateFiles {
    // Load environment variables
    private static final Dotenv dotenv = Dotenv.load();

    private static final String KMZ_DEST_STRING = dotenv.get("KMZ_DEST_STRING", "target/coordinates.kmz");
    private static final String NODE_DATAPLOT_DEST_STRING = dotenv.get("NODE_DATAPLOT_DEST_STRING", "target/TimeSeriesData.pdf");
    private static final String NODE_COORD_DEST_STRING = dotenv.get("NODE_COORD_DEST_STRING", "target/NodeCoordinates.pdf");
    private static final String WATER_HEAD_TEMPERATURE_CHART_PATH = dotenv.get("WATER_HEAD_TEMPERATURE_CHART_PATH", "src/main/resources/charts/{nodeId}_water_head_temperature.png");
    private static final String PORE_PRESSURE_TEMPERATURE_CHART_PATH = dotenv.get("PORE_PRESSURE_TEMPERATURE_CHART_PATH", "src/main/resources/charts/{nodeId}_pore_pressure_temperature.png");
    private static final String COORD_PATH = dotenv.get("COORD_PATH", "./target/");

    // Method to create KMZ file with coordinates
    public static void createKMZFile(List<String> coordinatesList) {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(KMZ_DEST_STRING))) {
            ZipEntry zipEntry = new ZipEntry("resources/coordinates.txt");
            zos.putNextEntry(zipEntry);
            for (String coordinate : coordinatesList) {
                zos.write((coordinate + "\n").getBytes());
            }
            zos.closeEntry();
        } catch (IOException e) {
            System.err.println("Error creating KMZ file: " + e.getMessage());
        }
    }

    // Method to generate Time Series PDF
    public static void generateTimeSeriesPDF(List<String> nodeIDs) {
        try (PdfWriter writer = new PdfWriter(NODE_DATAPLOT_DEST_STRING);
             PdfDocument pdfDocument = new PdfDocument(writer);
             Document document = new Document(pdfDocument)) {

            for (String nodeId : nodeIDs) {
                File waterChartFile = new File(WATER_HEAD_TEMPERATURE_CHART_PATH.replace("{nodeId}", nodeId));
                if (waterChartFile.exists()) {
                    addImageToDocument(document, waterChartFile);
                } else {
                    System.out.println("Water Head Temperature Chart for Node " + nodeId + " not found.");
                }

                File porePressureChartFile = new File(PORE_PRESSURE_TEMPERATURE_CHART_PATH.replace("{nodeId}", nodeId));
                if (porePressureChartFile.exists()) {
                    addImageToDocument(document, porePressureChartFile);
                } else {
                    System.out.println("Pore Pressure Temperature Chart for Node " + nodeId + " not found.");
                }
            }
        } catch (IOException e) {
            System.err.println("Error generating Time Series PDF: " + e.getMessage());
        }
    }

    // Method to generate Node Coordinates PDF
    public static void generateNodeCoordinatesPDF(List<String> nodeIDs) {
        try (PdfWriter writer = new PdfWriter(NODE_COORD_DEST_STRING);
             PdfDocument pdfDocument = new PdfDocument(writer);
             Document document = new Document(pdfDocument)) {

            for (String nodeId : nodeIDs) {
                File coordFile = new File(COORD_PATH + nodeId + "_map_coordinates.png");
                if (coordFile.exists()) {
                    document.add(new Paragraph("Coordinates for node: " + nodeId));
                    addImageToDocument(document, coordFile);
                } else {
                    System.err.println("Coordinates for Node " + nodeId + " not found.");
                }
            }
        } catch (IOException e) {
            System.err.println("Error generating Node Coordinates PDF: " + e.getMessage());
        }
    }

    // Helper method to add images to the PDF document
    public static void addImageToDocument(Document document, File imageFile) {
        try {
            Image image = new Image(ImageDataFactory.create(imageFile.getAbsolutePath()));
            document.add(image);
        } catch (IOException e) {
            System.err.println("Error adding image to document: " + e.getMessage());
        }
    }
}
