import Moteur.ListeControle;
import Moteur.Nom;
import Moteur.ResultatComparaison;
import Moteur.EntreeList;
import Pretraiteur.Netyoeur;
import ComparateurNom.ComparateurMeilleurPaire;
import Selectionneur.SelectionAvancee;
import Livraison.LivreurCSV;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    // Seuil de similarité (0.0 → 1.0)
    private static final double SEUIL        = 0.75;
    private static final String SOURCE_PRIO  = "OFAC";
    private static final String SORTIE_CSV   = "resultats_kyc.csv";

    public static void main(String[] args) {

        // 1. Chargement de la liste de contrôle
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

        // 2. Initialisation des composants du pipeline
        Netyoeur            pretraiteur  = new Netyoeur();
        ComparateurMeilleurPaire comparateur = new ComparateurMeilleurPaire(
                (t1, t2) -> {
                    return jaccardSimple(t1, t2);
                }   // comparateur de chaînes léger
        );
        SelectionAvancee    selecteur    = new SelectionAvancee(SEUIL, SOURCE_PRIO);
        LivreurCSV          livreur      = new LivreurCSV(SORTIE_CSV);

        // 3. Clients à vérifier
        List<Nom> clients = List.of(
                new Nom("Ben Laden Oussama"),
                new Nom("Alice Dupont"),
                new Nom("Mohamed Al Qahtani")
        );

        // 4. Exécution
        System.out.println("\n========== Résultats KYC/AML ==========");
        for (Nom client : clients) {
            List<String> tokensClient = pretraiteur.traiter(client);
            client.setNomPretraite(tokensClient);

            List<ResultatComparaison> alertes = new ArrayList<>();

            for (ListeControle liste : listes) {
                for (EntreeList entree : liste.entrees) {
                    Nom nomEntree = new Nom(entree.nom, entree.id);
                    nomEntree.setNomPretraite(pretraiteur.traiter(nomEntree));

                    double score = comparateur.comparer(
                            client.getNomPretraite(),
                            nomEntree.getNomPretraite()
                    );

                    alertes.add(new ResultatComparaison(
                            entree.id,
                            client,
                            entree.nom,
                            score,
                            entree.source
                    ));
                }
            }

            // Filtrage par seuil + tri
            List<ResultatComparaison> alertesFiltrees = selecteur.filtrer(alertes);

            if (alertesFiltrees.isEmpty()) {
                System.out.println("✓ " + client.getNomBrut() + " — aucune alerte.");
            } else {
                System.out.println("⚠ " + client.getNomBrut() + " — " + alertesFiltrees.size() + " alerte(s) :");
                for (ResultatComparaison r : alertesFiltrees) {
                    System.out.printf("   [%s] %s  score=%.3f  source=%s%n",
                            r.idEntreeListe, r.nomTrouve, r.score, r.source);
                }
                livreur.livrer(alertesFiltrees);
            }
        }
        System.out.println("\nRésultats exportés → " + SORTIE_CSV);
    }

    // Comparateur de chaînes simple (Jaccard sur caractères bigrammes)
    private static double jaccardSimple(String a, String b) {
        if (a == null || b == null) return 0.0;
        if (a.equals(b)) return 1.0;
        java.util.Set<String> sa = bigrammes(a);
        java.util.Set<String> sb = bigrammes(b);
        if (sa.isEmpty() || sb.isEmpty()) return 0.0;
        long inter = sa.stream().filter(sb::contains).count();
        long union = sa.size() + sb.size() - inter;
        return (double) inter / union;
    }

    private static java.util.Set<String> bigrammes(String s) {
        java.util.Set<String> set = new java.util.HashSet<>();
        for (int i = 0; i < s.length() - 1; i++)
            set.add(s.substring(i, i + 2));
        return set;
    }

    // Données de démonstration
    private static List<ListeControle> exempleListeControle() {
        return List.of(new ListeControle("demo", List.of(
                new EntreeList("001", "Ben Laden Usama",    "OFAC"),
                new EntreeList("002", "Bin Ladin Osama",    "ONU"),
                new EntreeList("003", "Al Qahtani Mohammed","EU"),
                new EntreeList("004", "Dupont Alice Marie", "LOCAL")
        )));
    }
}