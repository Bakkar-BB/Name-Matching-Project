package pretraitement;

import java.util.List;
import model.Nom;

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
        this.etapes = List.copyOf(etapes);
    }

    public List<Pretraitement> getEtapes() {
        return etapes;
    }

    public Nom traiter(Nom nom) {
        Nom courant = nom;
        for (Pretraitement etape : etapes) {
            courant.setNomPretraite(etape.traiter(courant));
        }
        return courant;
    }
}
