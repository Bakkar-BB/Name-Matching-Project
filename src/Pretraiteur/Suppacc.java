package kyc.Pretraitement;

import kyc.model.Nom;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class SuppAcc implements Pretraitement {
    @Override
    public List<String> traiter(Nom nom) {
        List<String> tokens = nom.getNomPretraite().isEmpty()
                ? List.of(nom.getNomBrut().trim().split("\\s+"))
                : nom.getNomPretraite();

        List<String> result = new ArrayList<>();
        for (String s : tokens) {
            if (s != null) {
                String normalized = Normalizer.normalize(s, Normalizer.Form.NFD);
                result.add(normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", ""));
            }
        }
        return result;
    }
}
