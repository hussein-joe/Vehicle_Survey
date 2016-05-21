package com.challenges.vehicle_survey.data_reader;

import java.nio.file.Paths;
import java.time.temporal.ChronoField;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.challenges.vehicle_survey.data_reader.FileSensorDataReader;
import com.challenges.vehicle_survey.data_reader.InvalidRecordHandler;
import com.challenges.vehicle_survey.data_reader.SensorDataReader.Sensor;
import com.challenges.vehicle_survey.data_reader.SensorDataReader.SensorDataRecord;

@RunWith(MockitoJUnitRunner.class)
public class FileBasedSensorDataSourceTest {

	private FileSensorDataReader<SensorDataRecord> fileSensorDataReader;
	@Rule
	public final ExpectedException expectedException = ExpectedException.none();
	@Mock
	private InvalidRecordHandler<SensorDataRecord> invalidRecordHandler;
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void whenFileIsNullThenExceptionThrown() {
		
		expectedException.expect( RuntimeException.class );
		fileSensorDataReader = new FileSensorDataReader<>(invalidRecordHandler, null );
	}
	@Test
	public void whenFileDoesNotExistThenExceptionThrown() {

		expectedException.expect( RuntimeException.class );
		fileSensorDataReader = new FileSensorDataReader<>(invalidRecordHandler, Paths.get("FILE_DOES_NOT_EXIST"));
	}
	
	@Test
	public void whenEmptyFileThenNoRecordReturned() {
		
		fileSensorDataReader = new FileSensorDataReader<>(invalidRecordHandler, Paths.get("src/test/resources/", "empty_sensor_data_file.txt"));
		MatcherAssert.assertThat(fileSensorDataReader.stream().iterator().hasNext(), Matchers.is(false));
	}
	
	@Test
	public void whenFileWithBlankLinesOnlyThenNoRecordReturned() {
		
		fileSensorDataReader = new FileSensorDataReader<>(invalidRecordHandler, Paths.get("src/test/resources/", "blank_lines_only_sensor_data_file.txt"));
		MatcherAssert.assertThat(fileSensorDataReader.stream().iterator().hasNext(), Matchers.is(false));
	}
	
	@Test
	public void whenInvalidRecordsInTheFileThenInvalidHandlerShouldBeCalled() {
		
		fileSensorDataReader = new FileSensorDataReader<>(invalidRecordHandler, Paths.get("src/test/resources/", "invalid_records_sensor_data_file.txt"));
		Iterator<SensorDataRecord> iterator = fileSensorDataReader.stream().iterator();
		while ( iterator.hasNext() ) {
			
			iterator.next();
		}
		
		Mockito.verify( this.invalidRecordHandler, Mockito.times(5) ).handleInvalidRecord( org.mockito.Matchers.any(String.class) );
	}
	
	@Test
	public void when5ValidRecordsInTheFileThen5EntriesShouldBeReturned() {
		
		int countValidRecords = 0;
		fileSensorDataReader = new FileSensorDataReader<>(invalidRecordHandler, Paths.get("src/test/resources/", "5_valid_records_sensor_data_file.txt"));
		Iterator<SensorDataRecord> iterator = fileSensorDataReader.stream().iterator();
		while ( iterator.hasNext() ) {
			
			SensorDataRecord parsedRecord = iterator.next();
			if ( parsedRecord != null ) {
				
				countValidRecords ++;
			}
		}
		
		MatcherAssert.assertThat(countValidRecords, Matchers.is( 5 ));
		Mockito.verify( this.invalidRecordHandler, Mockito.never() ).handleInvalidRecord( org.mockito.Matchers.any(String.class) );
	}
	
	@Test
	public void whenValidRecordsThenParsedInTheSameOrderDefinedInTheFile() {
		
		int readingTimeInMillisecond = 1;
		fileSensorDataReader = new FileSensorDataReader<>(invalidRecordHandler, Paths.get("src/test/resources/", "valid_records_sensor_data_file.txt"));
		Iterator<SensorDataRecord> iterator = fileSensorDataReader.stream().iterator();
		while ( iterator.hasNext() ) {
			
			SensorDataRecord parsedDataRecord = iterator.next();
			
			MatcherAssert.assertThat(parsedDataRecord, Matchers.notNullValue());
			MatcherAssert.assertThat(parsedDataRecord.getSensor(), Matchers.equalTo( Sensor.A ));
			MatcherAssert.assertThat(parsedDataRecord.getTime().get( ChronoField.MILLI_OF_DAY ), Matchers.equalTo( readingTimeInMillisecond ));
			readingTimeInMillisecond *= 10;
		}
	}
	
	@Test
	public void whenValidRecordsThenReturnedStreamHasAllRecordsParsedInTheSameOrderDefinedInTheFile() {
		
		fileSensorDataReader = new FileSensorDataReader<>(invalidRecordHandler, Paths.get("src/test/resources/", "valid_records_sensor_data_file.txt"));
		List<SensorDataRecord> dataRecords = fileSensorDataReader.stream().collect( Collectors.toList() );
		
		MatcherAssert.assertThat(dataRecords, Matchers.hasSize(6));
	}
}
