package pretraitement;

import java.util.ArrayList;
import java.util.List;
import model.Nom;

public class Normalisation implements Pretraitement {
    @Override
    public List<String> traiter(Nom nom) {
        List<String> source = nom.getNomPretraite();
        if (source.isEmpty()) {
            source = List.of(nom.getNomBrut());
        }

        List<String> result = new ArrayList<>();
        for (String token : source) {
            if (token != null && !token.isBlank()) {
                result.add(token.toUpperCase());
            }
        }
        return result;
    }
}

