package com;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

public class PDFGenator {
    public static final String PDF_DEST_STRING = "C:\\Users\\lekgo\\OneDrive\\Documents\\Groundwork\\GroundWork\\target\\GroundWorkReport.pdf";

    public static final String[] IMAGES = {
        "./src/main/resources/image/GroundWorkHeader.png"
    };

    public static void main(String[] args) throws Exception {
        try {
            // Create a PDF writer
            PdfWriter writer = new PdfWriter(PDF_DEST_STRING);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            //Front Page with company logo
            ImageData imgData = ImageDataFactory.create(IMAGES[0]);
            Image img = new Image(imgData);
            document.add(img);
            document.add(new Paragraph("--Page 1--"));

            // Introduction Page
            pdf.addNewPage();
            document.add(new Paragraph("Introduction:"));

            document.close();
            System.out.println("Pdf created successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
