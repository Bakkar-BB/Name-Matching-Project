package livreur;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import model.Nom;
import model.ResultatComparaison;

public class LivreurCSV implements Livreur {
    private final String cheminFichier;

    public LivreurCSV(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }

    @Override
    public void livrer(List<ResultatComparaison> resultats) {
        if (resultats == null || resultats.isEmpty()) return;
        try (PrintWriter writer = new PrintWriter(new FileWriter(cheminFichier, true))) {
            for (ResultatComparaison resultat : resultats) {
                writer.printf("%s,%s,%s,%.4f,%s%n",
                        resultat.idEntreeListe,
                        resultat.nomClient.getNomBrut(),
                        resultat.nomTrouve,
                        resultat.score,
                        resultat.source);
            }
        } catch (IOException e) {
            System.err.println("Erreur écriture CSV : " + e.getMessage());
        }
    }

    @Override
    public void livrerAvecDetails(Nom nom, List<ResultatComparaison> resultats) {
        if (resultats == null || resultats.isEmpty()) return;
        try (PrintWriter writer = new PrintWriter(new FileWriter(cheminFichier, true))) {
            writer.printf("Nom client: %s%n", nom.getNomBrut());
            for (ResultatComparaison resultat : resultats) {
                writer.printf("%s,%s,%.4f,%s%n",
                        resultat.idEntreeListe,
                        resultat.nomTrouve,
                        resultat.score,
                        resultat.source);
            }
        } catch (IOException e) {
            System.err.println("Erreur écriture CSV : " + e.getMessage());
        }
    }
}
