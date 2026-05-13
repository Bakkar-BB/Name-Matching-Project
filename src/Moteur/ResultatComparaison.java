package Moteur;

public class ResultatComparaison {
    public final String idEntreeListe;
    public final Nom nomClient;
    public final String nomTrouve;
    public final double score;
    public final String source;

    public ResultatComparaison(String idEntreeListe, Nom nomClient, String nomTrouve, double score, String source) {
        if (score < 0.0 || score > 1.0)
            throw new IllegalArgumentException("Score invalide : " + score);
        this.idEntreeListe = idEntreeListe;
        this.nomClient = nomClient;
        this.nomTrouve = nomTrouve;
        this.score = score;
        this.source = source;
    }
    public double getScore() { return score; }

    public boolean estAuDessousDuSeuil(double seuil) { return score < seuil; }
    public boolean estAuDessus(double seuil) { return score >= seuil; }

}
