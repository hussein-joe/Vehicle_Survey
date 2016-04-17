package com.challenges.vehicle_survey.data_reader;

import com.challenges.vehicle_survey.data_reader.SensorDataReader.SensorDataRecord;

public class InvalidRecordHandler<T extends SensorDataRecord> {

	public T handleInvalidRecord(String recordRawData) {
		
		throw new RuntimeException( "Sensor data record " + recordRawData + " is not expected" );
	}
}
