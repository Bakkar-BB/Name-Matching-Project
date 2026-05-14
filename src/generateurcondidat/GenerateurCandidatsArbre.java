package candidat;

import model.Nom;

import java.util.*;

/**
 * Génère des candidats via un VP-Tree (Vantage Point Tree) basé sur
 * la distance de Levenshtein sur les noms normalisés (tokens triés).
 *
 * L'arbre est construit une seule fois au constructeur.
 * Lors de la recherche, tous les Noms dont la distance ≤ maxDistance
 * sont retournés comme candidats.
 */
public class GenerateurCandidatsArbre extends AbstractCandidateGenerator {

    private final int maxDistance;
    private final Node root;

    // ── Nœud interne du VP-Tree ──────────────────────────
    private static class Node {
        final String cle;     // nom normalisé (tokens triés + joints)
        final Nom    nom;     // référence au Nom original
        double       mu;      // médiane des distances (seuil de partition)
        Node         left;    // sous-arbre : distance ≤ mu
        Node         right;   // sous-arbre : distance > mu

        Node(String cle, Nom nom) {
            this.cle = cle;
            this.nom = nom;
        }
    }

    // ── Constructeur ────────────────────────────────────
    public GenerateurCandidatsArbre(List<Nom> listeSanctions, int maxDistance) {
        this.maxDistance = Math.max(0, maxDistance);
        this.root        = construire(listeSanctions);
    }

    // ── Construction du VP-Tree ──────────────────────────
    private Node construire(List<Nom> noms) {
        if (noms == null || noms.isEmpty()) return null;

        // Dédoublonnage sur la clé normalisée
        Map<String, Nom> map = new LinkedHashMap<>();
        for (Nom n : noms) {
            map.putIfAbsent(normaliser(n), n);
        }
        return buildNode(new ArrayList<>(map.keySet()), map);
    }

    private Node buildNode(List<String> cles, Map<String, Nom> map) {
        if (cles.isEmpty()) return null;
        if (cles.size() == 1) return new Node(cles.get(0), map.get(cles.get(0)));

        // Le premier élément est le vantage point
        String vpCle = cles.get(0);
        Node   node  = new Node(vpCle, map.get(vpCle));

        List<String> reste = cles.subList(1, cles.size());

        // Calcul des distances depuis le vantage point
        int[] distances = new int[reste.size()];
        for (int i = 0; i < reste.size(); i++) {
            distances[i] = levenshtein(vpCle, reste.get(i));
        }

        // La médiane devient le seuil mu
        int[] tries = distances.clone();
        Arrays.sort(tries);
        node.mu = tries[tries.length / 2];

        // Partition gauche (≤ mu) et droite (> mu)
        List<String> gauche = new ArrayList<>();
        List<String> droite = new ArrayList<>();
        for (int i = 0; i < reste.size(); i++) {
            if (distances[i] <= node.mu) gauche.add(reste.get(i));
            else                         droite.add(reste.get(i));
        }

        node.left  = buildNode(gauche, map);
        node.right = buildNode(droite, map);
        return node;
    }

    // ── Recherche dans le VP-Tree ────────────────────────
    @Override
    public List<Nom> generateCandidates(Nom nomClient) {
        List<Nom> resultats = new ArrayList<>();
        rechercherNode(root, normaliser(nomClient), resultats);
        return resultats;
    }

    private void rechercherNode(Node node, String requete, List<Nom> resultats) {
        if (node == null) return;

        int dist = levenshtein(node.cle, requete);
        if (dist <= maxDistance) resultats.add(node.nom);

        // Élagage : on explore les branches selon la distance
        if (dist - maxDistance <= node.mu) rechercherNode(node.left,  requete, resultats);
        if (dist + maxDistance >  node.mu) rechercherNode(node.right, requete, resultats);
    }

    // ── Normalisation : tokens triés et joints ───────────
    private String normaliser(Nom nom) {
        List<String> tokens = new ArrayList<>(getTokens(nom));
        Collections.sort(tokens);
        return String.join(" ", tokens);
    }

    // ── Distance de Levenshtein ──────────────────────────
    private static int levenshtein(String a, String b) {
        int la = a.length(), lb = b.length();
        int[][] dp = new int[la + 1][lb + 1];
        for (int i = 0; i <= la; i++) dp[i][0] = i;
        for (int j = 0; j <= lb; j++) dp[0][j] = j;
        for (int i = 1; i <= la; i++) {
            for (int j = 1; j <= lb; j++) {
                int cout = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i-1][j] + 1, dp[i][j-1] + 1),
                        dp[i-1][j-1] + cout
                );
            }
        }
        return dp[la][lb];
    }

    @Override
    public String getName() { return "GenerateurArbre"; }
}
