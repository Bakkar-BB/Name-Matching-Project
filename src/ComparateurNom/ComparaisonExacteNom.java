package comparateur;

import java.util.List;

interface ComparateurChaine {
    double comparer(String s1, String s2);
}


class ExacteChaine implements ComparateurChaine {
    @Override
    public double comparer(String s1, String s2) {
        if (s1 == null || s2 == null) return 0.0;
        return s1.equalsIgnoreCase(s2) ? 1.0 : 0.0;
    }
}

public class ComparaisonExacteNom implements ComparateurNom {

    private ComparateurChaine comparateurChaine;

    public ComparaisonExacteNom() {
        this.comparateurChaine = new ExacteChaine();
    }

    @Override
    public double comparer(List<String> tokens1, List<String> tokens2) {
        if (tokens1 == null || tokens2 == null || tokens1.isEmpty() || tokens2.isEmpty())
            return 0.0;

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
        return (double) matches / total;
    }
}