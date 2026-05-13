package kyc.comparateur;

import java.util.List;

public class ComparateurMeilleurPaire implements ComparateurNom {

    private final ComparateurChaine comparateurChaine;

    public ComparateurMeilleurPaire(ComparateurChaine comparateurChaine) {
        this.comparateurChaine = comparateurChaine;
    }

    @Override
    public double comparer(List<String> tokens1, List<String> tokens2) {
        if (tokens1 == null || tokens2 == null) return 0.0;
        if (tokens1.isEmpty() || tokens2.isEmpty()) return 0.0;

        double scoreGauche = scorePondere(tokens1, tokens2); 
        double scoreDroite = scorePondere(tokens2, tokens1); 
        return (scoreGauche + scoreDroite) / 2.0;
    }
    private double scorePondere(List<String> source, List<String> cible) {
        double scoreTotal = 0.0;
        double poidsTotal = 0.0;

        for (String tokenSource : source) {
            double meilleurScore = 0.0;

            for (String tokenCible : cible) {
                double score = comparateurChaine.comparer(tokenSource, tokenCible);
                if (score > meilleurScore) {
                    meilleurScore = score;
                }
            }
            double poids = tokenSource.length();
            scoreTotal += meilleurScore * poids;
            poidsTotal += poids;
        }

        return poidsTotal > 0 ? scoreTotal / poidsTotal : 0.0;
    }
}
