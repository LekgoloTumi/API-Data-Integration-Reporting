package com;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

public class FrontPage {
    public static final String PDF_DEST_STRING = "target\\FrontPage.pdf";

    public static final String[] IMAGES = {
        "src\\main\\resources\\image\\ImperialLogo.png"
    };

    public static void main(String[] args) throws Exception {
        try {
            // Create a PDF writer
            PdfWriter writer = new PdfWriter(PDF_DEST_STRING);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Front Page with company logo
            ImageData imgData = ImageDataFactory.create(IMAGES[0]);
            Image img = new Image(imgData);
            document.add(img);  // Add logo to the front page

            document.close();
            System.out.println("Pdf created successfully.");

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
