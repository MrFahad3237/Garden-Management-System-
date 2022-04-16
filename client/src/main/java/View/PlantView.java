package View;

import java.awt.event.ActionListener;

public interface PlantView  extends CrudView {
    void addTxtReportButtonListener(ActionListener mal);
    void addPdfReportButtonListener(ActionListener mal);
}
