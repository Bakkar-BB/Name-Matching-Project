package ComparateurNom;
import ComparateurChaine.ComparateurChaine;

import java.util.List;
public class ComparateurMeilleurPaire implements ComparateurNom{
    private final ComparateurChaine comparateurChaine;

    public ComparateurMeilleurPaire(ComparateurChaine comparateurChaine){
        this.comparateurChaine=comparateurChaine;
    }
    @Override
    public double comparer( List<String> tokens1, List<String> tokens2){
        if (tokens1 == null || tokens2 == null) return 0.0;
        if (tokens1.isEmpty() || tokens2.isEmpty()) return 0.0;

        double totalScore = 0.0;
        for (String t1 : tokens1) {
            double best = 0.0;
            for (String t2 : tokens2) {
                best = Math.max(best, comparateurChaine.comparer(t1, t2));
            }
            totalScore += best;
        }
        return totalScore / tokens1.size();
    }
}
