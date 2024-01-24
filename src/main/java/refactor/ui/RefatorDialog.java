package refactor.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import refactor.BatchRefactor;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.io.File;

public class RefatorDialog extends DialogWrapper implements DocumentListener {

  private static final Logger LOG = LoggerFactory.getLogger("es.ewald.intellij.refactor.ui.BulkRenameDialog");

  private Project project;

  private BatchRefactor refactor;

  public RefatorDialog(Project prj) {
    super(true);
    this.project = prj;
    setTitle("Luna Rename");
    init();
  }

  @Override
  protected JComponent createCenterPanel() {
    TextFieldWithBrowseButton csvFileChooser = new TextFieldWithBrowseButton();
    csvFileChooser.addBrowseFolderListener("Choose YML",
        "Choose YML",
        project,
        FileChooserDescriptorFactory.createSingleFileDescriptor("yml"));
    csvFileChooser.getTextField().getDocument().addDocumentListener(this);

//    JBList<RenameTask> renameTaskLists = new JBList<>(tasks);
//    renameTaskLists.setCellRenderer(new RenameTaskListCellRenderer());
  
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(csvFileChooser, BorderLayout.NORTH);
//    panel.add(new JBScrollPane(renameTaskLists), BorderLayout.CENTER);
    return panel;
  }

  @Override
  protected void doOKAction() {
    super.doOKAction();
    refactor.startRefactoring();
  }

  @Override
  public void insertUpdate(DocumentEvent documentEvent) {
    changedUpdate(documentEvent);
  }

  @Override
  public void removeUpdate(DocumentEvent documentEvent) {

  }

  @Override
  public void changedUpdate(DocumentEvent documentEvent) {
    try {
      reloadTasks(documentEvent.getDocument().getText(0, documentEvent.getLength()));
    } catch (BadLocationException  e) {
      LOG.error("Could not load rename tasks.", e);
    }
  }

  private void reloadTasks(String ymlFile)  {
    LOG.info("select file :" +ymlFile);
    File f = new File(ymlFile);

    refactor = new BatchRefactor(project,f.getAbsolutePath());
  }

}
