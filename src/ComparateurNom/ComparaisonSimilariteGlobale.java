package kyc.comparateur;

import java.util.List;

public class ComparaisonSimilariteGlobale implements ComparateurNom {

    private final ComparateurChaine comparateurChaine;

    public ComparaisonSimilariteGlobale(ComparateurChaine comparateurChaine) {
        this.comparateurChaine = comparateurChaine;
    }
    @Override
    public double comparer(List<String> tokens1, List<String> tokens2) {
        if (tokens1 == null || tokens1.isEmpty()
         || tokens2 == null || tokens2.isEmpty()) return 0.0;

        double total = 0.0;
        int count = 0;
        for (String t1 : tokens1) {
            for (String t2 : tokens2) {
                total += comparateurChaine.comparer(t1, t2);
                count++;
            }
        }
     double moyenne = count > 0 ? total / count : 0.0;

        int maxLen = Math.max(tokens1.size(), tokens2.size());
        int minLen = Math.min(tokens1.size(), tokens2.size());
        double penalite = (double) minLen / maxLen; /*penalité dans le cas ou les deux tokens n'ont pas la meme longuer
     */

        return moyenne * penalite;
    }
}

    