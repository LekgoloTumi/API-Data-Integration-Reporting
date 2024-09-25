package  com;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

public class DataPlotter {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("3D Scatter Plot Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JPanel chartPanel = createChartPanel(); // Create the chart
            frame.add(chartPanel);
            frame.pack();
            frame.setVisible(true);

            try {
                saveChartAsPDF("chart_output.pdf", chartPanel); // Save to PDF
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // Create the 3D scatter plot chart
    private static JPanel createChartPanel() {
        XYSeries series = new XYSeries("Data");
        series.add(1, 5); // Replace with actual data
        series.add(2, 7);
        series.add(3, 8);

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createScatterPlot(
                "3D Scatter Plot",   // Chart title
                "X-Axis",            // X-Axis Label
                "Y-Axis",            // Y-Axis Label
                dataset,             // Data
                PlotOrientation.VERTICAL, 
                true,                // Show legend
                true,
                false
        );

        return new ChartPanel(chart);
    }

    // Save the chart as a PDF
    private static void saveChartAsPDF(String filePath, JPanel chartPanel) throws IOException {
        // Convert the chart to a BufferedImage
        BufferedImage bufferedImage = new BufferedImage(chartPanel.getWidth(), chartPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
        chartPanel.paint(bufferedImage.getGraphics());

        // Write the buffered image to a ByteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);

        // Create PDF with iText
        PdfWriter pdfWriter = new PdfWriter(filePath);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument);

        // Convert the image to an iText Image object
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        Image image = new Image(ImageDataFactory.create(imageBytes));
        
        // Scale the image to fit the PDF page
        image.setAutoScale(true);

        // Add the image to the PDF
        document.add(image);

        // Close the document
        document.close();

        System.out.println("PDF created: " + filePath);
    }
}
