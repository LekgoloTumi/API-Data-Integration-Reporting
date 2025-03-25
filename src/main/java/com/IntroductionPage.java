package com;

import java.io.FileNotFoundException;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import io.github.cdimascio.dotenv.Dotenv;

public class IntroductionPage {
    // Load environment variables
    private static final Dotenv dotenv = Dotenv.load();
    
    // PDF destination path from .env or default value
    public static final String PDF_DEST_STRING = dotenv.get("INTRODUCTION_PAGE");

    public static void main(String[] args) throws Exception {
        try {
            // Create a PDF writer
            PdfWriter writer = new PdfWriter(PDF_DEST_STRING);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Introduction content
            document.add(new Paragraph("Introduction:" + "\n"));
            document.add(new Paragraph("The report aims to conduct a thorough analysis of sensor data obtained from the API. Our primary objective is to improve our understanding of sensor parameters over time, identify any irregularities, and assess the reliability of the sensor network. The main goal is to visually present the data to identify trends and anomalies, ultimately creating a comprehensive summary of the sensor parameters, including visual location plots of the coordinates of each of the nodes that are online."));

            document.close();
            System.out.println("PDF created successfully.");

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
