import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.io.IOException;

public class PDFProcessor {

    public String extractText(String filePath) {
        StringBuilder extractedText = new StringBuilder();
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            extractedText.append(pdfStripper.getText(document));
        } catch (IOException e) {
            extractedText.append("Erro ao processar o PDF: ").append(e.getMessage());
        }
        return extractedText.toString();
    }

    public String calculateDoses(String text) {
        MedicationCalculator calculator = new MedicationCalculator();
        return calculator.calculateDoses(text);
    }
}