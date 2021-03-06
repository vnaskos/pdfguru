package ui;

import com.vnaskos.pdfguru.pdfbox.PdfboxProcessFactory;
import com.vnaskos.pdfguru.ui.AboutMeFrame;
import com.vnaskos.pdfguru.ui.PDFGuru;
import org.assertj.swing.core.BasicComponentFinder;
import org.assertj.swing.core.ComponentFinder;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.assertj.swing.timing.Pause;
import org.junit.Test;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

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
    public void shouldOpenPagesHelpDialogWhenPagesHelpButtonIsClicked() {
        window.button("pagesHelpButton").click();

        ComponentFinder finder = BasicComponentFinder.finderWithCurrentAwtHierarchy();
        finder.find(component -> component instanceof JOptionPane).isVisible();
    }

    @Test
    public void shouldOpenFileChooserWhenAddButtonIsClicked() {
        window.button("addButton").click();

        ComponentFinder finder = BasicComponentFinder.finderWithCurrentAwtHierarchy();
        finder.find(component -> component instanceof JFileChooser).isVisible();
    }

    @Test
    public void shouldOpenFileChooserWhenOuputBrowseButtonIsClicked() {
        window.button("outputBrowseButton").click();

        ComponentFinder finder = BasicComponentFinder.finderWithCurrentAwtHierarchy();
        finder.find(component -> component instanceof JFileChooser).isVisible();
    }

    @Test
    public void shouldMoveItemUpWhenUpButtonIsClicked() {
        GuiActionRunner.execute(() -> pdfGuruFrame.addElements(new File[]{
                new File("/file/1.pdf"),
                new File("/file/2.pdf")}));

        window.list("inputList").clickItem("/file/2.pdf");
        window.button("upButton").click();

        int actualIndex = window.list("inputList").item("/file/2.pdf").index();
        assertThat(actualIndex).isEqualTo(0);
    }

    @Test
    public void shouldMoveItemDownWhenDownButtonIsClicked() {
        GuiActionRunner.execute(() -> pdfGuruFrame.addElements(new File[]{
                new File("/file/1.pdf"),
                new File("/file/2.pdf")}));

        window.list("inputList").clickItem("/file/1.pdf");
        window.button("downButton").click();

        int actualIndex = window.list("inputList").item("/file/1.pdf").index();
        assertThat(actualIndex).isEqualTo(1);
    }

    @Test
    public void shouldRemoveItemWhenRemoveButtonIsClicked() {
        GuiActionRunner.execute(() -> pdfGuruFrame.addElements(new File[]{
                new File("/file/1.pdf"),
                new File("/file/2.pdf")}));

        window.list("inputList").clickItem("/file/1.pdf");
        window.button("removeButton").click();

        window.list("inputList").requireItemCount(1);
        assertThat(window.list("inputList").valueAt(0)).isEqualTo("/file/2.pdf");
    }

    @Test
    public void shouldRemoveAllItemsFromInputListWhenClearButtonIsClicked() {
        GuiActionRunner.execute(() -> pdfGuruFrame.addElements(new File[]{
                new File("/file/1.pdf"),
                new File("/file/2.pdf")}));

        window.button("clearButton").click();
        window.list("inputList").requireItemCount(0);
    }

    @Test
    public void shouldProducePdfWhenOkButtonIsClickedWithCorrectlySetParameters() throws IOException {
        pdfGuruFrame.setProcessFactory(new PdfboxProcessFactory());

        File outputDir = Files.createTempDirectory("pdfguru").toFile();
        File expectedResult = new File(outputDir + File.separator + "test_1.pdf");

        expectedResult.deleteOnExit();
        outputDir.deleteOnExit();

        GuiActionRunner.execute(() -> pdfGuruFrame.addElements(new File[]{
                new File("src/test/resources/5pages.pdf")
        }));

        window.textBox("outputFilePathField").setText(outputDir + File.separator + "test");

        window.button("okButton").click();
        Pause.pause(800);

        assertThat(expectedResult.exists()).isTrue();

        // CLEAN UP
        assertThat(expectedResult.delete()).isTrue();
        assertThat(outputDir.delete()).isTrue();
    }
}