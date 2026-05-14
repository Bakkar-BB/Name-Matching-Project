package candidat;

import model.Nom;

import java.util.*;


public class GenerateurCandidatsArbre extends AbstractCandidateGenerator {

    private final int maxDistance;
    private final Node root;


    private static class Node {
        final String cle;     
        final Nom    nom;  
        double       mu;
        Node         left;    
        Node         right;   

        Node(String cle, Nom nom) {
            this.cle = cle;
            this.nom = nom;
        }
    }

   
    public GenerateurCandidatsArbre(List<Nom> listeSanctions, int maxDistance) {
        this.maxDistance = Math.max(0, maxDistance);
        this.root        = construire(listeSanctions);
    }

    
    private Node construire(List<Nom> noms) {
        if (noms == null || noms.isEmpty()) return null;

        
        Map<String, Nom> map = new LinkedHashMap<>();
        for (Nom n : noms) {
            map.putIfAbsent(normaliser(n), n);
        }
        return buildNode(new ArrayList<>(map.keySet()), map);
    }

    private Node buildNode(List<String> cles, Map<String, Nom> map) {
        if (cles.isEmpty()) return null;
        if (cles.size() == 1) return new Node(cles.get(0), map.get(cles.get(0)));

       
        String vpCle = cles.get(0);
        Node   node  = new Node(vpCle, map.get(vpCle));

        List<String> reste = cles.subList(1, cles.size());

       
        int[] distances = new int[reste.size()];
        for (int i = 0; i < reste.size(); i++) {
            distances[i] = levenshtein(vpCle, reste.get(i));
        }

   
        int[] tries = distances.clone();
        Arrays.sort(tries);
        node.mu = tries[tries.length / 2];

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


        if (dist - maxDistance <= node.mu) rechercherNode(node.left,  requete, resultats);
        if (dist + maxDistance >  node.mu) rechercherNode(node.right, requete, resultats);
    }


    private String normaliser(Nom nom) {
        List<String> tokens = new ArrayList<>(getTokens(nom));
        Collections.sort(tokens);
        return String.join(" ", tokens);
    }

 
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
