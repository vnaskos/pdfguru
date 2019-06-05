package ui;

import com.vnaskos.pdfguru.ProcessListener;
import com.vnaskos.pdfguru.ui.OutputDialog;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.assertj.swing.timing.Pause;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class OutputDialogTest extends AssertJSwingJUnitTestCase {

    private static final int TOTAL_ITEMS_TO_PROCESS = 2;

    private final ProcessListener fakeControlListener = mock(ProcessListener.class);

    private FrameFixture window;
    private OutputDialog outputDialog;

    @Override
    protected void onSetUp() {
        outputDialog = GuiActionRunner.execute(() -> new OutputDialog(TOTAL_ITEMS_TO_PROCESS));
        outputDialog.setProcessListener(fakeControlListener);
        window = new FrameFixture(robot(), outputDialog);
        window.show();
    }

    @Test
    public void shouldTriggerCancelEventWhenCancelButtonIsClicked() {
        window.requireVisible().button("cancelButton").click();
        verify(fakeControlListener, times(1)).requestStop();
    }

    @Test
    public void shouldCloseTheFrameWhenCancelButtonIsClicked() {
        window.requireVisible().button("cancelButton").click();
        Pause.pause(300);
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

    private void processAllItems() {
        for (int i=0; i<TOTAL_ITEMS_TO_PROCESS; i++) {
            outputDialog.incrementProgress();
        }
    }
}