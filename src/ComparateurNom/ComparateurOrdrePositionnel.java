package kyc.comparateur;

import java.util.List;

public class ComparateurOrdrePositionnel implements ComparateurNom {

    private final ComparateurChaine comparateurChaine;

    public ComparateurOrdrePositionnel(ComparateurChaine comparateurChaine) {
        this.comparateurChaine = comparateurChaine;
    }

    @Override
    public double comparer(List<String> tokens1, List<String> tokens2) {
        if (tokens1 == null || tokens2 == null) return 0.0;
        if (tokens1.isEmpty() || tokens2.isEmpty()) return 0.0;

        int minLen = Math.min(tokens1.size(), tokens2.size());
        int maxLen = Math.max(tokens1.size(), tokens2.size());

        double scoreTotal = 0.0;

        // Compare only up to the shorter name's length — position by position
        for (int i = 0; i < minLen; i++) {
            double score = comparateurChaine.comparer(tokens1.get(i), tokens2.get(i));

            // Positional weight: first tokens matter more than last tokens
            // position 0 → weight maxLen, position 1 → weight maxLen-1, etc.
            double poids = maxLen - i;

            scoreTotal += score * poids;
        }

        // Normalize by the sum of all positional weights including unmatched positions
        // This penalizes names of different lengths
        double poidsTotal = 0.0;
        for (int i = 0; i < maxLen; i++) {
            poidsTotal += maxLen - i;
        }

        return poidsTotal > 0 ? scoreTotal / poidsTotal : 0.0;
    }
}
