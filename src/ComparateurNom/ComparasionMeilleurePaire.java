package kyc.comparateur;

import java.util.List;

public class ComparasionMeilleurePaire implements ComparateurNom {

    private final ComparateurChaine comparateurChaine;

    public ComparasionMeilleurePaire(ComparateurChaine comparateurChaine) {
        this.comparateurChaine = comparateurChaine;
    }

    @Override
    public double comparer(List<String> tokens1, List<String> tokens2) {
        if (tokens1 == null || tokens1.isEmpty()
         || tokens2 == null || tokens2.isEmpty()) return 0.0;

        double totalMeilleurscore = 0.0;
        for (String t1 : tokens1) {
            double bestMatch = 0.0;
            for (String t2 : tokens2) {
                bestMatch = Math.max(bestMatch, comparateurChaine.comparer(t1, t2));
            }
            totalMeilleurscore += bestMatch; // ← was "besmatcht" (typo)
        }
        return totalMeilleurscore / tokens1.size();
    }
}