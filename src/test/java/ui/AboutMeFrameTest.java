package ui;

import com.vnaskos.pdfguru.ui.AboutMeFrame;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AboutMeFrameTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;

    @Override
    protected void onSetUp() {
        AboutMeFrame aboutMeForm = GuiActionRunner.execute(AboutMeFrame::display);
        window = new FrameFixture(robot(), aboutMeForm);
        window.show();
    }

    @Test
    public void shouldDisplayMyNameWhenWindowIsVisible() {
        window.requireVisible();
        assertThat(window.textBox("infoPane").text()).contains("vnaskos");
    }
}