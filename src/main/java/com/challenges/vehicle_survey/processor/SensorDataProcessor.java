package com.challenges.vehicle_survey.processor;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

import com.challenges.vehicle_survey.data_reader.SensorDataReader;
import com.challenges.vehicle_survey.data_reader.SensorDataReader.Sensor;
import com.challenges.vehicle_survey.data_reader.SensorDataReader.SensorDataRecord;
import com.challenges.vehicle_survey.processor.VehiclePassRecord.VehiclePassRecordBuilder;


public class SensorDataProcessor<I extends SensorDataRecord, O extends VehiclePassRecord> {
	
	private VehiclePassCollector<O> collector;
	private VehiclePassRecordBuilder vehiclePassRecordBuilder;
	private LocalTime lastReadingTime = LocalTime.ofNanoOfDay(0);
	private int lastReadingDay = 1;
	
	public SensorDataProcessor(VehiclePassCollector<O> collector, VehiclePassRecordBuilder vehiclePassRecordBuilder) {
		super();
		this.collector = collector;
		this.vehiclePassRecordBuilder = vehiclePassRecordBuilder;
	}

	public void processSensorData(SensorDataReader<I> sensorDataReader) {
		
		Objects.requireNonNull(sensorDataReader, "The passed reader cannot be null");
		
		Iterator<I> sensorDataRecordIterator = sensorDataReader.stream().iterator();
		
		O vehiclePassRecord = this.readVehiclePassData(sensorDataRecordIterator);
		while ( vehiclePassRecord != null ) {
			
			collector.accept(vehiclePassRecord);
			vehiclePassRecord = this.readVehiclePassData(sensorDataRecordIterator);
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<I> splitSensorRecords(Iterator<I> sensorRecordIterator) {
		
		if ( !sensorRecordIterator.hasNext() ) {
			
			return null;
		}
		
		try {
			SensorDataRecord sensorDateRecord1 = sensorRecordIterator.next();
			SensorDataRecord sensorDateRecord2 = sensorRecordIterator.next();
			if ( sensorDateRecord1.getSensor() == sensorDateRecord2.getSensor() ) {
				
				return (List<I>) Arrays.asList(sensorDateRecord1, sensorDateRecord2);
			}
			
			return (List<I>) Arrays.asList(sensorDateRecord1, sensorDateRecord2, sensorRecordIterator.next(), sensorRecordIterator.next());
		} catch(NoSuchElementException exp) {
			
			throw new RuntimeException("The passed data records are not expected", exp);
		}
	}
	
	private boolean validateVehicleSensorRecords(List<I> vehicleRecords) {
		
		String sensorNames = vehicleRecords.stream().map(SensorDataRecord::getSensor).map(Sensor::name).collect( Collectors.joining() );
		return sensorNames.equals("AA") || sensorNames.equals("ABAB");
	}
	
	@SuppressWarnings("unchecked")
	private O readVehiclePassData(Iterator<I> sensorRecordIterator) {
		
		List<I> vehicleSensorReadings = this.splitSensorRecords(sensorRecordIterator);
		
		if ( vehicleSensorReadings == null ) {
			
			return null;
		}
		
		if ( !this.validateVehicleSensorRecords(vehicleSensorReadings) ) {
			
			throw new RuntimeException("The passed sequence " + vehicleSensorReadings + " is not expected");
		}
		
		this.vehiclePassRecordBuilder.sensorDataRecordStart( vehicleSensorReadings.get(0) );
		this.vehiclePassRecordBuilder.readingDay( this.calculateReadingDay(vehicleSensorReadings) );
		
		if ( vehicleSensorReadings.size() == 2 ) {
			
			this.vehiclePassRecordBuilder.direction( RoadDirection.Northbound );
			this.vehiclePassRecordBuilder.sensorDataRecordEnd( vehicleSensorReadings.get(1) );
		} else {
			
			this.vehiclePassRecordBuilder.direction( RoadDirection.Southbound );
			this.vehiclePassRecordBuilder.sensorDataRecordEnd( vehicleSensorReadings.get(2) );
		}
		return (O) this.vehiclePassRecordBuilder.build();
	}
	
	private int calculateReadingDay(List<I> vehicleSensorReadings) {
		
		LocalTime vehiclePassTime = vehicleSensorReadings.get( vehicleSensorReadings.size() - 1 ).getTime();
		if ( vehiclePassTime.isBefore(lastReadingTime) ) {
			
			this.lastReadingDay++;
		}
		
		this.lastReadingTime = vehiclePassTime;
		return this.lastReadingDay;
	}
}
