import Moteur.ListeControle;
import Moteur.EntreeList;
import Moteur.Nom;
import Moteur.ResultatComparaison;
import Pretraiteur.Netyoeur;
import ComparateurChaine.JaroWinkler;
import ComparateurNom.ComparateurMeilleurPaire;
import Selectionneur.SelectionAvancee;
import Livraison.LivreurCSV;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final double seuil       = 0.75;
    private static final String source_prio = "OFAC";
    private static final String sortie_csv  = "resultats_kyc.csv";

    public static void main(String[] args) {

        // 1. Chargement liste de contrôle
        List<ListeControle> listes;
        if (args.length > 0) {
            try {
                listes = List.of(ListeControle.chargerDepuisCSV(args[0]));
                System.out.println("Liste chargée : " + args[0]
                        + " (" + listes.get(0).entrees.size() + " entrées)");
            } catch (IOException e) {
                System.err.println("Impossible de charger le fichier : " + e.getMessage());
                System.exit(1);
                return;
            }
        } else {
            listes = exempleListeControle();
            System.out.println("Mode démonstration — liste embarquée utilisée.");
        }

        // 2. Pipeline
        Netyoeur             pretraiteur = new Netyoeur();
        ComparateurMeilleurPaire comparateur = new ComparateurMeilleurPaire(new JaroWinkler());
        SelectionAvancee     selecteur   = new SelectionAvancee(seuil, source_prio);
        LivreurCSV           livreur     = new LivreurCSV(sortie_csv);

        // 3. Clients à vérifier
        List<Nom> clients = List.of(
                new Nom("Ben Laden Oussama"),
                new Nom("Alice Dupont"),
                new Nom("Mohamed Al Qahtani")
        );

        // 4. Exécution
        System.out.println("\n========== Résultats KYC/AML ==========");
        for (Nom client : clients) {
            client.setNomPretraite(pretraiteur.traiter(client));

            List<ResultatComparaison> candidats = new ArrayList<>();

            for (ListeControle liste : listes) {
                for (EntreeList entree : liste.entrees) {
                    Nom nomEntree = new Nom(entree.nomBrut, entree.id);
                    nomEntree.setNomPretraite(pretraiteur.traiter(nomEntree));

                    double score = comparateur.comparer(
                            client.getNomPretraite(),
                            nomEntree.getNomPretraite()
                    );

                    candidats.add(new ResultatComparaison(
                            entree.id,
                            client,
                            entree.nomBrut,
                            score,
                            entree.source
                    ));
                }
            }

            // Filtrage + tri par seuil
            List<ResultatComparaison> alertes = selecteur.filtrer(candidats);

            if (alertes.isEmpty()) {
                System.out.println("✓ " + client.getNomBrut() + " — aucune alerte.");
            } else {
                System.out.println("⚠  " + client.getNomBrut()
                        + " — " + alertes.size() + " alerte(s) :");
                for (ResultatComparaison r : alertes) {
                    System.out.printf("     [%s] %-25s  score=%.3f  source=%s%n",
                            r.idEntreeListe, r.nomTrouve, r.score, r.source);
                }
                livreur.livrer(alertes);
            }
        }
        System.out.println("\nRésultats exportés → " + sortie_csv);
    }

    private static List<ListeControle> exempleListeControle() {
        return List.of(new ListeControle("demo", List.of(
                new EntreeList("001", "Ben Laden Usama",     "OFAC"),
                new EntreeList("002", "Bin Ladin Osama",     "ONU"),
                new EntreeList("003", "Al Qahtani Mohammed", "EU"),
                new EntreeList("004", "Dupont Alice Marie",  "LOCAL")
        )));
    }
}