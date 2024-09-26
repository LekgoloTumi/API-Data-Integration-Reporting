# GroundWork Data Processing and PDF Generation Project

This project contains various Java scripts that retrieve sensor data from GroundWork's API, visualize it, and generate a detailed PDF report. The report includes data analysis, location plotting, time series visualization, and more. The project uses iTextPDF for PDF generation, Google Maps API for location plotting, and environment variables for dynamic configuration.

## Prerequisites

Ensure that the following are installed on your system:

- **Java 8** or higher
- **Maven** (for dependency management)
- Internet access for external API requests (Google Maps, GroundWork API)

## Project Overview

The project includes the following scripts:

1. **FetchAndPlotData**: Retrieves sensor data from the GroundWork API and generates PNG plots.
2. **NodeMapPDFGenerator**: Generates a PDF with map visualizations using Google Maps API.
3. **TimeSeriesPDFGenerator**: Generates a PDF with time series data plots.
4. **MergePDF**: Merges multiple PDF files (generated by the above scripts) into one final report.


## Install Dependencies

To install all dependencies before running any of the scripts 
**Use this command: mvn clean install**
