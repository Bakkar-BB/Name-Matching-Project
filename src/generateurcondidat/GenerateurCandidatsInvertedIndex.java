package candidat;

import model.Nom;

import java.util.*;
import java.util.stream.Collectors;

public class GenerateurCandidatsInvertedIndex extends AbstractCandidateGenerator {

    private final Map<String, Set<Nom>> index = new HashMap<>();
    private final int minTokensCommuns;

    public GenerateurCandidatsInvertedIndex(List<Nom> listeSanctions, int minTokensCommuns) {
        this.minTokensCommuns = Math.max(1, minTokensCommuns);
        construireIndex(listeSanctions);
    }

    private void construireIndex(List<Nom> listeSanctions) {
        for (Nom nom : listeSanctions) {
            for (String token : getTokens(nom)) {
                index.computeIfAbsent(token, k -> new HashSet<>()).add(nom);
            }
        }
    }

    @Override
    public List<Nom> generateCandidates(Nom nomClient) {
        List<String> tokensClient = getTokens(nomClient);
        Map<Nom, Integer> scoreMap = new HashMap<>();

        for (String token : tokensClient) {
            for (Nom candidat : index.getOrDefault(token, Collections.emptySet())) {
                scoreMap.merge(candidat, 1, Integer::sum);
            }
        }

        return scoreMap.entrySet().stream()
                .filter(e -> e.getValue() >= minTokensCommuns)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public String getName() { return "GenerateurInvertedIndex"; }
}
