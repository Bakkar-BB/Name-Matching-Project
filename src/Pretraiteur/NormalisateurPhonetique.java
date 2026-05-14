package pretraitement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import model.Nom;

public class NormalisateurPhonetique implements Pretraitement {
    private static final Map<String, String> VARIANTES = Map.ofEntries(
            Map.entry("MOHAMMED", "MOHAMMAD"),
            Map.entry("MOHAMED", "MOHAMMAD"),
            Map.entry("MUHAMMED", "MOHAMMAD"),
            Map.entry("MUHAMAD", "MOHAMMAD"),
            Map.entry("MOHAMAD", "MOHAMMAD"),
            Map.entry("YOUSEF", "YUSUF"),
            Map.entry("YOUSUF", "YUSUF"),
            Map.entry("YUSEF", "YUSUF"),
            Map.entry("MUSTAFA", "MOSTAFA"),
            Map.entry("MOUSTAFA", "MOSTAFA"),
            Map.entry("MUSTAPHA", "MOSTAFA"),
            Map.entry("OMER", "OMAR"),
            Map.entry("UMAR", "OMAR"),
            Map.entry("OSSAMA", "OSAMA"),
            Map.entry("OUSAMA", "OSAMA"),
            Map.entry("USAMA", "OSAMA"),
            Map.entry("ABDALLAH", "ABDULLAH"),
            Map.entry("ABDALLA", "ABDULLAH"),
            Map.entry("ABDELAH", "ABDULLAH"),
            Map.entry("HASSAN", "HASAN"),
            Map.entry("HUSSEIN", "HUSSAIN"),
            Map.entry("HOSSEIN", "HUSSAIN")
    );

    @Override
    public List<String> traiter(Nom nom) {
        List<String> source = nom.getNomPretraite();
        if (source.isEmpty()) {
            source = List.of(nom.getNomBrut());
        }
        List<String> result = new ArrayList<>();
        for (String token : source) {
            if (token == null) continue;
            result.add(VARIANTES.getOrDefault(token.toUpperCase(), token.toUpperCase()));
        }
        return result;
    }
}

