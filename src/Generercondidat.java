package kyc.comparateur;

import kyc.model.Nom;
import java.util.List;
import java.util.ArrayList;

public class GenerateurCandidats {
    private final int minTokensCommuns;

    public GenerateurCandidats(int minTokensCommuns) {
        this.minTokensCommuns = minTokensCommuns;
    }

    public List<Nom> genererCandidats(Nom nomClient, List<Nom> listeSanctions) {
        List<Nom> candidats = new ArrayList<>();

        List<String> tokensClient = nomClient.getNomPretraite().isEmpty()
                ? List.of(nomClient.getNomBrut().trim().toUpperCase().split("\\s+"))
                : nomClient.getNomPretraite();

        for (Nom nomListe : listeSanctions) {
            List<String> tokensListe = nomListe.getNomPretraite().isEmpty()
                    ? List.of(nomListe.getNomBrut().trim().toUpperCase().split("\\s+"))
                    : nomListe.getNomPretraite();

            int communs = compterTokensCommuns(tokensClient, tokensListe);
            if (communs >= minTokensCommuns) {
                candidats.add(nomListe);
            }
        }
        return candidats;
    }
    private int compterTokensCommuns(List<String> tokens1, List<String> tokens2) {
        int count = 0;
        for (String t1 : tokens1) {
            for (String t2 : tokens2) {
                if (t1.equalsIgnoreCase(t2)) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }
}