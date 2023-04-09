package com.dehidehidehi.twitchtagcarousel.swing.panel;
import com.dehidehidehi.twitchtagcarousel.service.TagCarouselService;
import com.dehidehidehi.twitchtagcarousel.swing.frame.MandatoryTagsFrame;

import javax.swing.*;
import java.awt.*;

public class CommandCenterPanel extends JPanel {

    private JButton setMandatoryTagsButton;
    private JButton setRotatingTagsButton;
    private JRadioButton autoFetchPopularTagsRadioButton;

    private final TagCarouselService tagCarouselService;

    public CommandCenterPanel(final TagCarouselService tagCarouselService) {
        this.tagCarouselService = tagCarouselService;
        setLayout(new GridLayout(2, 2));
        setUpMandatoryTagsButton();
        setUpRotatingTagsButton();
//        setUpAutoFetchPopularTagsRadioButton();
    }

    private void setUpAutoFetchPopularTagsRadioButton() {
        autoFetchPopularTagsRadioButton = new JRadioButton("Auto-fetch popular tags instead");
        autoFetchPopularTagsRadioButton.setEnabled(false);
        autoFetchPopularTagsRadioButton.addActionListener(e -> {
            setRotatingTagsButton.setEnabled(!autoFetchPopularTagsRadioButton.isSelected());
        });
        add(autoFetchPopularTagsRadioButton);
    }

    private void setUpRotatingTagsButton() {
        setRotatingTagsButton = new JButton("Set rotating tags");
        setRotatingTagsButton.setEnabled(false);
        setRotatingTagsButton.addActionListener(e -> {
            // TODO: Implement set rotating tags functionality
        });
        add(setRotatingTagsButton);
    }

    private void setUpMandatoryTagsButton() {
        setMandatoryTagsButton = new JButton("Set mandatory tags");
        setMandatoryTagsButton.addActionListener(e -> {
            final MandatoryTagsFrame mandatoryTagsFrame = new MandatoryTagsFrame(tagCarouselService);
            mandatoryTagsFrame.setVisible(true);
        });
        add(setMandatoryTagsButton);
    }
    

}
