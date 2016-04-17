package com.challenges.vehicle_survey.analysis;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalTime;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import com.challenges.vehicle_survey.analysis.AnalysisResultEntry;
import com.challenges.vehicle_survey.analysis.VehicleSpeedCommand;
import com.challenges.vehicle_survey.data_reader.SensorDataReader.Sensor;
import com.challenges.vehicle_survey.data_reader.SensorDataReader.SensorDataRecord;
import com.challenges.vehicle_survey.processor.VehiclePassRecord;

public class VehicleSpeedCommandTest extends AnalysisTestCommon {

	private VehicleSpeedCommand<VehiclePassRecord> vehicleSpeedCommand;
	
	@Before
	public void setUp() throws Exception {
		
		this.vehicleSpeedCommand = new VehicleSpeedCommand<>(null);
	}

	@Test
	public void whenExecuteCommandWithNullStreamPassedThenThrowException() {
		
		this.expectedException.expect( NullPointerException.class );
		this.vehicleSpeedCommand.execute( null );
	}
	
	@Test
	public void whenEmptyStreamIsPassedThenAverageSpeedIsZero() {
		
		AnalysisResultEntry vehicleSpeedResult = this.vehicleSpeedCommand.execute(Stream.empty());
		assertThat(vehicleSpeedResult.getResultEntry(), equalTo(0.0));
	}

	@Test
	public void whenOnlyOneCarWithSpeed57ThenAverageSpeedIs57() {
		
		this.vehiclePassRecordBuilder.sensorDataRecordStart(new SensorDataRecord(Sensor.A, LocalTime.of(1, 0, 0, 0)))
			.sensorDataRecordEnd(new SensorDataRecord(Sensor.A, LocalTime.of(1, 0, 0, 157894737)));
		
		AnalysisResultEntry vehicleSpeedResult = this.vehicleSpeedCommand.execute(Stream.of( this.vehiclePassRecordBuilder.build() ));
		assertThat(vehicleSpeedResult.getResultEntry(), equalTo(57.0));
	}
	
	@Test
	public void when2CarsWithSpeed57ThenAverageSpeedIs57() {
		
		this.vehiclePassRecordBuilder.sensorDataRecordStart(new SensorDataRecord(Sensor.A, LocalTime.of(1, 0, 0, 0)))
			.sensorDataRecordEnd(new SensorDataRecord(Sensor.A, LocalTime.of(1, 0, 0, 157894737)));
		
		Stream<VehiclePassRecord> vehiclePassRecordStream = Stream.of( this.vehiclePassRecordBuilder.build(),
				this.vehiclePassRecordBuilder.sensorDataRecordStart(new SensorDataRecord(Sensor.A, LocalTime.of(1, 2, 0, 0)))
				.sensorDataRecordEnd(new SensorDataRecord(Sensor.A, LocalTime.of(1, 2, 0, 157894737))).build()
				);
		
		AnalysisResultEntry vehicleSpeedResult = this.vehicleSpeedCommand.execute( vehiclePassRecordStream );
		assertThat(vehicleSpeedResult.getResultEntry(), equalTo(57.0));
	}
	
	@Test
	public void givenMorningAnalysisIsRequiredWhen1CarWithSpeed57InTheMorningAndAnotherOneWithSpeed90InTheEveningThenAverageSpeedIs57() {
		
		this.vehicleSpeedCommand = new VehicleSpeedCommand<>(p -> p.getPassTimeStart().isBefore(LocalTime.of(18, 00)) );
		
		
		AnalysisResultEntry vehicleSpeedResult = this.vehicleSpeedCommand.execute( this.createMorningAndEveningRecords(1) );
		assertThat(vehicleSpeedResult.getResultEntry(), equalTo(57.0));
	}
}
