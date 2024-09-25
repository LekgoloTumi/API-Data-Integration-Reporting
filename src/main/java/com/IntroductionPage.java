package com;

import java.io.FileNotFoundException;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

public class IntroductionPage {
    public static final String PDF_DEST_STRING = "target\\IntroductionPage.pdf";

    public static void main(String[] args) throws Exception {
        try {
            // Create a PDF writer
            PdfWriter writer = new PdfWriter(PDF_DEST_STRING);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Front Page with company logo
            document.add(new Paragraph("Introduction:"));
            document.add(new Paragraph("--The page content is here.--"));

            document.close();
            System.out.println("Pdf created successfully.");

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
