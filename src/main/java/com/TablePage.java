package com;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

public class TablePage {
    public static final String PDF_DEST_STRING = "C:\\Users\\lekgo\\OneDrive\\Documents\\Groundwork\\GroundWork\\target\\TablePage.pdf";

    public static void main(String[] args) throws Exception {
        // Create a new PDF document
        PdfWriter writer = new PdfWriter(PDF_DEST_STRING);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Create a table with 4 columns
        Table table = new Table(4); 

        // Adding header row
        table.addHeaderCell(new Cell().add(new Paragraph(" "))); // Empty cell for the first column
        table.addHeaderCell(new Cell().add(new Paragraph("Node ID")));
        table.addHeaderCell(new Cell().add(new Paragraph("Node ID")));
        table.addHeaderCell(new Cell().add(new Paragraph("Node ID")));

        // Adding data rows

        // Status row
        table.addCell(new Cell().add(new Paragraph("Status "))); 
        table.addCell(new Cell()); // Empty cell for "Node ID"
        table.addCell(new Cell());
        table.addCell(new Cell());

        // Site name row
        table.addCell(new Cell().add(new Paragraph("Site Name ")));
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());

        // Location row
        table.addCell(new Cell().add(new Paragraph("Location ")));
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());

        // Coordinates row
        table.addCell(new Cell().add(new Paragraph("Coordinates ")));
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());

        // Last reading date row
        table.addCell(new Cell().add(new Paragraph("Last reading date ")));
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());

        // Water head row
        table.addCell(new Cell().add(new Paragraph("Water head ")));
        table.addCell(new Cell());
        table.addCell(new Cell());
        table.addCell(new Cell());

        // Add the table to the PDF
        document.add(table);

        // Close the document
        document.close();
        System.out.println("PDF with table created successfully.");
    }
}
