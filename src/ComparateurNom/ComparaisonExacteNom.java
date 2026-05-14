package comparateur;

import java.util.List;

public class ComparaisonExacteNom implements ComparateurNom {
    private final ComparateurChaine comparateurChaine;

    public ComparaisonExacteNom() {
        this.comparateurChaine = new ExacteChaine();
    }

    @Override
    public double comparer(List<String> tokens1, List<String> tokens2) {
        if (tokens1 == null || tokens2 == null || tokens1.isEmpty() || tokens2.isEmpty()) {
            return 0.0;
        }

        int matches = 0;
        int total = Math.max(tokens1.size(), tokens2.size());

        for (String t1 : tokens1) {
            for (String t2 : tokens2) {
                if (comparateurChaine.comparer(t1, t2) == 1.0) {
                    matches++;
                    break;
                }
            }
        }

        return total == 0 ? 0.0 : (double) matches / total;
    }
}
