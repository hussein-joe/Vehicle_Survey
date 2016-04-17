package com.challenges.vehicle_survey.analysis;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import java.time.LocalTime;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.challenges.vehicle_survey.analysis.AnalysisRequest;
import com.challenges.vehicle_survey.analysis.CountVehiclesCommand;
import com.challenges.vehicle_survey.analysis.VehicleAnalysisCommand;
import com.challenges.vehicle_survey.analysis.VehicleAnalysisCommandFactory;
import com.challenges.vehicle_survey.analysis.VehicleDistancesCommand;
import com.challenges.vehicle_survey.analysis.VehicleSpeedCommand;
import com.challenges.vehicle_survey.analysis.AnalysisRequest.AnalysisFeature;
import com.challenges.vehicle_survey.data_reader.SensorDataReader.Sensor;
import com.challenges.vehicle_survey.data_reader.SensorDataReader.SensorDataRecord;
import com.challenges.vehicle_survey.processor.RoadDirection;
import com.challenges.vehicle_survey.processor.VehiclePassRecord;

public class VehicleAnalysisFactoryTest extends AnalysisTestCommon {

	private VehicleAnalysisCommandFactory<VehiclePassRecord> vehicleAnalysisFactory;
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setUp() throws Exception {
		
		this.vehicleAnalysisFactory = new VehicleAnalysisCommandFactory<>();
		this.vehiclePassRecordBuilder.sensorDataRecordEnd(new SensorDataRecord(Sensor.A, LocalTime.of(1, 0)))
			.sensorDataRecordStart(new SensorDataRecord(Sensor.A, LocalTime.of(1, 2))).build();
	}

	@Test
	public void whenCreateCommandWithNullAnalysisRequestThenExceptionIsThrown() {
		
		expectedException.expect( NullPointerException.class );
		AnalysisRequest analysisRequest = null;
		
		this.vehicleAnalysisFactory.createCommand( analysisRequest );
	}

	@Test
	public void whenCreateCommandWithAnalysisRequestHasNullRoadDirectionThenExceptionIsThrown() {
		
		expectedException.expect( RuntimeException.class );
		AnalysisRequest analysisRequest = new AnalysisRequest(AnalysisFeature.COUNT, null, 10);
		
		this.vehicleAnalysisFactory.createCommand( analysisRequest );
	}
	
	@Test
	public void whenCreateCommandWithAnalysisRequestHasNullAnalysisFeatureThenExceptionIsThrown() {
		
		expectedException.expect( RuntimeException.class );
		AnalysisRequest analysisRequest = new AnalysisRequest(null, RoadDirection.Northbound, 10);
		
		this.vehicleAnalysisFactory.createCommand( analysisRequest );
	}
	
	@Test
	public void whenCreateCommandWithAnalysisRequestHasLessThanZeroAsPeriodMinutesThenExceptionIsThrown() {
		
		expectedException.expect( RuntimeException.class );
		AnalysisRequest analysisRequest = new AnalysisRequest(AnalysisFeature.COUNT, RoadDirection.Northbound, -1);
		
		this.vehicleAnalysisFactory.createCommand( analysisRequest );
	}
	
	@Test
	public void whenCreateCommandWithInvalidAnalysisRequestThenExceptionIsThrown() {
		
		expectedException.expect( RuntimeException.class );
		AnalysisRequest analysisRequest = new AnalysisRequest(null, null, -1);
		
		this.vehicleAnalysisFactory.createCommand( analysisRequest );
	}
	
	@Test
	public void whenCreateCommandWithFeatureCountThenInstanceOfCountVehicleCommandIsReturned() {
		
		AnalysisRequest analysisRequest = new AnalysisRequest(AnalysisFeature.COUNT, RoadDirection.Northbound, 10);
		
		VehicleAnalysisCommand<VehiclePassRecord> command = this.vehicleAnalysisFactory.createCommand( analysisRequest );
		assertThat(command, instanceOf(CountVehiclesCommand.class));
	}
	
	@Test
	public void whenCreateCommandWithFeatureSpeedThenInstanceOfVehicleSpeedCommandIsReturned() {
		
		AnalysisRequest analysisRequest = new AnalysisRequest(AnalysisFeature.SPEED, RoadDirection.Northbound, 10);
		
		VehicleAnalysisCommand<VehiclePassRecord> command = this.vehicleAnalysisFactory.createCommand( analysisRequest );
		assertThat(command, instanceOf(VehicleSpeedCommand.class));
	}
	@Test
	public void whenCreateCommandWithFeatureDistanceThenInstanceOfVehicleDistancesCommandIsReturned() {
		
		AnalysisRequest analysisRequest = new AnalysisRequest(AnalysisFeature.DISTANCE, RoadDirection.Northbound, 10);
		
		VehicleAnalysisCommand<VehiclePassRecord> command = this.vehicleAnalysisFactory.createCommand( analysisRequest );
		assertThat(command, instanceOf(VehicleDistancesCommand.class));
	}
	
	@Test
	public void whenGenerateFilterWithDirectionNorthboundThenReturnedPredicateMatchesOnlyNothboundRecords() {
		
		AnalysisRequest analysisRequest = new AnalysisRequest(AnalysisFeature.DISTANCE, RoadDirection.Northbound, 10);
		Predicate<VehiclePassRecord> predicate = this.vehicleAnalysisFactory.generateCommandFilter(analysisRequest);
		
		VehiclePassRecord vehiclePassRecord1 = this.vehiclePassRecordBuilder.direction( RoadDirection.Northbound ).build();
		VehiclePassRecord vehiclePassRecord2 = this.vehiclePassRecordBuilder.direction( RoadDirection.Southbound ).build();
		
		Assert.assertTrue(predicate.test(vehiclePassRecord1));
		Assert.assertFalse(predicate.test(vehiclePassRecord2));
	}
	
	@Test
	public void whenGenerateFilterForDay1OnlyThenReturnedPredicateMatchesOnlyDay1Records() {
		
		AnalysisRequest analysisRequest = new AnalysisRequest(AnalysisFeature.DISTANCE, RoadDirection.Northbound, 10, 1);
		Predicate<VehiclePassRecord> predicate = this.vehicleAnalysisFactory.generateCommandFilter(analysisRequest);
		
		VehiclePassRecord vehiclePassRecord1 = this.vehiclePassRecordBuilder.direction( RoadDirection.Northbound ).readingDay(1).build();
		VehiclePassRecord vehiclePassRecord2 = this.vehiclePassRecordBuilder.direction( RoadDirection.Northbound ).readingDay(2).build();
		
		Assert.assertTrue(predicate.test(vehiclePassRecord1));
		Assert.assertFalse(predicate.test(vehiclePassRecord2));
	}
}
