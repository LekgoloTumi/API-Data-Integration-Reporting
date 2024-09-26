package com;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import io.github.cdimascio.dotenv.Dotenv;

public class VisualizeData {
    private static final String MAP_URL;
    private static final String WATER_HEAD_TEMPERATURE_CHART_PATH;
    private static final String PORE_PRESSURE_TEMPERATURE_CHART_PATH;
    private static final String MAP_COORDINATES_IMAGE_PATH;

    static {
        // Load environment variables
        Dotenv dotenv = Dotenv.load();
        MAP_URL = dotenv.get("MAP_URL");
        WATER_HEAD_TEMPERATURE_CHART_PATH = dotenv.get("WATER_HEAD_TEMPERATURE_CHART_PATH");
        PORE_PRESSURE_TEMPERATURE_CHART_PATH = dotenv.get("PORE_PRESSURE_TEMPERATURE_CHART_PATH");
        MAP_COORDINATES_IMAGE_PATH = dotenv.get("MAP_COORDINATES_IMAGE_PATH");
    }

    // Method to plot map with coordinates
    public static void plotMapCoordinates(String mapsApiKey, String latitude, String longitude, String nodeId) {
    try {
        String mapUrl = String.format(MAP_URL, latitude, longitude, latitude, longitude, mapsApiKey);

        BufferedImage image = ImageIO.read(new URL(mapUrl));
        String mapImagePath = MAP_COORDINATES_IMAGE_PATH.replace("{nodeId}", nodeId);
        ImageIO.write(image, "png", new File(mapImagePath));
        System.out.println("Map image saved for node: " + nodeId);
    } catch (IOException e) {
        System.out.println("Error fetching map for Node ID: " + nodeId);
    } catch (Exception e) {
        e.printStackTrace();
    }
}


    // Method to plot Water Head and Temperature
    public static void plotWaterHeadAndTemperature(String nodeId, List<Double> timeSeries, List<Double> waterHeadSeries, List<Double> temperatureSeries) {
        XYSeries waterHeadSeriesPlot = new XYSeries("Water Head (cm)");
        XYSeries temperatureSeriesPlot = new XYSeries("Temperature (°C)");

        for (int i = 0; i < timeSeries.size(); i++) {
            if (!waterHeadSeries.get(i).isNaN()) {
                waterHeadSeriesPlot.add(timeSeries.get(i), waterHeadSeries.get(i));
            }
            if (!temperatureSeries.get(i).isNaN()) {
                temperatureSeriesPlot.add(timeSeries.get(i), temperatureSeries.get(i));
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(waterHeadSeriesPlot);
        dataset.addSeries(temperatureSeriesPlot);

        JFreeChart chart = ChartFactory.createXYLineChart("Water Head and Temperature for Node " + nodeId, "Time (Hours)", "Value", dataset);
        try {
            String chartFilePath = WATER_HEAD_TEMPERATURE_CHART_PATH.replace("{nodeId}", nodeId);
            File chartFile = new File(chartFilePath);
            ChartUtils.saveChartAsPNG(chartFile, chart, 800, 600);
        } catch (IOException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to plot Pore Pressure and Temperature
    public static void plotPorePressureAndTemperature(String nodeId, List<Double> timeSeries, List<Double> porePressureSeries, List<Double> temperatureSeries) {
        XYSeries porePressureSeriesPlot = new XYSeries("Pore Pressure (kPa)");
        XYSeries temperatureSeriesPlot = new XYSeries("Temperature (°C)");

        for (int i = 0; i < timeSeries.size(); i++) {
            if (!porePressureSeries.get(i).isNaN()) {
                porePressureSeriesPlot.add(timeSeries.get(i), porePressureSeries.get(i));
            }
            if (!temperatureSeries.get(i).isNaN()) {
                temperatureSeriesPlot.add(timeSeries.get(i), temperatureSeries.get(i));
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(porePressureSeriesPlot);
        dataset.addSeries(temperatureSeriesPlot);

        JFreeChart chart = ChartFactory.createXYLineChart("Pore Pressure and Temperature for Node " + nodeId, "Time (Hours)", "Value", dataset);
        try {
            String chartFilePath = PORE_PRESSURE_TEMPERATURE_CHART_PATH.replace("{nodeId}", nodeId);
            File chartFile = new File(chartFilePath);
            ChartUtils.saveChartAsPNG(chartFile, chart, 800, 600);
        } catch (IOException e) {
            System.out.println("Error: " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
