package Pretraiteur;
 
import model.Nom;
import java.util.Arrays;
import java.util.List;

public class Decomposeur implements Pretraiteur{
 
    private static final List<String> PARTICULES = List.of(
            // French
            "de", "du", "des", "le", "la", "les", "dit",
            // Dutch/German
            "van", "von", "der",
            // Arabic
            "al", "el", "ben", "bin", "binte", "abu", "um", "bou",
            // Spanish
            "del", "las", "los",
            // Italian
            "di", "da", "dello", "della"
    );
 
    @Override
    public List<String> traiter(Nom nom) {
        List<String> tokens = nom.getNomPretraite().isEmpty()
                ? Arrays.asList(nom.getNomBrut().toLowerCase().trim().split("\\s+"))
                : nom.getNomPretraite();
 
        return tokens.stream()
                .filter(t -> t != null && !t.isBlank())
                .filter(t -> !PARTICULES.contains(t.toLowerCase()))
                .toList();
    }
}
