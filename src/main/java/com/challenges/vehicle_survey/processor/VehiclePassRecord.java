package com.challenges.vehicle_survey.processor;

import java.time.LocalTime;
import java.time.temporal.ChronoField;

import com.challenges.vehicle_survey.common.VehicleCommons;
import com.challenges.vehicle_survey.data_reader.SensorDataReader.SensorDataRecord;

public class VehiclePassRecord {

	private final RoadDirection direction;
	private final int readingDay;
	private final LocalTime passTimeStart;
	private final LocalTime passTimeEnd;
	private final int vehicleSpeed;
	
	private VehiclePassRecord(VehiclePassRecordBuilder builder) {
		super();
		this.direction = builder.direction;
		this.readingDay = builder.readingDay;
		this.passTimeStart = builder.vehiclePassStartTime;
		this.passTimeEnd = builder.vehiclePassEndTime;
		this.vehicleSpeed = builder.vehicleSpeed;
	}
	
	public RoadDirection getDirection() {
		return direction;
	}
	public int getReadingDay() {
		return readingDay;
	}
	public LocalTime getPassTimeStart() {
		return passTimeStart;
	}
	public LocalTime getPassTimeEnd() {
		return passTimeEnd;
	}
	public int getVehicleSpeed() {
		return vehicleSpeed;
	}
	
	@Override
	public String toString() {
		return "VehiclePassRecord [direction=" + direction + ", readingDay="
				+ readingDay + ", passTimeStart=" + passTimeStart
				+ ", passTimeEnd=" + passTimeEnd + ", vehicleSpeed="
				+ vehicleSpeed + "]";
	}



	public static class VehiclePassRecordBuilder {

		private RoadDirection direction;
		private int readingDay;
		private SensorDataRecord sensorDataRecordPassStart;
		private SensorDataRecord sensorDataRecordPassEnd;
		private int vehicleSpeed;
		private LocalTime vehiclePassStartTime;
		private LocalTime vehiclePassEndTime;
		
		public VehiclePassRecordBuilder direction(RoadDirection direction) {
			
			if ( direction == null ) {
				throw new NullPointerException("The passed direction cannot be null");
			}
			this.direction = direction;
			return this;
		}
		
		public VehiclePassRecordBuilder readingDay(int readingDay) {
			
			if ( readingDay <= 0 ) {
				throw new RuntimeException("The passed reading day is not correct");
			}
			this.readingDay = readingDay;
			return this;
		}
		
		public VehiclePassRecordBuilder sensorDataRecordStart(SensorDataRecord sensorDateRecord) {
			
			if ( sensorDateRecord == null) {
				throw new NullPointerException("The passed record cannot be null");
			}
			this.sensorDataRecordPassStart = sensorDateRecord;
			return this;
		}
		
		public VehiclePassRecordBuilder sensorDataRecordEnd(SensorDataRecord sensorDateRecord) {
			
			if ( sensorDateRecord == null) {
				throw new NullPointerException("The passed record cannot be null");
			}
			this.sensorDataRecordPassEnd = sensorDateRecord;
			return this;
		}
		
		public VehiclePassRecord build() {
			
			if ( !isValidToBuild() ) {
				
				throw new RuntimeException("The builder is not initialized properly");
			}
			this.vehiclePassStartTime = this.sensorDataRecordPassStart.getTime();
			this.vehiclePassEndTime = this.sensorDataRecordPassEnd.getTime();
			this.vehicleSpeed = VehicleCommons.calculateSpeed(this.vehiclePassStartTime.getLong(ChronoField.MILLI_OF_DAY), 
					this.vehiclePassEndTime.getLong(ChronoField.MILLI_OF_DAY));
			
			return new VehiclePassRecord(this);
		}
		
		private boolean isValidToBuild() {
			
			return this.direction != null && this.readingDay > 0 && this.sensorDataRecordPassStart != null
					&& this.sensorDataRecordPassEnd != null && this.sensorDataRecordPassStart.getTime() != null
					&& this.sensorDataRecordPassEnd.getTime() != null;
		}
	}
}
