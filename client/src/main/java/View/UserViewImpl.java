package View;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.RoundedBalloonStyle;
import net.java.balloontip.utils.TimingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class UserViewImpl extends AbstractCrudView implements UserView {
    private JButton btnPlant = new JButton("CRUD plants");
    private JButton btnGarden = new JButton("View Live Garden");
    private JButton btnRequest = new JButton("View Requests");
    private JButton btnStanding = new JButton("View Standing for Requests");

    public UserViewImpl() {
        panel3.add(btnPlant);
        panel3.add(btnGarden);
        panel3.add(btnRequest);
        panel3.add(btnStanding);
    }

    @Override
    public void addPlantButtonListener(ActionListener mal) {
        btnPlant.addActionListener(mal);
    }

    @Override
    public void addGardenButtonListener(ActionListener mal) {
        btnGarden.addActionListener(mal);
    }

    @Override
    public void addRequestButtonListener(ActionListener mal) {
        btnRequest.addActionListener(mal);
    }

    @Override
    public void addStandingButtonListener(ActionListener mal) {
        btnStanding.addActionListener(mal);
    }

    @Override
    public void showTooltip(String msg) {
        BalloonTip balloonTip = new BalloonTip(
                btnGarden,
                new JLabel(msg),
                new RoundedBalloonStyle(5, 5, Color.WHITE, Color.BLACK),
                BalloonTip.Orientation.LEFT_BELOW,
                BalloonTip.AttachLocation.ALIGNED,
                15,
                25,
                true);
        TimingUtils.showTimedBalloon(balloonTip, 3000);
    }
}
