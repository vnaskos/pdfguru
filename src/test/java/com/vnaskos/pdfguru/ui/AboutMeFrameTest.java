package com.vnaskos.pdfguru.ui;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AboutMeFrameTest {

    private FrameFixture window;

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Before
    public void setUp() {
        AboutMeFrame aboutMeForm = GuiActionRunner.execute(AboutMeFrame::display);
        window = new FrameFixture(aboutMeForm);
        window.show();
    }

    @Test
    public void shouldDisplayMyNameWhenWindowIsVisible() {
        window.requireVisible();
        assertThat(window.textBox("infoPane").text()).contains("vnaskos");
    }

    @After
    public void tearDown() {
        window.cleanUp();
    }
}