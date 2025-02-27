import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

public class GUIReader {

    private JTextArea resultArea;
    private JTextArea extractedTextArea;
    private boolean isExtractedTextVisible = false;

    public static void main(String[] args) {
        JFrame frame = new JFrame("HMIMJ contador de doses");
        SwingUtilities.invokeLater(GUIReader::new);
        HMJICON.applyCustomIcon(); // Chame aqui, após tornar o frame visível
    }

    public GUIReader() {
        JFrame frame = new JFrame("HMIMJ contador de doses");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLayout(new BorderLayout());

        JLabel instructionLabel = new JLabel("Arraste e solte um arquivo PDF aqui ou clique para selecionar");
        JButton selectButton = new JButton("Escolher PDF");
        JButton toggleTextButton = new JButton("Mostrar Texto");

        resultArea = new JTextArea();
        resultArea.setEditable(false);

        extractedTextArea = new JTextArea();
        extractedTextArea.setEditable(false);
        extractedTextArea.setVisible(isExtractedTextVisible);

        JScrollPane resultScrollPane = new JScrollPane(resultArea);
        JScrollPane extractedTextScrollPane = new JScrollPane(extractedTextArea);

        toggleTextButton.addActionListener(e -> toggleExtractedText());

        selectButton.addActionListener(e -> selectFile());

        JPanel topPanel = new JPanel();
        topPanel.add(instructionLabel);
        topPanel.add(selectButton);
        topPanel.add(toggleTextButton);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(resultScrollPane, BorderLayout.CENTER);
        frame.add(extractedTextScrollPane, BorderLayout.SOUTH);

        frame.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(java.awt.dnd.DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File file : droppedFiles) {
                        processFile(file);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        frame.setVisible(true);
    }

    private void toggleExtractedText() {
        isExtractedTextVisible = !isExtractedTextVisible;
        extractedTextArea.setVisible(isExtractedTextVisible);
        extractedTextArea.getParent().revalidate();
        extractedTextArea.getParent().repaint();
    }

    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            processFile(selectedFile);
        }
    }

    private void processFile(File file) {
        PDFProcessor pdfProcessor = new PDFProcessor();
        String extractedText = pdfProcessor.extractText(file.getAbsolutePath());
        extractedTextArea.setText("Texto extraído:\n" + extractedText);
        String result = pdfProcessor.calculateDoses(extractedText);
        resultArea.setText(result);
    }
}