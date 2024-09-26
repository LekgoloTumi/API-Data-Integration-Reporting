package com;

import java.io.FileNotFoundException;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import io.github.cdimascio.dotenv.Dotenv;

public class ApiChallenges {
    // Load environment variables
    private static final Dotenv dotenv = Dotenv.load();
    
    // PDF destination path from .env or default value
    public static final String PDF_DEST_STRING = dotenv.get("API_CHALLENGES_PAGE");

    public static void main(String[] args) throws Exception {
        try {
            // Create a PDF writer
            PdfWriter writer = new PdfWriter(PDF_DEST_STRING);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // API Challenges section
            document.add(new Paragraph("API Challenges:"));
            document.add(new Paragraph("I encountered an error while trying to retrieve data directly from a JSONArray. Upon further investigation, I found that this error is caused by a server-side issue that arises when handling my request.\n" +
                                "\n" +
                                "There are several potential causes for this error. To address this, I have implemented error handling to log and catch specific mistakes that might lead to this error in the code. These include using a malformed JSON structure, retrieving data from elements in the JSONArray that are null or undefined, implementing a retry mechanism, parsing the response into a JSONObject first, and then attempting to extract the nodes array."));

            document.close();
            System.out.println("Pdf created successfully.");

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
