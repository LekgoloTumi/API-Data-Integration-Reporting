package com;

import java.io.FileNotFoundException;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import io.github.cdimascio.dotenv.Dotenv;

public class DataAnalysis {
    // Load environment variables
    private static final Dotenv dotenv = Dotenv.load();
    
    // PDF destination path from .env or default value
    public static final String PDF_DEST_STRING = dotenv.get("DATA_ANALYSIS_PAGE");

    public static void main(String[] args) throws Exception {
        try {
            // Create a PDF writer
            PdfWriter writer = new PdfWriter(PDF_DEST_STRING);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Heading of page
            document.add(new Paragraph("Data Analysis:"));

            // Analysis of the first graph
            document.add(new Paragraph("Graph 1: Water Head and Temperature for Node 4526" + 
                                "\n" + 
                                "Key Observations:\n" + 
                                "\n" + 
                                "Water Head (Red Line):\n" + 
                                "There is a sharp spike in the water head around hour 10, where it rises significantly, reaching over 100 cm. This anomaly suggests an unusual event or reading, as most other points stay between 0 and 30 cm.\n" + 
                                "The pattern from hours 0 to 24 shows a cyclic nature, with the water head consistently rising and falling. However, most of the time, the water head does not exceed 30 cm except for that one major spike.\n" + 
                                "\n" + 
                                "Temperature (Blue Line):\n" + 
                                "The temperature remains much more stable compared to the water head. It fluctuates between 5 and 25°C across the day.\n" + 
                                "There are regular peaks every few hours, showing a slight cyclic pattern similar to the water head, but these fluctuations are less dramatic.\n" + 
                                "\n" + 
                                "Hourly Pattern:\n" + 
                                "Both variables (water head and temperature) seem to exhibit a repeating hourly pattern, with periodic increases and decreases. The main outlier is the dramatic spike in the water head at hour 10, which may suggest a sensor malfunction or an event like a water surge or pump activation.\n" + 
                                "\n" + 
                                "Interpretation:\n" + 
                                "The water head behaves unpredictably at certain times, especially at hour 10, which could indicate a specific event or error that needs investigation.\n" + 
                                "The temperature remains stable, with minor fluctuations, suggesting a consistent environment throughout the day."));

            // Analysis of the second graph
            document.add(new Paragraph("Graph 2: Water Head and Temperature for Node 7999" + 
                                "\n" + 
                                "Key Observations:\n" + 
                                "\n" + 
                                "Water Head (Red Line):\n" + 
                                "The water head for Node 7999 fluctuates between 0 and 15 cm throughout the day, with peaks at specific intervals around hours 3, 6, 9, 12, 15, 18, and 21.\n" + 
                                "\n" + 
                                "Temperature (Blue Line):\n" + 
                                "The temperature fluctuates between 10 and 24°C, with peaks at similar times as the water head.\n" + 
                                "\n" + 
                                "Interpretation:\n" + 
                                "The water head and temperature for Node 7999 exhibit stable cyclic behaviour, with no extreme outliers or sudden changes. The regular peaks in both temperature and water head could suggest a system or environmental factor causing these cyclic fluctuations.\n" + 
                                ""));

            // Notable issues with the data and api
            document.add(new Paragraph("I have observed the following issues:" + 
                                "\n" + 
                                "I have noticed that no parameter clearly indicates whether a node is online, which makes it difficult to determine its status.\n" + 
                                "\n" + 
                                "As a result of this issue, I encountered a response code error. This led me to implement a logic to check whether a node result array is empty to indicate the node's status."));

            document.close();
            System.out.println("Pdf created successfully.");

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
