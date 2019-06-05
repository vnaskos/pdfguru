package com.vnaskos.pdfguru.ui;

import com.vnaskos.pdfguru.PDFGuru;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.swing.finder.WindowFinder.findFrame;

public class PDFGuruTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private PDFGuru pdfGuruFrame;

    @Override
    protected void onSetUp() {
        pdfGuruFrame = GuiActionRunner.execute(PDFGuru::new);
        window = new FrameFixture(robot(), pdfGuruFrame);
        window.show();
    }

    @Test
    public void shouldOpenAboutMeFrameWhenAboutButtonIsClicked() {
        window.button("aboutButton").click();
        FrameFixture aboutFrame = findFrame(AboutMeFrame.class)
                .withTimeout(2, TimeUnit.SECONDS)
                .using(robot());
        aboutFrame.requireVisible();
    }

}