package com.challenges.vehicle_survey.processer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.challenges.vehicle_survey.data_reader.SensorDataReader;
import com.challenges.vehicle_survey.data_reader.SensorDataReader.Sensor;
import com.challenges.vehicle_survey.data_reader.SensorDataReader.SensorDataRecord;
import com.challenges.vehicle_survey.processor.RoadDirection;
import com.challenges.vehicle_survey.processor.SensorDataProcessor;
import com.challenges.vehicle_survey.processor.VehiclePassCollector;
import com.challenges.vehicle_survey.processor.VehiclePassRecord;
import com.challenges.vehicle_survey.processor.VehiclePassRecord.VehiclePassRecordBuilder;

@RunWith(MockitoJUnitRunner.class)
public class SensorDataProcessorTest {

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();
	private SensorDataProcessor<SensorDataRecord, VehiclePassRecord> sensorDataProcesser;
	@Mock
	private SensorDataReader<SensorDataRecord> sensorDataReader;
	private List<SensorDataRecord> sensorDataRecordList = new ArrayList<>();
	@Mock
	private VehiclePassCollector<VehiclePassRecord> vehiclePassCollector;
	
	private VehiclePassRecordBuilder vehiclePassRecordBuilder = new VehiclePassRecordBuilder();
	private List<VehiclePassRecord> processedRecords = new ArrayList<>();
	
	@Before
	public void setUp() throws Exception {
		
		this.sensorDataProcesser = new SensorDataProcessor<>(vehiclePassCollector, vehiclePassRecordBuilder);
		Mockito.when( this.sensorDataReader.stream() ).thenReturn( this.sensorDataRecordList.stream() );
		
		Mockito.doAnswer( new MockedVehiclePassCollector() ).when( this.vehiclePassCollector ).accept(Mockito.any());		
	}

	@Test
	public void whenNullReaderPassedThenExceptionThrown() {
		
		this.expectedException.expect( NullPointerException.class );
		this.sensorDataProcesser.processSensorData(null);
	}
	/*@Test
	public void whenNullReadingDayPassedThenExceptionThrown() {
		
		this.expectedException.expect( NullPointerException.class );
		this.sensorDataProcesser.processSensorData(null, this.sensorDataReader);
	}*/
	
	@Test
	public void when3SuccessiveDataRecordsFromSensorAPassedThenExceptionThrown() {
		
		this.expectedException.expect( RuntimeException.class );
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.A, LocalTime.now()) );
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.A, LocalTime.now().plus(30, ChronoUnit.MILLIS)) );
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.A, LocalTime.now().plus(60, ChronoUnit.MILLIS)) );
		
		this.sensorDataProcesser.processSensorData(sensorDataReader);
	}
	
	@Test
	public void when2SuccessiveBRecordsThenExceptionThrown() {
		
		this.expectedException.expect( RuntimeException.class );
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.A, LocalTime.now()) );
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.B, LocalTime.now().plus(30, ChronoUnit.MILLIS)) );
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.B, LocalTime.now().plus(60, ChronoUnit.MILLIS)) );
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.A, LocalTime.now().plus(90, ChronoUnit.MILLIS)) );
		
		this.sensorDataProcesser.processSensorData(sensorDataReader);
	}
	
	@Test
	public void whenOrderOfSensorRecordsAreNotExpectedThenExceptionThrown() {
		
		this.expectedException.expect( RuntimeException.class );
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.B, LocalTime.now()) );
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.A, LocalTime.now().plus(30, ChronoUnit.MILLIS)) );
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.B, LocalTime.now().plus(60, ChronoUnit.MILLIS)) );
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.A, LocalTime.now().plus(90, ChronoUnit.MILLIS)) );
		
		this.sensorDataProcesser.processSensorData(sensorDataReader);
	}
	
	@Test
	public void whenRecordTimeStartsToDecreaseThenStartReadingDay2Records() {
		
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.A, LocalTime.now()) );
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.A, LocalTime.now().plus(30, ChronoUnit.MILLIS)) );
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.A, LocalTime.now().minus(15, ChronoUnit.MINUTES)) );
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.A, LocalTime.now().minus(14, ChronoUnit.MINUTES)) );
		
		this.sensorDataProcesser.processSensorData(sensorDataReader);
		
		assertThat(this.processedRecords.get(0).getReadingDay(), equalTo(1));
		assertThat(this.processedRecords.get(1).getReadingDay(), equalTo(2));
	}
	
	@Test
	public void when2SuccessiveSensorARecordsPassedThenReturnPassRecordWithDirectionNorthbound() {
		
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.A, LocalTime.now()) );
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.A, LocalTime.now().plus(30, ChronoUnit.MILLIS)) );
		
		this.sensorDataProcesser.processSensorData(sensorDataReader);
		
		assertThat(this.processedRecords.get(0).getDirection(), equalTo(RoadDirection.Northbound));
	}
	@Test
	public void when2SensorsRecordsPassedWithIncreasingPassTimeThenReturnPassRecordWithDirectionSouthbound() {
		
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.A, LocalTime.now()) );
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.B, LocalTime.now().plus(30, ChronoUnit.MILLIS)) );
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.A, LocalTime.now().plus(60, ChronoUnit.MILLIS)) );
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.B, LocalTime.now().plus(90, ChronoUnit.MILLIS)) );
		
		this.sensorDataProcesser.processSensorData(sensorDataReader);
		
		assertThat(this.processedRecords.get(0).getDirection(), equalTo(RoadDirection.Southbound));
	}
	
	@Test
	public void when2RecordsFromSensorAPassedThenProcessedRecordHasTheTimeOfFirstRecordAsPassStartTimeAndTheTimeOfSecondRecordAsPassEndTime() {
		
		LocalTime record1Time = LocalTime.now();
		LocalTime record2Time = LocalTime.now().plus(30, ChronoUnit.MILLIS);
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.A, record1Time) );
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.A, record2Time) );
		
		this.sensorDataProcesser.processSensorData(sensorDataReader);
		
		assertThat(this.processedRecords.get(0).getPassTimeStart(), equalTo( record1Time ));
		assertThat(this.processedRecords.get(0).getPassTimeEnd(), equalTo( record2Time ));
	}
	
	@Test
	public void when4RecordsFromSensorAandBPassedThenProcessedRecordHasTheTimeOfFirstRecordFromSensorAIsPassStartTimeAndTheTimeOfSecondRecordFromSensorAIsPassEndTime() {
		
		LocalTime record1Time = LocalTime.now();
		LocalTime record2Time = LocalTime.now().plus(60, ChronoUnit.MILLIS);
		
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.A, record1Time) );
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.B, LocalTime.now().plus(30, ChronoUnit.MILLIS)) );
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.A, record2Time) );
		this.sensorDataRecordList.add( new SensorDataRecord(Sensor.B, LocalTime.now().plus(90, ChronoUnit.MILLIS)) );
		
		
		this.sensorDataProcesser.processSensorData(sensorDataReader);
		
		assertThat(this.processedRecords.get(0).getPassTimeStart(), equalTo( record1Time ));
		assertThat(this.processedRecords.get(0).getPassTimeEnd(), equalTo( record2Time ));
	}
	
	@SuppressWarnings("rawtypes")
	private class MockedVehiclePassCollector implements Answer {

		@Override
		public String answer(InvocationOnMock invocation) throws Throwable {

			VehiclePassRecord record = (VehiclePassRecord) invocation.getArguments()[0];
			processedRecords.add(record);
			return "";
		}
	}
}
