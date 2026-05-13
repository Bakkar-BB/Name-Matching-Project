package Pretraitement;

import model.Nom;
import java.util.List;

public class PipelinePretraitement {
    private final List<Pretraitement> etapes;
    public PipelinePretraitement() {
        this.etapes = List.of(
                new Nettoyeur(),
                new SuppAcc(),
                new Normalisation(),
                new NormalisateurPhonetique(),
                new Decomposeur()
        );
    }
    public PipelinePretraitement(List<Pretraitement> etapes) {
        this.etapes = etapes;
    }
    public Nom traiter(Nom nom) {
        for (Pretraitement etape : etapes) {
            List<String> resultat = etape.traiter(nom);
            nom.setNomPretraite(resultat);
        }
        return nom;
    }
}
