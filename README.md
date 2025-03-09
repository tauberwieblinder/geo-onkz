# GeoONKZ

A Spring Boot application that provides German phone area codes (Ortsnetzkennzahlen) as of 2025 based on geographic coordinates.

## Features

- RESTful API endpoint to retrieve area codes from latitude/longitude coordinates
- Uses official geodata from the German Federal Network Agency Bundesnetzagentur
- Coordinate system transformation from WGS84 (standard GPS) to DHDN (German geodetic system)
- Simple web interface

## API Usage

### Get Area Code by Coordinates

```
GET /api/areacode?lat={latitude}&lon={longitude}
```

**Parameters:**

- `lat` (required): Latitude in decimal degrees (WGS84)
- `lon` (required): Longitude in decimal degrees (WGS84)

**Example Request:**

```
GET /api/areacode?lat=52.516667&lon=13.388889
```

**Example Response:**

```json
{
  "areaCode": "30"
}
```

**Error Responses:**

- 400 Bad Request: Missing required parameters
- 404 Not Found: No area code found for the given coordinates
- 500 Internal Server Error: Server-side error

## Setup and Installation

1. Clone the repository
2. If a newer version is available, download the shapefile from the Bundesnetzagentur
   (https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/ONRufnr/Einteilung_ONB/start.html). Extract and replace the shapefile data in `src/main/resources/static/ONKZBorders`
4. Build the project: `./mvnw clean install`
5. Run the application: `./mvnw spring-boot:run`
6. Access the web interface: http://localhost:8080
7. Access the API: http://localhost:8080/api/areacode?lat=52.516667&lon=13.388889

## Technology Stack

- Java 23
- Spring Boot 3.4.3
- GeoTools 32.2 (for shapefile processing)

## License

This project is licensed under the MIT License - see the LICENSE file for details.