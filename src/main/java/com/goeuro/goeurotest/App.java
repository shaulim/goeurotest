package com.goeuro.goeurotest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.goeuro.goeurotest.model.Location;
import com.goeuro.goeurotest.model.LocationCSV;

public class App {
	public static void main(String[] args) throws ClientProtocolException, IOException {

		final String apiUrlPrefix = "http://api.goeuro.com/api/v2/position/suggest/en/";
		
		String inputStr = extractInput(args);

		List<Location> locations = getLocationsFromAPI(apiUrlPrefix, inputStr);

		System.out.print("locations: " + locations);
		
		List<LocationCSV> locationsCSV = transformToCSV(locations);
		
		createCSVFile(locationsCSV);
	}

	private static void createCSVFile(List<LocationCSV> locationsCSV) throws FileNotFoundException,
			UnsupportedEncodingException, IOException, JsonGenerationException, JsonMappingException {
		// create mapper and schema
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.schemaFor(LocationCSV.class);
        schema = schema.withColumnSeparator(',');
        
        // output writer
        ObjectWriter myObjectWriter = csvMapper.writer(schema);
        File tempFile = new File("locations.csv");
        FileOutputStream tempFileOutputStream = new FileOutputStream(tempFile);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(tempFileOutputStream, 1024);
        OutputStreamWriter writerOutputStream = new OutputStreamWriter(bufferedOutputStream, "UTF-8");
        myObjectWriter.writeValue(writerOutputStream, locationsCSV);
	}

	private static List<LocationCSV> transformToCSV(List<Location> locations) {
		List<LocationCSV> locationsCSV = new ArrayList<>();
		
		for (Location location : locations) {
			locationsCSV.add(new LocationCSV(location));
		}
		return locationsCSV;
	}

	private static List<Location> getLocationsFromAPI(final String apiUrlPrefix, String inputStr)
			throws IOException, ClientProtocolException, JsonParseException, JsonMappingException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet getReq = new HttpGet(apiUrlPrefix + inputStr);

		HttpResponse response = client.execute(getReq);

		int statusCode = response.getStatusLine().getStatusCode();

		if (statusCode >= 300) {
			throw new RuntimeException("Bad response from API server. code : " + statusCode);
		}

		// Get hold of the response entity
		HttpEntity entity = response.getEntity();

		InputStream content = entity.getContent();

		ObjectMapper mapper = new ObjectMapper();

		List<Location> locations = mapper.readValue(content, new TypeReference<List<Location>> () {});
		return locations;
	}

	private static String extractInput(String[] args) {
		if (args.length == 0) {
			throw new RuntimeException("no input provided");
		}

		String inputStr = args[0];

		if (inputStr == null || inputStr == "") {
			throw new RuntimeException("null or empty input");
		}
		return inputStr;
	}
}
