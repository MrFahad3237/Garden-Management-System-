package Controller.util;

public class ReportGenerator {

    public FileMaker generateReport(ReportType reportType) {
        switch (reportType) {
            case PDF:
                return new PdfMaker();
            case TXT:
                return new TxtMaker();
            default:
                throw new IllegalArgumentException("Bad report-type");
        }
    }

}
