package View;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.RoundedBalloonStyle;
import net.java.balloontip.utils.TimingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PlantViewImpl extends AbstractCrudView implements PlantView {

    private JButton btnReportTxt = new JButton("Generate Report TXT");
    private JButton btnReportPdf = new JButton("Generate Report PDF");

    public PlantViewImpl() {
        super.panel3.add(btnReportTxt);
        super.panel3.add(btnReportPdf);
    }

    @Override
    public void showTooltip(String msg) {
        BalloonTip balloonTip = new BalloonTip(
                btnReportPdf,
                new JLabel(msg),
                new RoundedBalloonStyle(5, 5, Color.WHITE, Color.BLACK),
                BalloonTip.Orientation.LEFT_BELOW,
                BalloonTip.AttachLocation.ALIGNED,
                15,
                25,
                true);
        TimingUtils.showTimedBalloon(balloonTip, 3000);
    }

    @Override
    public void addTxtReportButtonListener(ActionListener mal) {
        btnReportTxt.addActionListener(mal);
    }

    @Override
    public void addPdfReportButtonListener(ActionListener mal) {
        btnReportPdf.addActionListener(mal);
    }
}
