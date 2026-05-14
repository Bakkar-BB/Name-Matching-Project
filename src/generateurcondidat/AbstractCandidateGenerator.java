package candidat;

import model.Nom;
import java.util.List;

public abstract class AbstractCandidateGenerator implements CandidateGenerator {
    
    protected List<String> getTokens(Nom nom) {
        return nom.getNomPretraite();
    }
    
    @Override
    public abstract List<Nom> generateCandidates(Nom nomClient);
}
