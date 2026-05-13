package kyc.comparateur;

import kyc.model.Nom;

import java.util.*;
import java.util.stream.Collectors;

public class GenerateurCandidats {

    
    private final Map<String, Set<Nom>> index = new HashMap<>();
    private final int minTokensCommuns;

    public GenerateurCandidats(List<Nom> listeSanctions, int minTokensCommuns) {
        this.minTokensCommuns = minTokensCommuns;
        construireIndex(listeSanctions);
    }
    private void construireIndex(List<Nom> listeSanctions) {
        for (Nom nom : listeSanctions) {
            List<String> tokens = getTokens(nom);
            for (String token : tokens) {
                index.computeIfAbsent(token, k -> new HashSet<>()).add(nom);
            }
        }
    }

    public List<Nom> genererCandidats(Nom nomClient) {
        List<String> tokensClient = getTokens(nomClient);

        Map<Nom, Integer> scoreMap = new HashMap<>();

        for (String token : tokensClient) {
            Set<Nom> matches = index.getOrDefault(token, Collections.emptySet());
            for (Nom match : matches) {
                scoreMap.merge(match, 1, Integer::sum);
            }
        }

        return scoreMap.entrySet().stream()
                .filter(e -> e.getValue() >= minTokensCommuns)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    private List<String> getTokens(Nom nom) {
        if (nom.getNomPretraite() != null && !nom.getNomPretraite().isEmpty()) {
            return nom.getNomPretraite();
        }
        return Arrays.asList(nom.getNomBrut().trim().toUpperCase().split("\\s+"));
    }
}
