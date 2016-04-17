package com.challenges.vehicle_survey.processer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.time.LocalTime;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.challenges.vehicle_survey.data_reader.SensorDataReader.Sensor;
import com.challenges.vehicle_survey.data_reader.SensorDataReader.SensorDataRecord;
import com.challenges.vehicle_survey.processor.RoadDirection;
import com.challenges.vehicle_survey.processor.VehiclePassRecord;
import com.challenges.vehicle_survey.processor.VehiclePassRecord.VehiclePassRecordBuilder;

public class VehiclePassRecordBuilderTest {

	private VehiclePassRecordBuilder builder;
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setUp() throws Exception {
		this.builder = new VehiclePassRecordBuilder();
	}

	@Test
	public void whenDirectionIsNullThenExceptionThrown() {
		
		this.expectedException.expect( NullPointerException.class );
		this.builder.direction(null);
	}

	@Test
	public void whenReadingDayIsNegativeNumberThenExceptionThrown() {
		
		this.expectedException.expect( RuntimeException.class );
		this.builder.readingDay(-1);
	}
	
	@Test
	public void whenSensorDataRecordStartIsNullThenExceptionThrown() {
		
		this.expectedException.expect( NullPointerException.class );
		this.builder.sensorDataRecordStart(null);
	}
	
	@Test
	public void whenSensorDataRecordEndIsNullThenExceptionThrown() {
		
		this.expectedException.expect( NullPointerException.class );
		this.builder.sensorDataRecordEnd(null);
	}
	
	@Test
	public void givenDirectionIsNullWhenBuildThenExceptionThrown() {
		
		this.expectedException.expect( RuntimeException.class );
		this.builder.build();
	}
	
	@Test
	public void givenReadingDayIsNullWhenBuildThenExceptionThrown() {
		
		this.expectedException.expect( RuntimeException.class );
		this.builder.direction( RoadDirection.Northbound )
			.sensorDataRecordStart(new SensorDataRecord(Sensor.A, LocalTime.now()))
			.sensorDataRecordEnd( new SensorDataRecord(Sensor.A, LocalTime.now()) )
			.build();
	}
	
	@Test
	public void givenSensorStartIsNullWhenBuildThenExceptionThrown() {
		
		this.expectedException.expect( RuntimeException.class );
		this.builder.direction( RoadDirection.Northbound )
			.readingDay( 1 )
			
			.sensorDataRecordEnd( new SensorDataRecord(Sensor.A, LocalTime.now()) )
			.build();
	}
	
	@Test
	public void givenSensorEndIsNullWhenBuildThenExceptionThrown() {
		
		this.expectedException.expect( RuntimeException.class );
		this.builder.direction( RoadDirection.Northbound )
			.readingDay( 1 )
			.sensorDataRecordStart(new SensorDataRecord(Sensor.A, LocalTime.now()))
			
			.build();
	}
	
	@Test
	public void whenBuilderCompletelyInitializedThenObjectCreated() {
		
		VehiclePassRecord inst = this.builder.direction( RoadDirection.Northbound )
			.readingDay( 1 )
			.sensorDataRecordStart(new SensorDataRecord(Sensor.A, LocalTime.now()))
			.sensorDataRecordEnd( new SensorDataRecord(Sensor.A, LocalTime.now()) )
			.build();
		
		assertThat(inst, notNullValue());
	}
	
	@Test
	public void whenBuilderCompletelyInitializedThenReturnedInstanceHasSpeedCalculated() {
		
		LocalTime startTime = LocalTime.ofNanoOfDay( 98186l * 1000000 );
		LocalTime endTime = LocalTime.ofNanoOfDay( 98333l * 1000000 );
		
		VehiclePassRecord inst = this.builder.direction( RoadDirection.Northbound )
			.readingDay( 1 )
			.sensorDataRecordStart(new SensorDataRecord(Sensor.A, startTime))
			.sensorDataRecordEnd( new SensorDataRecord(Sensor.A, endTime) )
			.build();
		
		assertThat(inst, notNullValue());
		assertThat(inst.getVehicleSpeed(), is(equalTo(61)));
	}
}
