package pretraitement;

import java.util.Arrays;
import java.util.List;
import model.Nom;

public class Nettoyeur implements Pretraitement {
    @Override
    public List<String> traiter(Nom nom) {
        if (nom == null || nom.getNomBrut() == null) return List.of();
        String texte = nom.getNomBrut()
                .replaceAll("-", " ")
                .replaceAll("[^\\p{L}\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim()
                .toUpperCase();
        if (texte.isBlank()) {
            return List.of();
        }
        return Arrays.asList(texte.split("\\s+"));
    }
}

