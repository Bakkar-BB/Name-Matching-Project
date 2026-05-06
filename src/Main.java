import config.Configuration;
import model.ListeControle;
import model.Nom;
import model.ResultatComparaison;
import pipeline.ChaineTraitement;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // config-1
        Configuration config = Configuration.defaut();
        // data load
        List<ListeControle> listes;

        if (args.length > 0) {
            try {
                listes = List.of(ListeControle.chargerDepuisCSV(args[0]));
                System.out.println("Liste chargée : " + args[0] +
                        " (" + listes.get(0).entrees().size() + " entrées)");
            } catch (IOException e) {
                System.err.println("Impossible de charger le fichier : " + e.getMessage());
                System.exit(1);
                return;
            }
        } else {
            listes = exempleListeControle();
            System.out.println("Mode démonstration — liste embarquée utilisée.");
        }
        // pipeline
        ChaineTraitement pipeline = new ChaineTraitement(config);

        List<Nom> clientsAVerifier = List.of(
                new Nom("Ben Laden Oussama"),
                new Nom("Alice Dupont"),
                new Nom("Mohamed Al Qahtani")
        );

        System.out.println("\n========== Résultats KYC/AML ==========");
        for (Nom client : clientsAVerifier) {
            List<ResultatComparaison> alertes = pipeline.executer(client, listes);
            if (alertes.isEmpty()) {
                System.out.println("✓ " + client.getNomBrut() + " — aucune alerte.");
            }
        }
    }
    private static List<ListeControle> exempleListeControle() {
        return List.of(new ListeControle("demo", List.of(
                new model.EntreeListe("001", "Ben Laden Usama", "OFAC"),
                new model.EntreeListe("002", "Bin Ladin Osama", "ONU"),
                new model.EntreeListe("003", "Al Qahtani Mohammed", "EU"),
                new model.EntreeListe("004", "Dupont Alice Marie", "LOCAL")
        )));
    }
}


