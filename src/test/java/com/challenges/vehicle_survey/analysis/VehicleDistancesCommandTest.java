package com.challenges.vehicle_survey.analysis;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import com.challenges.vehicle_survey.analysis.AnalysisResultEntry;
import com.challenges.vehicle_survey.analysis.VehicleDistancesCommand;
import com.challenges.vehicle_survey.data_reader.SensorDataReader.Sensor;
import com.challenges.vehicle_survey.data_reader.SensorDataReader.SensorDataRecord;
import com.challenges.vehicle_survey.processor.VehiclePassRecord;

public class VehicleDistancesCommandTest extends AnalysisTestCommon {

	private VehicleDistancesCommand<VehiclePassRecord> vehicleDistancesCommand;
	
	
	@Before
	public void setUp() throws Exception {
		
		this.vehicleDistancesCommand = new VehicleDistancesCommand<>();
		
		this.vehiclePassRecordBuilder.readingDay(1).sensorDataRecordStart(new SensorDataRecord(Sensor.A, LocalTime.of(10, 10)))
			.sensorDataRecordEnd(new SensorDataRecord(Sensor.A, LocalTime.of(10, 11)));
	}

	@Test
	public void whenExecuteCommandWithNullStreamPassedThenThrowException() {
		
		this.expectedException.expect( NullPointerException.class );
		this.vehicleDistancesCommand.execute( null );
	}

	@Test
	public void whenEmptyStreamPassedThenReturnDistanceIsZero() {
		
		Stream<VehiclePassRecord> vehiclePassRecordStream = Stream.empty();
		AnalysisResultEntry averageDistanceAnalysisResult = this.vehicleDistancesCommand.execute(vehiclePassRecordStream);
		
		assertThat(averageDistanceAnalysisResult.getResultEntry(), equalTo(0.0));
	}
	
	@Test
	public void whenStreamWithOneVehiclePassRecordThenDistanceIsZero() {
		
		Stream<VehiclePassRecord> vehiclePassRecordStream = Stream.of( this.vehiclePassRecordBuilder.build() );
		AnalysisResultEntry averageDistanceAnalysisResult = this.vehicleDistancesCommand.execute(vehiclePassRecordStream);
		
		assertThat(averageDistanceAnalysisResult.getResultEntry(), equalTo(0.0));
	}
	
	@Test
	public void givenAverageSpeedIs60When3CarsIn6MinutesThenAverageDistanceIs1333Meters() {
		
		Stream<VehiclePassRecord> vehiclePassRecordStream = super.createVehiclePassRecords(3, 2);
		AnalysisResultEntry averageDistanceAnalysisResult = this.vehicleDistancesCommand.execute( vehiclePassRecordStream );
		
		assertThat(averageDistanceAnalysisResult.getResultEntry(), equalTo(1333.33));
	}
	
	@Test
	public void givenAverageSpeedIs60AndOneCarPassedInTheEveningAndTheOther3CarsPassedInTheMorningIn6MinutesWhenMorningOnlyRequiredThenAverageDistanceIs1333Meters() {
		
		this.vehicleDistancesCommand = new VehicleDistancesCommand<>( p -> p.getPassTimeStart().isBefore(LocalTime.of(18, 00)) );
	
		this.vehiclePassRecordBuilder.sensorDataRecordStart(new SensorDataRecord(Sensor.A, LocalTime.of(21, 10, 0, 0)))
			.sensorDataRecordEnd(new SensorDataRecord(Sensor.A, LocalTime.of(21, 10, 0, 100000000)));
	
		List<VehiclePassRecord> vehiclePassRecords = super.createVehiclePassRecords(3, 2).collect( Collectors.toList() );
		vehiclePassRecords.add( this.vehiclePassRecordBuilder.build() );
		
		AnalysisResultEntry analysisResult = this.vehicleDistancesCommand.execute( vehiclePassRecords.stream() );
		Double result = (Double) analysisResult.getResultEntry();
		assertThat(result, equalTo( 1333.33 ));
	}
}
