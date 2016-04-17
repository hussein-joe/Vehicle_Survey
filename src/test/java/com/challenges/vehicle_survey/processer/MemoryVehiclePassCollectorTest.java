package com.challenges.vehicle_survey.processer;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.challenges.vehicle_survey.data_reader.SensorDataReader.Sensor;
import com.challenges.vehicle_survey.data_reader.SensorDataReader.SensorDataRecord;
import com.challenges.vehicle_survey.processor.MemoryVehiclePassCollector;
import com.challenges.vehicle_survey.processor.RoadDirection;
import com.challenges.vehicle_survey.processor.VehiclePassRecord;
import com.challenges.vehicle_survey.processor.VehiclePassRecord.VehiclePassRecordBuilder;

@RunWith(MockitoJUnitRunner.class)
public class MemoryVehiclePassCollectorTest {

	private MemoryVehiclePassCollector<VehiclePassRecord> memoryVehiclePassCollector;
	
	private VehiclePassRecordBuilder vehiclePassRecordBuilder = new VehiclePassRecordBuilder();
	@Rule
	public final ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setUp() throws Exception {
		
		this.memoryVehiclePassCollector = new MemoryVehiclePassCollector<>();
	}

	@Test
	public void whenPassedRecordIsNullTheNullPointerExceptionThrown() {
		
		this.expectedException.expect( NullPointerException.class );
		this.memoryVehiclePassCollector.accept(null);
	}

	@Test
	public void when2PassedRecordsThenTheReturnedStreamContainsTheSameObjectsInTheSameOrder() {
		
		this.vehiclePassRecordBuilder.direction(RoadDirection.Northbound).readingDay(1)
				.sensorDataRecordStart(new SensorDataRecord(Sensor.A, LocalTime.now()));
		this.vehiclePassRecordBuilder.sensorDataRecordEnd( new SensorDataRecord(Sensor.A, LocalTime.now()));
		
		VehiclePassRecord record1 = vehiclePassRecordBuilder.build();
		VehiclePassRecord record2 = vehiclePassRecordBuilder.build();
		this.memoryVehiclePassCollector.accept( record1 );
		this.memoryVehiclePassCollector.accept( record2 );
		
		List<VehiclePassRecord> storedRecordList = this.memoryVehiclePassCollector.stream().collect( Collectors.toList() );
		MatcherAssert.assertThat(storedRecordList, Matchers.hasSize(2));
		MatcherAssert.assertThat(storedRecordList.get(0), Matchers.equalTo(record1));
		MatcherAssert.assertThat(storedRecordList.get(1), Matchers.equalTo(record2));
	}
	
	@Test
	public void when1RecordPassedThenTheReturnedStreamContainsARecordWithTheSameValues() {
		
		this.vehiclePassRecordBuilder.direction(RoadDirection.Northbound).readingDay(1)
				.sensorDataRecordStart(new SensorDataRecord(Sensor.A, LocalTime.now()));
		this.vehiclePassRecordBuilder.sensorDataRecordEnd( new SensorDataRecord(Sensor.A, LocalTime.now()));
		
		VehiclePassRecord record1 = vehiclePassRecordBuilder.build();
		this.memoryVehiclePassCollector.accept( record1 );
		
		List<VehiclePassRecord> storedRecordList = this.memoryVehiclePassCollector.stream().collect( Collectors.toList() );
		
		VehiclePassRecord storedRecord = storedRecordList.get(0); 
		MatcherAssert.assertThat(storedRecord.getDirection(), Matchers.equalTo(record1.getDirection()));
		MatcherAssert.assertThat(storedRecord.getPassTimeEnd(), Matchers.equalTo(record1.getPassTimeEnd()));
		MatcherAssert.assertThat(storedRecord.getPassTimeStart(), Matchers.equalTo(record1.getPassTimeStart()));
		MatcherAssert.assertThat(storedRecord.getReadingDay(), Matchers.equalTo(record1.getReadingDay()));
		MatcherAssert.assertThat(storedRecord.getVehicleSpeed(), Matchers.equalTo(record1.getVehicleSpeed()));
	}
}
