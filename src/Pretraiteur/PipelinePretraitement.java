package kyc.Pretraitement;

import kyc.model.Nom;
import java.util.List;

/**
 * Chains all preprocessing steps in the correct order.
 *
 * Pipeline order:
 *   1. Nettoyeur               → remove punctuation, hyphens→spaces, extra spaces, lowercase
 *   2. SuppAcc                 → remove accents (é→e, ñ→n, ü→u, ç→c)
 *   3. Normalisation           → uppercase, trim each token
 *   4. NormalisateurPhonetique → collapse transliteration variants (MOHAMMED→MOHAMMAD)
 *   5. Decomposeur             → remove particles (BEN, EL, VAN, DE...)
 *
 * Each step reads from nomPretraite and writes back to it,
 * so every step builds on the previous one's output.
 *
 * Usage:
 *   PipelinePretraitement pipeline = new PipelinePretraitement();
 *   Nom nom = pipeline.traiter(new Nom("jean-pierre éL ben Salah"));
 *   // nom.getNomPretraite() → ["JEAN", "PIERRE", "SALAH"]
 */
public class PipelinePretraitement {

    private final List<Pretraitement> etapes;

    /**
     * Default pipeline — uses the standard order of all steps.
     */
    public PipelinePretraitement() {
        this.etapes = List.of(
                new Nettoyeur(),
                new SuppAcc(),
                new Normalisation(),
                new NormalisateurPhonetique(),
                new Decomposeur()
        );
    }

    /**
     * Custom pipeline — inject your own steps and order.
     * Useful for testing individual steps or adding new ones.
     *
     * @param etapes ordered list of preprocessing steps
     */
    public PipelinePretraitement(List<Pretraitement> etapes) {
        this.etapes = etapes;
    }

    /**
     * Runs the full preprocessing pipeline on a name.
     *
     * Each step:
     *   - receives the Nom object (reads nomPretraite internally)
     *   - returns a new List<String>
     *   - that list is written back to nomPretraite before the next step runs
     *
     * @param nom the raw Nom to preprocess
     * @return the same Nom with nomPretraite fully populated
     */
    public Nom traiter(Nom nom) {
        for (Pretraitement etape : etapes) {
            List<String> resultat = etape.traiter(nom);
            nom.setNomPretraite(resultat);
        }
        return nom;
    }
}