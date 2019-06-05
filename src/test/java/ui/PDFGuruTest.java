package ui;

import com.vnaskos.pdfguru.PDFGuru;
import com.vnaskos.pdfguru.ui.AboutMeFrame;
import org.assertj.swing.core.BasicComponentFinder;
import org.assertj.swing.core.ComponentFinder;
import org.assertj.swing.core.ComponentMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
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
        FrameFixture aboutFrame = WindowFinder.findFrame(AboutMeFrame.class)
                .withTimeout(5, TimeUnit.SECONDS)
                .using(robot());
        aboutFrame.requireVisible();
    }

    @Test
    public void shouldOpenFileChooserWhenAddButtonIsClicked() {
        window.button("addButton").click();

        ComponentFinder finder = BasicComponentFinder.finderWithCurrentAwtHierarchy();
        finder.find(component -> component instanceof JFileChooser).isVisible();
    }


}