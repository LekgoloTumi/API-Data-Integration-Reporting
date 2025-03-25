# Project Overview 

This project is designed to handle environmental data collected from sensor nodes. It fetches data from an API, visualizes the data through charts and maps, generates reports (PDFs and KMZ), and includes helper methods for extracting and formatting data.
How the Project Works (Updated)

The project consists of several components that work together to:

    Fetch Data from an external API.

    Visualize the Data using charts and maps.

    Generate Reports in PDF and KMZ formats.

# 1. APIRequest.java (Fetching and Processing Data)

This class is responsible for fetching and processing sensor data from an API.
How It Works:

    Sends an HTTPS request to fetch data from the external API.

    Handles 500 errors by retrying up to 3 times.

    Parses the JSON response and extracts relevant node data.

# 2. Getters.java (Helper Methods for Data Extraction)

This class provides helper methods for safely extracting values from JSON data.
How It Works:

    getStringValue: Safely retrieves string values from JSON objects. If the key doesn’t exist, it returns "N/A".

    getCoordinates: Formats and returns the latitude and longitude from the JSON result as a single string.

    getHourFromDateTime: Extracts and returns the hour from a date-time string (in the format YYYY-MM-DDTHH:MM:SSZ).

# 3. VisualizeData.java (Generating Charts and Maps)

This class generates visual representations of sensor data, including charts and maps.
How It Works:
a) Plotting a Map for Node Location

    Fetches a Google Maps API image of the node’s latitude and longitude.

    Saves the image locally for visualization.

b) Plotting Water Head and Temperature Chart

    Creates a line chart for Water Head (cm) and Temperature (°C) over time.

    Saves the chart as a PNG image.

c) Plotting Pore Pressure and Temperature Chart

    Generates a line chart for Pore Pressure (kPa) and Temperature (°C) over time.

    Saves the chart as a PNG image.

# 4. GenerateFiles.java (Creating KMZ and PDFs)

This class generates KMZ and PDF files for node-related data.
How It Works:
a) Generating a KMZ File

    Creates a KMZ file containing KML format with a LineString representing the path between node coordinates.

    The file can be viewed in mapping applications like Google Earth.

b) Generating Time Series PDF

    Combines Water Head & Temperature charts and Pore Pressure & Temperature charts into a single PDF document.

    Each chart is added as an image and grouped by Node ID.

c) Generating Node Coordinates PDF

    Combines coordinate images for each node in a PDF document.

    Each image represents the node's location on the map.

# 5. DataAnalysis.java (Generating a PDF Report)

This class compiles the generated data, charts, and analysis into a structured PDF report.

    The Time Series PDF and Node Coordinates PDF are integrated into a final report summarizing key findings and visualizing trends.

# Integration and Workflow

    APIRequest – Fetches real-time data for sensor nodes.

    Getters – Provides helper methods for safely extracting values and formatting data.

    VisualizeData – Creates charts and maps based on the retrieved data.

    GenerateFiles – Generates KMZ files and PDF documents for the data visualizations.

    DataAnalysis – Compiles the findings into a final report.
