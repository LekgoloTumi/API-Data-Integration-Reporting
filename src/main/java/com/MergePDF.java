package com;

import java.io.File;
import java.io.IOException;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;

import io.github.cdimascio.dotenv.Dotenv;

public class MergePDF {
    
    // Output file path for the merged PDF
    public static String PDF_STRING;

    // Array with all file paths of PDFs to be merged
    public static String[] DEST_STRING;

    public static void main(String[] args) {
        // Load environment variables
        Dotenv dotenv = Dotenv.load();

        // Assign values from the .env file
        PDF_STRING = dotenv.get("PDF_STRING", "./target/Report.pdf");  // Default value if not present in .env
        String FRONT_PAGE = dotenv.get("FRONT_PAGE");
        String INTRODUCTION_PAGE = dotenv.get("INTRODUCTION_PAGE");
        String DATA_SUMMARY_PAGE = dotenv.get("DATA_SUMMARY_PAGE");
        String DATA_VISUALIZATION_PAGE = dotenv.get("DATA_VISUALIZATION_PAGE");
        String DATA_ANALYSIS_PAGE = dotenv.get("DATA_ANALYSIS_PAGE");
        String API_CHALLENGES_PAGE = dotenv.get("API_CHALLENGES_PAGE");
        String LOCATION_PLOT_PAGE = dotenv.get("LOCATION_PLOT_PAGE");

        // Populate the array of file paths dynamically from .env
        DEST_STRING = new String[]{
            FRONT_PAGE,
            INTRODUCTION_PAGE,
            DATA_SUMMARY_PAGE,
            DATA_VISUALIZATION_PAGE,
            DATA_ANALYSIS_PAGE,
            API_CHALLENGES_PAGE,
            LOCATION_PLOT_PAGE
        };

        // Create directory if it doesn't exist
        File file = new File(PDF_STRING);
        file.getParentFile().mkdirs();

        // Perform PDF merge
        new MergePDF().mergePdf(PDF_STRING);
    }

    // Method to merge all PDFs into one
    protected void mergePdf(String dest) {
        try {
            // Create PdfWriter for the destination merged file
            PdfWriter writer = new PdfWriter(dest);

            // Create a PdfDocument for the merged output
            PdfDocument pdfDoc = new PdfDocument(writer);
            PdfMerger merger = new PdfMerger(pdfDoc);

            // Loop through each file in the array of source PDFs
            for (String file : DEST_STRING) {
                if (file != null && !file.isEmpty()) {  // Check if the file path is not null or empty
                    // Ensure that the PDF file exists before attempting to merge
                    File pdfFile = new File(file);
                    if (pdfFile.exists() && !pdfFile.isDirectory()) {
                        // Open the source PDF
                        PdfDocument pdf = new PdfDocument(new PdfReader(file));
                        
                        // Merge the source PDF into the destination
                        merger.merge(pdf, 1, pdf.getNumberOfPages());

                        // Close the source PDF to free resources
                        pdf.close();
                    } else {
                        System.out.println("File does not exist or is not a PDF: " + file);
                    }
                }
            }

            // Close the merged PdfDocument
            pdfDoc.close();

            System.out.println("PDFs merged successfully into: " + PDF_STRING);

        } catch (IOException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
