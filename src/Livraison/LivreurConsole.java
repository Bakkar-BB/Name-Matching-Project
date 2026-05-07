package livraison;

import java.util.List;

class Nom {
    private String valeur;

    public Nom(String valeur) { this.valeur = valeur; }

    @Override
    public String toString() { return valeur; }
}

class ResultatComparaison {
    private String nom;
    private double score;
    private String source;

    public ResultatComparaison(String nom, double score, String source) {
        this.nom = nom;
        this.score = score;
        this.source = source;
    }

    public String getNom() { return nom; }
    public double getScore() { return score; }
    public String getSource() { return source; }

    @Override
    public String toString() {
        return nom + " | score=" + score + " | source=" + source;
    }
}



public class LivreurConsole implements Livreur {

    @Override
    public void livrer(List<ResultatComparaison> resultats) {
        System.out.println("=== Résultats ===");
        for (ResultatComparaison r : resultats) {
            System.out.println(r);
        }
    }

    @Override
    public void livrerAvecDetails(Nom nom, List<ResultatComparaison> resultats) {
        System.out.println("=== Résultats pour : " + nom + " ===");
        for (ResultatComparaison r : resultats) {
            System.out.println("  Nom    : " + r.getNom());
            System.out.println("  Score  : " + r.getScore());
            System.out.println("  Source : " + r.getSource());
            System.out.println("  --------");
        }
    }
}