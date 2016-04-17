package com.challenges.vehicle_survey.analysis;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.time.LocalTime;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import com.challenges.vehicle_survey.data_reader.SensorDataReader.Sensor;
import com.challenges.vehicle_survey.data_reader.SensorDataReader.SensorDataRecord;
import com.challenges.vehicle_survey.processor.RoadDirection;
import com.challenges.vehicle_survey.processor.VehiclePassRecord;
import com.challenges.vehicle_survey.processor.VehiclePassRecord.VehiclePassRecordBuilder;

public class CountVehiclesCommandTest extends AnalysisTestCommon {
	
	
	private VehiclePassRecordBuilder vehiclePassRecordBuilder;
	private CountVehiclesCommand<VehiclePassRecord> countVehiclesCommand;
	
	@Before
	public void setUp() throws Exception {
		
		this.countVehiclesCommand = new CountVehiclesCommand<>();
		this.vehiclePassRecordBuilder = new VehiclePassRecordBuilder();
		this.vehiclePassRecordBuilder.direction(RoadDirection.Northbound);
		this.vehiclePassRecordBuilder.readingDay(1).sensorDataRecordStart(new SensorDataRecord(Sensor.A, LocalTime.of(10, 10)))
			.sensorDataRecordEnd(new SensorDataRecord(Sensor.A, LocalTime.of(10, 11)));
	}
	
	@Test
	public void whenExecuteCommandWithNullStreamPassedThenThrowException() {
		
		this.expectedException.expect( NullPointerException.class );
		this.countVehiclesCommand.execute( null );
	}

	@Test
	public void givenOnlyOneRecordPassedWhenCountCarsThen1IsReturned() {
		
		Stream<VehiclePassRecord> vehiclePassRecordsStream = Stream.of( this.vehiclePassRecordBuilder.build() );
		AnalysisResultEntry analysisResult = this.countVehiclesCommand.execute(vehiclePassRecordsStream);
		
		assertThat(analysisResult, notNullValue());
		assertThat(analysisResult.getResultEntry(), equalTo(1.0));
	}
	
	@Test
	public void given2CarRecordsPassedWhenExecuteCountCommandThen2IsReturned() {
		
		
		Stream<VehiclePassRecord> vehiclePassRecordsStream = this.createVehiclePassRecords(2, 2);
		AnalysisResultEntry analysisResult = this.countVehiclesCommand.execute( vehiclePassRecordsStream );
		
		assertThat(analysisResult.getResultEntry(), equalTo(2.0));
	}
	
	@Test
	public void given2VehiclePassRecordsOneInTheMorningAndAnotherInTheEveningWhenCountExecuteCommandForMorningOnlyRequiredThenReturn1() {
		
		this.countVehiclesCommand = new CountVehiclesCommand<>( p -> p.getPassTimeStart().isBefore(LocalTime.of(18, 00)) );
		AnalysisResultEntry analysisResult = this.countVehiclesCommand.execute( this.createMorningAndEveningRecords(1) );
		assertThat(analysisResult.getResultEntry(), equalTo(1.0));
	}
}
