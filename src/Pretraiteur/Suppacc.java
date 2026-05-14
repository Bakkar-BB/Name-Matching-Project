package pretraitement;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import model.Nom;

public class SuppAcc implements Pretraitement {
    @Override
    public List<String> traiter(Nom nom) {
        List<String> source = nom.getNomPretraite();
        if (source.isEmpty()) {
            source = List.of(nom.getNomBrut());
        }
        List<String> result = new ArrayList<>();
        for (String token : source) {
            if (token == null) continue;
            String normalized = Normalizer.normalize(token, Normalizer.Form.NFD);
            result.add(normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", ""));
        }
        return result;
    }
}

