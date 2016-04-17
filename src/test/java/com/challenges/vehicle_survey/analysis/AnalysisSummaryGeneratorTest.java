package com.challenges.vehicle_survey.analysis;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AnalysisSummaryGeneratorTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private AnalysisSummaryGenerator analysisSummaryGenerator;
	private Map<Long, AnalysisResultEntry> analysisResultMap;
	
	@Before
	public void setUp() throws Exception {
		
		this.analysisSummaryGenerator = new AnalysisSummaryGenerator();
		this.analysisResultMap = new HashMap<>();		
		IntStream.range(1, 5).forEach( p-> analysisResultMap.put(10L*p, new AnalysisResultEntry(p*15.5)));
	}

	@Test
	public void whenSummaryStatisticsCalledWithNullValueThenNullPointerExceptionIsThrown() {
		
		this.expectedException.expect( NullPointerException.class );
		this.analysisSummaryGenerator.summaryStatistics( null );
	}
	
	@Test
	public void whenSummaryStatisticsWithEmptyMapThenEmptyAnalysisSummaryIsReturned() {
		
		AnalysisSummary<AnalysisResultEntry> analysisSummary = this.analysisSummaryGenerator.summaryStatistics( new HashMap<>() );
		
		assertThat(analysisSummary, Matchers.notNullValue());
		assertThat(analysisSummary.getMax(), Matchers.notNullValue());
		assertThat(analysisSummary.getMax().getRecordKey(), Matchers.equalTo(0L));
		assertThat(analysisSummary.getMax().getRecordValue(), Matchers.nullValue());
		
		assertThat(analysisSummary.getMin(), Matchers.notNullValue());
		assertThat(analysisSummary.getMin().getRecordKey(), Matchers.equalTo(0L));
		assertThat(analysisSummary.getMin().getRecordValue(), Matchers.nullValue());
	}
	
	@Test
	public void SummaryStatisticsWithPopulatedMapThenMaxAndMinValuesAreReturned() {
		
		AnalysisResultEntry maxAnalysisResultEntry = new AnalysisResultEntry(Long.MAX_VALUE);
		AnalysisResultEntry minAnalysisResultEntry = new AnalysisResultEntry(Long.MIN_VALUE);
		
		this.analysisResultMap.put(999999999L, maxAnalysisResultEntry);
		this.analysisResultMap.put(100000000L, minAnalysisResultEntry);
		
		AnalysisSummary<AnalysisResultEntry> analysisSummary = this.analysisSummaryGenerator.summaryStatistics( this.analysisResultMap );
		
		assertThat(analysisSummary.getMax().getRecordValue(), Matchers.equalTo(maxAnalysisResultEntry));
		assertThat(analysisSummary.getMin().getRecordValue(), Matchers.equalTo(minAnalysisResultEntry));
	}	
}
