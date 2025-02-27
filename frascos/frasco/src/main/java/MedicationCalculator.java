import java.util.*;
import java.util.regex.*;

public class MedicationCalculator {

    private final Map<String, Map<String, ConversionInfo>> equivalences = new HashMap<>();

    public MedicationCalculator() {
        //cadastro
        cadastrarEquivalencia("dipiRONA sódica 500 mg/mL ampola 2", "mg", 1000, "Amp", 1);
        cadastrarEquivalencia("dipiRONA sódica 500 mg/mL ampola 2", "mL", 2, "Amp", 1);
        cadastrarEquivalencia("CAPTOpril 25mg cp", "mg", 25, "cp", 1);
        cadastrarEquivalencia("DEXAmetasona, fosfato 4 mg/mL ampola", "mg", 20, "Amp", 1);
        cadastrarEquivalencia("Espironolactona 25mg cp", "mg", 25, "cp", 1);
        cadastrarEquivalencia("Furosemida 10mg/mL ampola 2ml Inj", "mg", 20, "Amp", 1);
        cadastrarEquivalencia("Furosemida 40mg cp", "mg", 40, "cp", 1);
        cadastrarEquivalencia("Meropenem FA 500mg IV Inj", "mg", 500, "Fr", 1);
        cadastrarEquivalencia("Metronidazol 250 mg cp", "mg", 250, "cp", 1);
        cadastrarEquivalencia("Acido Folico", "mg", 5, "cp", 1);
        cadastrarEquivalencia("Gabapentina 300mg cáps (HMJ)", "mg", 300, "cp", 1);
        cadastrarEquivalencia("GENTAmicina 40 mg/mL ampola 2 ml inj", "mg", 80, "amp", 1);
        cadastrarEquivalencia("Hidroxicloroquina 400mg cp", "mg", 400, "cp", 1);
        cadastrarEquivalencia("predniSONA 20 mg cp", "mg", 20, "cp", 1);
        cadastrarEquivalencia("Albumina Humana 20% (200mg/mL) FA", "ml", 50, "fr", 1);
    }

    public void cadastrarEquivalencia(String medicamento, String unidadeOriginal, double quantidadeOriginal, String unidadeEquivalente, double quantidadeEquivalente) {
        equivalences.putIfAbsent(medicamento.toLowerCase(), new HashMap<>());
        equivalences.get(medicamento.toLowerCase()).put(unidadeOriginal.toLowerCase(),
                new ConversionInfo(quantidadeOriginal, quantidadeEquivalente, unidadeEquivalente));
    }

    public String calculateDoses(String text) {
        Map<String, Map<String, Double>> medicationDoses = new HashMap<>();
        String[] lines = text.split("\\r?\\n");

        //Lógica do regex para interpretação do relat´orio
        Pattern patternPatient = Pattern.compile("^ISO\\s+\\d+\\s+(.+?)\\s+-\\s+Atend:");
        Pattern patternMedication = Pattern.compile("^[\\d\\s]*([\\p{L}\\s\\-,()\\d%/]+(?:\\s+\\d+\\s*mg/mL(?:\\s+ampola\\s+\\d+)?)?)\\s+(\\d+[,.]?\\d*)\\s*(mg|mcg|mL|gts|ml)\\b(?!\\s*-\\s*\\d{1,2}:\\d{2})");

        String currentPatient = "";
        Set<String> patientMedications = new HashSet<>();

        for (String line : lines) {
            Matcher matcherPatient = patternPatient.matcher(line);
            if (matcherPatient.find()) {
                currentPatient = matcherPatient.group(1).trim();
                patientMedications.clear();
                continue;
            }

            if (line.toUpperCase().contains("SUSPENSO")) {
                continue;
            }

            Matcher matcherMedication = patternMedication.matcher(line);

            if (matcherMedication.find()) {
                String medicationName = matcherMedication.group(1).trim();
                double dose = Double.parseDouble(matcherMedication.group(2).replace(",", "."));
                String unit = matcherMedication.group(3).toLowerCase();

                String medicationKey = medicationName + " (" + unit + ")";

                if (!patientMedications.contains(medicationKey)) {
                    patientMedications.add(medicationKey);
                    medicationDoses.putIfAbsent(medicationKey, new HashMap<>());
                    medicationDoses.get(medicationKey).put(unit, medicationDoses.get(medicationKey).getOrDefault(unit, 0.0) + dose);
                }
            }
        }

        TreeMap<String, Map<String, Double>> sortedMedicationDoses = new TreeMap<>(medicationDoses);

        StringBuilder result = new StringBuilder("Totais de Medicamentos (em ordem alfabética):\n");
        sortedMedicationDoses.forEach((name, unitMap) -> {
            unitMap.forEach((unit, totalDose) -> {
                String displayName = name.substring(0, name.lastIndexOf(" ("));
                result.append("Medicamento: ").append(displayName).append(", Dose Total: ")
                        .append(String.format("%.2f", totalDose)).append(" ").append(unit);

                String medicationLower = displayName.toLowerCase();
                if (equivalences.containsKey(medicationLower) && equivalences.get(medicationLower).containsKey(unit)) {
                    ConversionInfo conversion = equivalences.get(medicationLower).get(unit);
                    double equivalentDose = Math.ceil((totalDose / conversion.quantidadeOriginal) * conversion.quantidadeEquivalente);
                    result.append(" (equivalente a ").append(String.format("%.0f", equivalentDose))
                            .append(" ").append(conversion.unidadeEquivalente).append(")");
                }

                result.append("\n");
            });
        });

        return result.toString();
    }

    private static class ConversionInfo {
        double quantidadeOriginal;
        double quantidadeEquivalente;
        String unidadeEquivalente;

        ConversionInfo(double quantidadeOriginal, double quantidadeEquivalente, String unidadeEquivalente) {
            this.quantidadeOriginal = quantidadeOriginal;
            this.quantidadeEquivalente = quantidadeEquivalente;
            this.unidadeEquivalente = unidadeEquivalente;
        }
    }
}