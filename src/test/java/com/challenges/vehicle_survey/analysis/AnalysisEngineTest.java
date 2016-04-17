package com.challenges.vehicle_survey.analysis;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.stream.Stream;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.challenges.vehicle_survey.analysis.AnalysisRequest.AnalysisFeature;
import com.challenges.vehicle_survey.analysis.AnalysisSummary.SummaryRecord;
import com.challenges.vehicle_survey.processor.RoadDirection;
import com.challenges.vehicle_survey.processor.VehiclePassRecord;

@RunWith(MockitoJUnitRunner.class)
public class AnalysisEngineTest extends AnalysisTestCommon {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	private AnalysisEngine<VehiclePassRecord> analysisEngine;
	@Mock
	private VehicleAnalysisCommandFactory<VehiclePassRecord> analysisCommandFactory;
	@Mock
	private VehicleAnalysisCommand<VehiclePassRecord> analysisCommand;
	@Mock
	private AnalysisSummaryGenerator analysisSummaryGenerator;
	
	@Before
	public void setUp() throws Exception {
		
		this.analysisEngine = new AnalysisEngine<>( this.analysisCommandFactory, analysisSummaryGenerator );
		BDDMockito.willReturn( this.analysisCommand ).given( this.analysisCommandFactory ).createCommand(Mockito.any(AnalysisRequest.class));
	}

	@Test
	public void givenPassedStreamPeriodIs30MinutesWhenAnalysisIsRequiredForPeriodOf10MinutesThenReturnMapWith3Records() {
		
		Stream<VehiclePassRecord> vehiclePassRecordStream = super.createVehiclePassRecords(3, 30);
		AnalysisRequest analysisRequest = new AnalysisRequest(AnalysisFeature.COUNT, RoadDirection.Northbound, 10);
		MapAnalysisResult<AnalysisResultEntry> analysisResult = this.analysisEngine.runAnalysisReport(vehiclePassRecordStream, analysisRequest);
		
		assertThat(analysisResult.getAnalysisResult().size(), equalTo(3));
	}
	
	@Test
	public void whenRunAnalysisReportThenSummaryStatisticsGeneratorGetsCalled() {
		
		BDDMockito.willReturn( null ).given( this.analysisSummaryGenerator ).summaryStatistics( Mockito.any() );
		Stream<VehiclePassRecord> vehiclePassRecordStream = super.createVehiclePassRecords(3, 30);
		AnalysisRequest analysisRequest = new AnalysisRequest(AnalysisFeature.COUNT, RoadDirection.Northbound, 10);
		this.analysisEngine.runAnalysisReport(vehiclePassRecordStream, analysisRequest);
		
		Mockito.verify( this.analysisSummaryGenerator, Mockito.times(1)).summaryStatistics(Mockito.any());
	}
	
	@Test
	public void whenRunAnalysisReportThenSummaryStatisticsMaxAndMinGetsPopulated() {
	
		AnalysisSummary<AnalysisResultEntry> analysisSummary = new AnalysisSummary<>(new SummaryRecord<>(10, 
				new AnalysisResultEntry(10.0)), new SummaryRecord<>(10, 
						new AnalysisResultEntry(30.0)));
		
		BDDMockito.willReturn( analysisSummary ).given( this.analysisSummaryGenerator ).summaryStatistics( Mockito.any() );
		Stream<VehiclePassRecord> vehiclePassRecordStream = super.createVehiclePassRecords(3, 30);
		AnalysisRequest analysisRequest = new AnalysisRequest(AnalysisFeature.COUNT, RoadDirection.Northbound, 10);
		AnalysisResult<?> analysisResult = this.analysisEngine.runAnalysisReport(vehiclePassRecordStream, analysisRequest);
		
		assertThat( analysisResult.getAnalysisSummary().getMax(), Matchers.notNullValue());
		assertThat( analysisResult.getAnalysisSummary().getMin(), Matchers.notNullValue());
	}
	
	@Test
	public void whenRunAnalysisReportThenReturnedReportTitleGetsGenerated() {
		
		Stream<VehiclePassRecord> vehiclePassRecordStream = super.createVehiclePassRecords(3, 30);
		AnalysisRequest analysisRequest = new AnalysisRequest(AnalysisFeature.COUNT, RoadDirection.Northbound, 10);
		AnalysisResult<?> analysisResult = this.analysisEngine.runAnalysisReport(vehiclePassRecordStream, analysisRequest);
		
		assertThat(analysisResult.getResultTitle(), Matchers.notNullValue());
		Assert.assertTrue(analysisResult.getResultTitle().trim().length() > 0);
	}
	
	@Test
	public void whenAnalysisCommandFactoryReturnsNullThenThrowException() {
		
		BDDMockito.willReturn( null ).given( this.analysisCommandFactory ).createCommand(Mockito.any(AnalysisRequest.class));
		
		this.expectedException.expect( RuntimeException.class );
		Stream<VehiclePassRecord> vehiclePassRecordStream = super.createVehiclePassRecords(3, 30);
		AnalysisRequest analysisRequest = new AnalysisRequest(AnalysisFeature.COUNT, RoadDirection.Northbound, 10);
		this.analysisEngine.runAnalysisReport(vehiclePassRecordStream, analysisRequest);
	}
	
	@Test
	public void whenEmptyStreamIsPassedThenReturnEmptyResult() {
		
		
		Stream<VehiclePassRecord> vehiclePassRecordStream = Stream.empty();
		AnalysisRequest analysisRequest = new AnalysisRequest(AnalysisFeature.COUNT, RoadDirection.Northbound, 10);
		MapAnalysisResult<AnalysisResultEntry> analysisResult = this.analysisEngine.runAnalysisReport(vehiclePassRecordStream, analysisRequest);
		
		assertThat(analysisResult.getAnalysisResult().size(), equalTo(0));
	}
}
