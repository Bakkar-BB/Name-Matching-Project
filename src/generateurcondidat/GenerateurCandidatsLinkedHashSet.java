package candidat;

import model.Nom;

import java.util.*;

/**
 * GÃ©nÃ¨re des candidats en prÃ©servant l'ordre d'insertion
 * et en Ã©liminant les doublons via un LinkedHashSet.
 *
 * Un candidat est retenu si au moins un de ses tokens
 * correspond exactement Ã  un token du nom client.
 * L'ordre d'apparition dans la liste de sanctions est respectÃ©.
 */
public class GenerateurCandidatsLinkedHashSet extends AbstractCandidateGenerator {

    private final List<Nom> listeSanctions;

    public GenerateurCandidatsLinkedHashSet(List<Nom> listeSanctions) {
        this.listeSanctions = new ArrayList<>(listeSanctions);
    }

    @Override
    public List<Nom> generateCandidates(Nom nomClient) {
        // Tokens du client dans un Set pour lookup O(1)
        Set<String> tokensClient = new LinkedHashSet<>(getTokens(nomClient));

        // LinkedHashSet pour dÃ©doublonnage avec ordre d'insertion prÃ©servÃ©
        Set<Nom>    vus      = new LinkedHashSet<>();
        List<Nom>   candidats = new ArrayList<>();

        for (Nom sanction : listeSanctions) {
            if (vus.contains(sanction)) continue;

            List<String> tokensSanction = getTokens(sanction);

            // DÃ©doublonnage sur la signature des tokens normalisÃ©s
            List<String> propres = new ArrayList<>();
            for (String t : tokensSanction) {
                if (t != null && !t.isBlank()) propres.add(t.trim());
            }

            for (String token : propres) {
                if (tokensClient.contains(token)) {
                    vus.add(sanction);
                    candidats.add(sanction);
                    break; // un token commun suffit
                }
            }
        }

        return Collections.unmodifiableList(candidats);
    }

    @Override
    public String getName() { return "GenerateurLinkedHashSet"; }
}
