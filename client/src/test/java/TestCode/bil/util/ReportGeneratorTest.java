package TestCode.bil.util;

import Controller.util.PdfMaker;
import Controller.util.FileMaker;
import Controller.util.ReportGenerator;
import Controller.util.TxtMaker;
import Controller.util.ReportType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.TestCase.assertTrue;

@RunWith(MockitoJUnitRunner.class)
/**
 * This class tests only the Factory Pattern
 */
public class ReportGeneratorTest {
    private ReportGenerator reportGenerator;

    @Before
    public void setUp() throws Exception {
        reportGenerator = new ReportGenerator();
    }

    @Test
    public void testGenerateReportTxt() {
        //empty constructor must not do any file specific action
        FileMaker fileMaker = reportGenerator.generateReport(ReportType.TXT);

        assertTrue("correct instance type", fileMaker instanceof TxtMaker);
    }

    @Test
    public void testGenerateReportPdf() {
        //empty constructor must not do any file specific action
        FileMaker fileMaker = reportGenerator.generateReport(ReportType.PDF);

        assertTrue("correct instance type", fileMaker instanceof PdfMaker);
    }
}