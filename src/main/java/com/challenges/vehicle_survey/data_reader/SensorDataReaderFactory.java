package com.challenges.vehicle_survey.data_reader;

import java.nio.file.Path;

import com.challenges.vehicle_survey.data_reader.SensorDataReader.SensorDataRecord;

public class SensorDataReaderFactory<T extends SensorDataRecord> {

	private InvalidRecordHandler<T> invalidRecordHandler;
	
	public SensorDataReaderFactory(InvalidRecordHandler<T> invalidRecordHandler) {
		super();
		this.invalidRecordHandler = invalidRecordHandler;
	}

	public SensorDataReader<T> getFileSensorDataReader(Path filePath) {
		
		return new FileSensorDataReader<>(invalidRecordHandler, filePath);
	}
}
