package com.challenges.vehicle_survey.data_reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.challenges.vehicle_survey.data_reader.SensorDataReader.SensorDataRecord;

public class FileSensorDataReader<T extends SensorDataRecord> implements SensorDataReader<T> {

	
	private Stream<String> fileLinesStream;
	private Pattern sensorDataPattern;
	private InvalidRecordHandler<T> invalidRecordHandler;
	
	public FileSensorDataReader(InvalidRecordHandler<T> invalidRecordHandler, Path filePath) {
		super();
		this.initReader(filePath);
		this.sensorDataPattern = Pattern.compile("^([AB]{1,1})(\\d+)$");
		this.invalidRecordHandler = invalidRecordHandler;
	}

	@Override
	public Stream<T> stream() {
		
		return this.fileLinesStream.map( this::parseRecord );
	}

	@SuppressWarnings("unchecked")
	protected T parseRecord(String rawData) {
		
		Matcher matcher = this.sensorDataPattern.matcher( rawData.trim() );
		if ( !matcher.matches() ) {
			
			return (T) this.invalidRecordHandler.handleInvalidRecord(rawData);
		}
		
		String sensorName = matcher.group(1);
		long readingTimeInMilliseconds = Long.valueOf(matcher.group(2));
		
		SensorDataRecord  parsedRecord = new SensorDataRecord(Sensor.valueOf( sensorName ), 
				LocalTime.ofNanoOfDay( readingTimeInMilliseconds * 1000000 ));
		
		return (T) parsedRecord;
	}
	
	private void initReader(Path filePath) {
		
		if ( filePath == null || !Files.isReadable(filePath) ) {
			
			throw new RuntimeException("The passed file is null or not readable");
		}
		try {
			this.fileLinesStream = Files.newBufferedReader(filePath).lines().filter( line -> !line.isEmpty() );
		} catch (IOException e) {
			
			throw new RuntimeException("Failed to read data from the passed file");
		}
	}
}
