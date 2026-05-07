package kyc.Pretraitement;
import kyc.model.Nom;
import java.util.List;
import java.text.Normalizer;
import java.util.ArrayList;


public class SuppAcc extends Pretraitement {

    @Override
    public List<String> traiter(List<String> input) {
        List<String> result = new ArrayList<>();

        for (String s : input) {
            if (s != null) {
                
                String normalized = Normalizer.normalize(s, Normalizer.Form.NFD);

                
                String withoutAccents = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

                result.add(withoutAccents);
            } else {
                result.add(null);
            }
        }

        return result;
    }
}
