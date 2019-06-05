package ui;

import com.vnaskos.pdfguru.execution.ExecutionControlListener;
import com.vnaskos.pdfguru.ui.OutputDialog;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.timing.Pause;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class OutputDialogTest {

    private static final int TOTAL_ITEMS_TO_PROCESS = 2;

    private final ExecutionControlListener fakeControlListener = mock(ExecutionControlListener.class);

    private FrameFixture window;
    private OutputDialog outputDialog;

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Before
    public void setUp() {
        outputDialog = GuiActionRunner.execute(() -> new OutputDialog(TOTAL_ITEMS_TO_PROCESS, fakeControlListener));
        window = new FrameFixture(outputDialog);
        window.show();
    }

    @Test
    public void shouldTriggerCancelEventWhenCancelButtonIsClicked() {
        window.button("cancelButton").click();
        verify(fakeControlListener, times(1)).requestStop();
    }

    @Test
    public void shouldCloseTheFrameWhenCancelButtonIsClicked() {
        window.button("cancelButton").click();
        Pause.pause(100);
        window.requireNotVisible();
    }

    @Test
    public void shouldAddNewLineOnTopWhenStatusUpdateIsRequested() {
        window.textBox("logTextArea").setText("OLD STATUS");
        outputDialog.updateStatus("NEW STATUS");
        window.textBox("logTextArea").requireText("NEW STATUS\nOLD STATUS");
    }

    @Test
    public void shouldIncrementProgressBarWhenFileIsDoneProcessing() {
        window.progressBar("progressBar").requireValue(0);
        processAllItems();
        window.progressBar("progressBar").requireValue(TOTAL_ITEMS_TO_PROCESS);
    }

    @Test
    public void shouldUpdateStatusWhenJobFinishes() {
        processAllItems();
        assertThat(window.textBox("logTextArea").text().toLowerCase()).contains("completed");
    }

    @Test
    public void shouldChangeCancelButtonToOkWhenJobFinishes() {
        processAllItems();
        assertThat(window.button("cancelButton").text().toLowerCase()).contains("ok");
    }

    @After
    public void tearDown() {
        window.cleanUp();
    }

    private void processAllItems() {
        for (int i=0; i<TOTAL_ITEMS_TO_PROCESS; i++) {
            outputDialog.incrementProgress();
        }
    }
}