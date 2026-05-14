package candidat;

import model.Nom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Génère des candidats par correspondance phonétique Soundex.
 * Chaque token du nom client est encodé en Soundex,
 * et tout nom de la liste partageant au moins un code Soundex commun
 * est retenu comme candidat.
 */
public class GenerateurCandidatsSoundex extends AbstractCandidateGenerator {

    private final List<Nom> listeSanctions;

    public GenerateurCandidatsSoundex(List<Nom> listeSanctions) {
        this.listeSanctions = new ArrayList<>(listeSanctions);
    }

    @Override
    public List<Nom> generateCandidates(Nom nomClient) {
        // Codes Soundex de tous les tokens du client
        Set<String> codesClient = new HashSet<>();
        for (String token : getTokens(nomClient)) {
            codesClient.add(soundex(token));
        }

        List<Nom> resultats = new ArrayList<>();

        for (Nom sanction : listeSanctions) {
            for (String token : getTokens(sanction)) {
                if (codesClient.contains(soundex(token))) {
                    resultats.add(sanction);
                    break; // un token suffisant, on passe au suivant
                }
            }
        }

        return resultats;
    }

    // ── Algorithme Soundex ───────────────────────────────
    public static String soundex(String mot) {
        if (mot == null || mot.isBlank()) return "";
        mot = mot.trim().toUpperCase();

        StringBuilder code     = new StringBuilder();
        String        precedent = getCode(mot.charAt(0));
        code.append(mot.charAt(0));

        for (int i = 1; i < mot.length() && code.length() < 4; i++) {
            String actuel = getCode(mot.charAt(i));
            if (!actuel.equals("0") && !actuel.equals(precedent)) {
                code.append(actuel);
            }
            precedent = actuel;
        }

        while (code.length() < 4) code.append("0");
        return code.toString();
    }

    private static String getCode(char c) {
        switch (c) {
            case 'B': case 'F': case 'P': case 'V':                         return "1";
            case 'C': case 'G': case 'J': case 'K':
            case 'Q': case 'S': case 'X': case 'Z':                         return "2";
            case 'D': case 'T':                                              return "3";
            case 'L':                                                        return "4";
            case 'M': case 'N':                                              return "5";
            case 'R':                                                        return "6";
            default:                                                         return "0";
        }
    }

    @Override
    public String getName() { return "GenerateurSoundex"; }
}
