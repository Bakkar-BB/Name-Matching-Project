package moteur;

import configuration.Configuration;
import comparateur.ComparateurNom;
import model.Nom;
import model.ResultatComparaison;
import candidat.GenerateurCandidatsInvertedIndex;
import pretraitement.PipelinePretraitement;

import java.util.ArrayList;
import java.util.List;

public class Moteur {

    private static final int DEFAULT_MIN_TOKENS_COMMUNS = 1;

    private final Configuration configuration;
    private final PipelinePretraitement pipeline;
    private final List<Nom> listeSanctions;
    private final GenerateurCandidatsInvertedIndex generateurCandidats;

    // Temps de chargement de la liste — accessible après construction
    private final long tempsChargementMs;

    public Moteur(Configuration configuration, List<Nom> listeSanctions) {
        this.configuration = configuration;
        this.pipeline = configuration.getPipeline();

        long debut = System.nanoTime();

        // Prétraitement de la liste de sanctions — une seule fois
        this.listeSanctions = new ArrayList<>();
        for (Nom sanction : listeSanctions) {
            Nom copie = new Nom(sanction.getNomBrut(), sanction.getIdNom());
            pipeline.traiter(copie);
            this.listeSanctions.add(copie);
        }

        // Construction de l'index inversé — une seule fois
        this.generateurCandidats = new GenerateurCandidatsInvertedIndex(this.listeSanctions, DEFAULT_MIN_TOKENS_COMMUNS);

        long fin = System.nanoTime();
        this.tempsChargementMs = (fin - debut) / 1_000_000;
    }

    public List<ResultatComparaison> analyser(Nom nomClient) {
        if (nomClient == null) return List.of();

        Nom clientTraite = pipeline.traiter(new Nom(nomClient.getNomBrut(), nomClient.getIdNom()));
        ComparateurNom comparateurNom = configuration.getComparateurNom();

        List<Nom> candidats = generateurCandidats.generateCandidates(clientTraite);
        if (candidats.isEmpty()) return List.of();

        List<ResultatComparaison> resultats = new ArrayList<>();
        for (Nom sanction : candidats) {
            double score = comparateurNom.comparer(
                    clientTraite.getNomPretraite(),
                    sanction.getNomPretraite()
            );
            if (score > 0.0) {
                resultats.add(new ResultatComparaison(
                        sanction.getIdNom(),
                        nomClient,
                        sanction.getNomBrut(),
                        score,
                        "SANCTIONS_LIST"
                ));
            }
        }

        return configuration.getSelectionneur().filtrer(resultats);
    }

    public long getTempsChargementMs() { return tempsChargementMs; }
    public int getTailleListe()        { return listeSanctions.size(); }
}
