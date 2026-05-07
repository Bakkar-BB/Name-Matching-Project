package Livraison;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.List;
import java.io.IOException;

import Moteur.Nom;
import Moteur.ResultatComparaison;
public class LivreurCSV implements Livreur {
    private final String cheminFichier;


    public LivreurCSV(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }

    @Override
    public void livrer(List<ResultatComparaison> resultats) {
        if (resultats == null || resultats.isEmpty()) return;
        try (PrintWriter pw = new PrintWriter(new FileWriter(cheminFichier, true))) {
            for (ResultatComparaison r : resultats)
                pw.printf("%s,%s,%s,%.4f,%s%n",
                        r.idEntreeListe, r.nomClient.getNomBrut(), r.nomTrouve, r.score, r.source);
        } catch (IOException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    @Override
    public void livrerAvecDetails(Nom nom, List<ResultatComparaison> resultats) {

    }
}