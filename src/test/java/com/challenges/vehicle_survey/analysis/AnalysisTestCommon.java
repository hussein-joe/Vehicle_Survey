package com.challenges.vehicle_survey.analysis;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

import com.challenges.vehicle_survey.data_reader.SensorDataReader.Sensor;
import com.challenges.vehicle_survey.data_reader.SensorDataReader.SensorDataRecord;
import com.challenges.vehicle_survey.processor.RoadDirection;
import com.challenges.vehicle_survey.processor.VehiclePassRecord;
import com.challenges.vehicle_survey.processor.VehiclePassRecord.VehiclePassRecordBuilder;

public abstract class AnalysisTestCommon {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	protected VehiclePassRecordBuilder vehiclePassRecordBuilder;
	
	protected AnalysisTestCommon() {
		
		this.vehiclePassRecordBuilder = new VehiclePassRecordBuilder();
		this.vehiclePassRecordBuilder.direction(RoadDirection.Northbound).readingDay(1);
	}
	
	protected Stream<VehiclePassRecord> createVehiclePassRecords(int count, int intervalInMinutes) {
		
		List<VehiclePassRecord> vehiclePassRecords = new ArrayList<VehiclePassRecord>();
		
		VehiclePassRecordBuilder vehiclePassRecordBuilder = new VehiclePassRecordBuilder();
		vehiclePassRecordBuilder.direction(RoadDirection.Northbound).readingDay(1);
		
		LocalTime currentTime = LocalTime.of(1, 0);
		
		for(int i=0; i<count;i++) {
			
			vehiclePassRecordBuilder.sensorDataRecordStart(new SensorDataRecord(Sensor.A, currentTime))
				.sensorDataRecordEnd(new SensorDataRecord(Sensor.A, currentTime.plus(10, ChronoUnit.SECONDS)));
			
			vehiclePassRecords.add( vehiclePassRecordBuilder.build() );
			currentTime = currentTime.plus(intervalInMinutes, ChronoUnit.MINUTES);
		}
		
		return vehiclePassRecords.stream();
	}
	
	protected Stream<VehiclePassRecord> createMorningAndEveningRecords(int count) {
		
		List<VehiclePassRecord> records = new ArrayList<>();
		for(int i=0;i<count;i++) {
			
			this.vehiclePassRecordBuilder.sensorDataRecordStart(new SensorDataRecord(Sensor.A, LocalTime.of(1, 10, i*2, 0)))
				.sensorDataRecordEnd(new SensorDataRecord(Sensor.A, LocalTime.of(1, 10, i*2, 157894737)));
		
			 records.add( this.vehiclePassRecordBuilder.build());
			 
			 this.vehiclePassRecordBuilder.sensorDataRecordStart(new SensorDataRecord(Sensor.A, LocalTime.of(21, 10, i*4, 0)))
				.sensorDataRecordEnd(new SensorDataRecord(Sensor.A, LocalTime.of(21, 10, i*4, 100000000)));
				
			 records.add(this.vehiclePassRecordBuilder.build());
		}
		return records.stream();
	}
}
