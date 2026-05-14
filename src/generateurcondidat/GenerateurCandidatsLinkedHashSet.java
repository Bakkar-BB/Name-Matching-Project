package candidat;

import model.Nom;

import java.util.*;


public class GenerateurCandidatsLinkedHashSet extends AbstractCandidateGenerator {

    private final List<Nom> listeSanctions;

    public GenerateurCandidatsLinkedHashSet(List<Nom> listeSanctions) {
        this.listeSanctions = new ArrayList<>(listeSanctions);
    }

    @Override
    public List<Nom> generateCandidates(Nom nomClient) {
      
        Set<String> tokensClient = new LinkedHashSet<>(getTokens(nomClient));

       
        Set<Nom>    vus      = new LinkedHashSet<>();
        List<Nom>   candidats = new ArrayList<>();

        for (Nom sanction : listeSanctions) {
            if (vus.contains(sanction)) continue;

            List<String> tokensSanction = getTokens(sanction);

           
            List<String> propres = new ArrayList<>();
            for (String t : tokensSanction) {
                if (t != null && !t.isBlank()) propres.add(t.trim());
            }

            for (String token : propres) {
                if (tokensClient.contains(token)) {
                    vus.add(sanction);
                    candidats.add(sanction);
                    break; 
                }
            }
        }

        return Collections.unmodifiableList(candidats);
    }

    @Override
    public String getName() { return "GenerateurLinkedHashSet"; }
}
