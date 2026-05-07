package kyc.Pretraitement;
import kyc.model.Nom;
import java.util.List;

public class Normalisation implements Pretraitement {
    @Override
    public List<String> traiter(Nom nom) {
        
        String source = nom.getNomPretraite().isEmpty() 
                        ? nom.getNomBrut() 
                        : String.join(" ", nom.getNomPretraite());

        String[] words = source.trim().split("\\s+");
        
        List<String> result = new java.util.ArrayList<>();
        for (String word : words) {
            String clean = word.trim().toUpperCase();
            if (!clean.isBlank()) {
                result.add(clean);
            }
        }
        return result;
    }
}