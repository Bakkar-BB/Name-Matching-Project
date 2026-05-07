package Pretraitement;
import model.Nom;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Decomposeur implements Pretraitement {
    private static final List<String> PARTICULES = List.of(
            "de", "du", "des", "le", "la", "les",
            "van", "von", "der", "bin", "binte",
            "al", "el", "ben", "dit", "dit"
    );

    @Override
    public List<String> traiter(Nom nom) {
        List<String> tokens = nom.getNomPretraite().isEmpty()
                ? Arrays.asList(nom.getNomBrut().toLowerCase().trim().split("\\s+"))
                : nom.getNomPretraite();

        return tokens.stream()
                .filter(t -> !PARTICULES.contains(t.toLowerCase()))
                .filter(t -> !t.isBlank())
                .toList();
    }
}