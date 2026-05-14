package main;

import configuration.Configuration;
import moteur.Moteur;
import model.Nom;
import model.ResultatComparaison;
import pretraitement.Decomposeur;
import pretraitement.Nettoyeur;
import pretraitement.Normalisation;
import pretraitement.NormalisateurPhonetique;
import pretraitement.SuppAcc;
import comparateur.ComparateurChaine;
import comparateur.ComparateurMeilleurPaire;
import comparateur.ComparateurNom;
import comparateur.ComparateurOrdrePositionnel;
import comparateur.ExacteChaine;
import comparateur.JaroWinkler;
import comparateur.Levenshtein;
import comparateur.ComparaisonExacteNom;
import livreur.LivreurCSV;
import selectionneur.SelectionAvancee;
import selectionneur.TopKSelection;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class Main {

    private Scanner         scanner;
    private List<Nom>       listeSanctions;
    private Configuration   configuration;
    private Moteur       moteur;

    public Main() {
        this.scanner        = new Scanner(System.in);
        this.listeSanctions = new ArrayList<>();
        this.configuration  = Configuration.defaut();
        this.moteur         = null;
    }

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        int choix = -1;
        while (choix != 0) {
            afficherMenu();
            choix = lireEntier("Votre choix : ");
            traiterChoix(choix);
        }
        scanner.close();
    }

    private void afficherMenu() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║        SYSTEME                     ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║  1. Charger liste de sanctions     ║");
        System.out.println("║  2. Vérifier un nom                ║");
        System.out.println("║  3. Vérifier un lot de noms        ║");
        System.out.println("║  4. Configuration                  ║");
        System.out.println("║  0. Quitter                        ║");
        System.out.println("╚════════════════════════════════════╝");
    }

    private void traiterChoix(int choix) {
        switch (choix) {
            case 1  -> gererChargementSanctions();
            case 2  -> gererVerificationNom();
            case 3  -> gererVerificationLot();
            case 4  -> gererConfiguration();
            case 0  -> System.out.println("Au revoir !");
            default -> System.out.println("Choix invalide.");
        }
    }

    private void gererChargementSanctions() {
        System.out.println("\n--- CHARGEMENT LISTE DE SANCTIONS ---");
        System.out.println("1. Saisie manuelle");
        System.out.println("2. Charger depuis CSV");
        int type = lireEntier("Type : ");

        if (type == 1) {
            listeSanctions.clear();
            System.out.println("Entrez les noms (tapez 'fin' pour terminer) :");
            while (true) {
                String nom = lireChaine("  Nom : ");
                if (nom.equalsIgnoreCase("fin")) break;
                listeSanctions.add(new Nom(nom));
            }
            System.out.println(" listeSanctions.size() + " nom(s) chargé(s).");
        } else if (type == 2) {
            String chemin = lireChaine("Chemin CSV : ");
            listeSanctions = chargerDepuisCSV(chemin);
            System.out.println("listeSanctions.size() + " nom(s) chargé(s) depuis " + chemin);
        } else {
            System.out.println("Option invalide.");
            return;
        }

        if (!listeSanctions.isEmpty()) {
            System.out.println(" Construction du moteur...");
            long debut = System.nanoTime();
            moteur = new Moteur(configuration, listeSanctions);
            long fin = System.nanoTime();
            long duree = (fin - debut) / 1_000_000;

            System.out.println(" Moteur prêt.");
            afficherTempsChargement(duree, listeSanctions.size());
        }
    }

    private void gererVerificationNom() {
        if (!verifierPret()) return;

        String saisie = lireChaine("\nNom à vérifier : ");

        long debut = System.nanoTime();
        List<ResultatComparaison> resultats = moteur.analyser(new Nom(saisie));
        long fin = System.nanoTime();
        long dureeMs = (fin - debut) / 1_000_000;

        if (resultats.isEmpty()) {
            System.out.println("Aucune correspondance trouvée.");
        } else {
            configuration.getLivreur().livrerAvecDetails(new Nom(saisie), resultats);
        }

        afficherTempsRecherche(saisie, dureeMs, moteur.getTailleListe());
        proposerExport(resultats);
    }

    private void gererVerificationLot() {
        if (!verifierPret()) return;

        System.out.println("\n--- VÉRIFICATION LOT ---");
        System.out.println("Entrez les noms à vérifier (tapez 'fin' pour terminer) :");

        List<Nom> clients = new ArrayList<>();
        while (true) {
            String nom = lireChaine("  Nom : ");
            if (nom.equalsIgnoreCase("fin")) break;
            clients.add(new Nom(nom));
        }

        if (clients.isEmpty()) {
            System.out.println(" Aucun nom saisi.");
            return;
        }

        List<ResultatComparaison> tousResultats = new ArrayList<>();
        long tempsTotal = 0;

        System.out.println("\n--- RÉSULTATS LOT (" + clients.size() + " noms) ---");

        for (Nom client : clients) {
            System.out.println("\n Client : " + client.getNomBrut());

            long debut = System.nanoTime();
            List<ResultatComparaison> resultats = moteur.analyser(client);
            long fin = System.nanoTime();
            long dureeMs = (fin - debut) / 1_000_000;
            tempsTotal += dureeMs;

            if (resultats.isEmpty()) {
                System.out.println("  Aucune correspondance.");
            }

            System.out.printf(" Temps de recherche : %d ms  (liste : %,d noms)%n",
                    dureeMs, moteur.getTailleListe());

            tousResultats.addAll(resultats);
        }

        System.out.println("\n--- RÉSUMÉ LOT ---");
        System.out.printf("  Noms vérifiés     : %d%n", clients.size());
        System.out.printf("  Correspondances   : %d%n", tousResultats.size());
        System.out.printf("  Temps total       : %d ms%n", tempsTotal);
        System.out.printf("  Temps moyen/nom   : %.1f ms%n",
                clients.isEmpty() ? 0.0 : (double) tempsTotal / clients.size());

        proposerExport(tousResultats);
    }

    private void gererConfiguration() {
        boolean continuer = true;

        while (continuer) {
            System.out.println("\n--- CONFIGURATION ---");
            System.out.println("1. Comparateur chaîne  (exact / jarowinkler / levenshtein)");
            System.out.println("2. Comparateur nom     (meilleurpaire / exact / positionnel)");
            System.out.println("3. Sélectionneur       (avancee / topk)");
            System.out.println("4. Prétraiteurs        (configurer la chaîne)");
            System.out.println("0. Retour");
            int choix = lireEntier("Choix : ");

            switch (choix) {
                case 1 -> configurerComparateurChaine();
                case 2 -> configurerComparateurNom();
                case 3 -> configurerSelectionneur();
                case 4 -> configurerPretraiteurs();
                case 0 -> { continuer = false; continue; }
                default -> { System.out.println(" Option invalide."); continue; }
            }

            if (!listeSanctions.isEmpty()) {
                System.out.println(" Reconstruction du moteur...");
                long debut = System.nanoTime();
                moteur = new Moteur(configuration, listeSanctions);
                long fin = System.nanoTime();
                long duree = (fin - debut) / 1_000_000;
                System.out.println(" Moteur mis à jour.");
                afficherTempsChargement(duree, listeSanctions.size());
            }
        }
    }

    private void configurerComparateurChaine() {
        String type = lireChaine("Type (exact/jarowinkler/levenshtein) : ");
        ComparateurChaine cc = switch (type.toLowerCase()) {
            case "exact"       -> new ExacteChaine();
            case "jarowinkler" -> new JaroWinkler();
            case "levenshtein" -> new Levenshtein();
            default -> {
                System.out.println("Inconnu, JaroWinkler utilisé par défaut.");
                yield new JaroWinkler();
            }
        };
        configuration.setComparateurChaine(cc);
        System.out.println("ComparateurChaîne mis à jour.");
    }

    private void configurerComparateurNom() {
        String type = lireChaine("Type (meilleurpaire/exact/positionnel) : ");
        ComparateurChaine cc = configuration.getComparateurChaine();
        ComparateurNom cn = switch (type.toLowerCase()) {
            case "meilleurpaire" -> new ComparateurMeilleurPaire(cc);
            case "exact"         -> new ComparaisonExacteNom();
            case "positionnel"   -> new ComparateurOrdrePositionnel(cc);
            default -> {
                System.out.println(" Inconnu, MeilleurPaire utilisé par défaut.");
                yield new ComparateurMeilleurPaire(cc);
            }
        };
        configuration.setComparateurNom(cn);
        System.out.println(" ComparateurNom mis à jour.");
    }

    private void configurerSelectionneur() {
        String type = lireChaine("Type (avancee/topk) : ");
        switch (type.toLowerCase()) {
            case "avancee" -> {
                double seuil  = lireDouble("Seuil (ex: 0.80) : ");
                String source = lireChaine("Source prioritaire (ex: SANCTIONS_LIST) : ");
                configuration.setSelectionneur(new SelectionAvancee(seuil, source));
                System.out.println(" Sélectionneur mis à jour.");
            }
            case "topk" -> {
                int k = lireEntier("K (ex: 5) : ");
                configuration.setSelectionneur(new TopKSelection(k));
                System.out.println(" Sélectionneur mis à jour.");
            }
            default -> System.out.println(" Type inconnu.");
        }
    }

    private void configurerPretraiteurs() {
        configuration.reinitialiserPretraiteurs();
        System.out.println("\n--- CONFIGURATION PRÉTRAITEURS ---");
        System.out.println("  nettoyeur     → Supprimer ponctuation, tirets");
        System.out.println("  accents       → Supprimer accents");
        System.out.println("  normalisation → Mettre en majuscules");
        System.out.println("  phonetique    → Normaliser variantes arabes");
        System.out.println("  decomposeur   → Supprimer particules (BEN, EL...)");
        System.out.println("  fin           → Terminer");

        int ordre = 1;
        while (true) {
            String type = lireChaine("Prétraiteur " + ordre + " : ");
            switch (type.toLowerCase()) {
                case "nettoyeur"     -> { configuration.ajouterPretraiteur(new Nettoyeur());               ordre++; }
                case "accents"       -> { configuration.ajouterPretraiteur(new SuppAcc());                 ordre++; }
                case "normalisation" -> { configuration.ajouterPretraiteur(new Normalisation());           ordre++; }
                case "phonetique"    -> { configuration.ajouterPretraiteur(new NormalisateurPhonetique()); ordre++; }
                case "decomposeur"   -> { configuration.ajouterPretraiteur(new Decomposeur());             ordre++; }
                case "fin"           -> { System.out.println(" (ordre-1) + " prétraiteur(s) configuré(s)."); return; }
                default              -> System.out.println("Prétraiteur inconnu : " + type);
            }
        }
    }

    // ── Affichage temps ──────────────────────────────────
    private void afficherTempsChargement(long dureeMs, int taille) {
        System.out.println("┌─────────────────────────────────────┐");
        System.out.printf( "│   Temps de chargement : %6d ms   │%n", dureeMs);
        System.out.printf( "│   Taille de la liste  : %,8d  │%n", taille);
        System.out.println("└─────────────────────────────────────┘");
    }

    private void afficherTempsRecherche(String nom, long dureeMs, int tailleListe) {
        System.out.println("┌─────────────────────────────────────┐");
        System.out.printf( "│   Nom recherché : %-16s │%n", nom.length() > 16 ? nom.substring(0, 16) : nom);
        System.out.printf( "│  Temps de recherche : %5d ms   │%n", dureeMs);
        System.out.printf( "│   Liste : %,8d noms          │%n", tailleListe);
        System.out.println("└─────────────────────────────────────┘");
    }

    // ── Export CSV ───────────────────────────────────────
    private void proposerExport(List<ResultatComparaison> resultats) {
        if (resultats == null || resultats.isEmpty()) return;
        String rep = lireChaine("\nExporter en CSV ? (o/n) : ");
        if (rep.equalsIgnoreCase("o")) {
            String chemin = lireChaine("Chemin fichier : ");
            new LivreurCSV(chemin).livrer(resultats);
            System.out.println(" Résultats exportés vers " + chemin);
        }
    }

    private List<Nom> chargerDepuisCSV(String chemin) {
        List<Nom> noms = new ArrayList<>();
        Path path = Paths.get(chemin);

        if (!Files.exists(path)) {
            System.err.println(" Fichier introuvable : " + chemin);
            return noms;
        }

        try (java.io.BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String ligne;
            boolean premiereLigne = true;
            while ((ligne = br.readLine()) != null) {
                if (ligne.isBlank()) continue;
                String[] colonnes = ligne.split(",");
                if (premiereLigne && colonnes.length >= 2
                        && colonnes[0].trim().equalsIgnoreCase("id")
                        && colonnes[1].trim().equalsIgnoreCase("name")) {
                    premiereLigne = false;
                    continue;
                }
                premiereLigne = false;
                if (colonnes.length >= 2) {
                    String id  = colonnes[0].trim();
                    String nom = String.join(",", java.util.Arrays.copyOfRange(colonnes, 1, colonnes.length)).trim();
                    noms.add(new Nom(nom, id));
                } else {
                    noms.add(new Nom(ligne.trim()));
                }
            }
        } catch (java.io.IOException e) {
            System.err.println(" Erreur lecture CSV : " + e.getMessage());
        }
        return noms;
    }

    private boolean verifierPret() {
        if (listeSanctions.isEmpty()) {
            System.out.println(" Aucune liste de sanctions chargée (option 1).");
            return false;
        }
        if (moteur == null) {
            moteur = new Moteur(configuration, listeSanctions);
        }
        return true;
    }
    private int lireEntier(String msg) {
        System.out.print(msg);
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { return -1; }
    }

    private double lireDouble(String msg) {
        System.out.print(msg);
        try { return Double.parseDouble(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { return 0.80; }
    }

    private String lireChaine(String msg) {
        System.out.print(msg);
        return scanner.nextLine().trim();
    }
}
