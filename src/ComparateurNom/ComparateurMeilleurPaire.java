package ComparateurNom;
import java.util.List;
public class ComparateurMeilleurPaire implements ComparateurNom{
    private final ComparateurChaine comparateurChaine;

    ComparateurMeilleurPaire(ComparateurChaine comparateurChaine){
        this.comparateurChaine=comparateurChaine;
    }
    @Override
    public double comparer( List<String> tokens1, List<String> tokens2){
        if (tokens1 == null )
    }
}
