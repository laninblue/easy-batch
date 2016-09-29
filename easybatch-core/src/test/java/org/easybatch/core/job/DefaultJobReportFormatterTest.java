package org.easybatch.core.job;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import static org.easybatch.core.util.Utils.LINE_SEPARATOR;

public class DefaultJobReportFormatterTest {

    private JobReport report;

    private DefaultJobReportFormatter reportFormatter;

    @Before
    public void setUp() throws Exception {
        JobParameters parameters = new JobParameters();
        JobMetrics metrics = new JobMetrics();
        report = new JobReport();
        report.setParameters(parameters);
        report.setMetrics(metrics);
        report.setJobName("job");
        report.setStatus(JobStatus.COMPLETED);
        reportFormatter = new DefaultJobReportFormatter();
    }

    @Test
    public void testFormatReport() throws Exception {
        String expectedReport = 
                "Job Report:" + LINE_SEPARATOR +
                "===========" + LINE_SEPARATOR +
                "Name: job" + LINE_SEPARATOR +
                "Status: COMPLETED" + LINE_SEPARATOR +
                "Parameters:" + LINE_SEPARATOR +
                "\tBatch size = 100" + LINE_SEPARATOR +
                "\tError threshold = N/A" + LINE_SEPARATOR +
                "\tJmx monitoring = false" + LINE_SEPARATOR +
                "Metrics:" + LINE_SEPARATOR +
                "\tStart time = 1970-01-01 01:00:00" + LINE_SEPARATOR +
                "\tEnd time = 1970-01-01 01:00:00" + LINE_SEPARATOR +
                "\tDuration = 0hr 0min 0sec 0ms" + LINE_SEPARATOR +
                "\tRead count = 0" + LINE_SEPARATOR +
                "\tWrite count = 0" + LINE_SEPARATOR +
                "\tFiltered count = 0" + LINE_SEPARATOR +
                "\tError count = 0";

        String formattedReport = reportFormatter.formatReport(report);

        Assertions.assertThat(formattedReport).isEqualTo(expectedReport);
    }
}