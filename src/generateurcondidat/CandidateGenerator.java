package candidat;

import model.Nom;
import java.util.List;

public interface CandidateGenerator {
    List<Nom> generateCandidates(Nom nomClient);
    String getName();
}