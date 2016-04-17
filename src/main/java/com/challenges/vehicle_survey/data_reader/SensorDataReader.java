package com.challenges.vehicle_survey.data_reader;

import java.time.LocalTime;
import java.util.stream.Stream;

import com.challenges.vehicle_survey.data_reader.SensorDataReader.SensorDataRecord;

public interface SensorDataReader<T extends SensorDataRecord> {
	
	Stream<T> stream();
	
	public static class SensorDataRecord {
		
		private Sensor sensor;
		private LocalTime time;
		public SensorDataRecord(Sensor sensor, LocalTime time) {
			super();
			this.sensor = sensor;
			this.time = time;
		}
		public Sensor getSensor() {
			return sensor;
		}
		public LocalTime getTime() {
			return time;
		}
	}
	
	public static enum Sensor {
		
		A,
		B
	}
}
