package pretraitement;

import java.util.List;
import java.util.stream.Collectors;
import model.Nom;

public class Decomposeur implements Pretraitement {
    private static final List<String> PARTICULES = List.of(
            "DE", "DU", "DES", "LE", "LA", "LES", "DIT",
            "VAN", "VON", "DER", "AL", "EL", "BEN", "BIN", "BINTE", "ABU", "UM", "BOU",
            "DEL", "LAS", "LOS", "DI", "DA", "DELLO", "DELLA"
    );

    @Override
    public List<String> traiter(Nom nom) {
        List<String> source = nom.getNomPretraite();
        if (source.isEmpty()) {
            return List.of();
        }
        return source.stream()
                .filter(token -> token != null && !token.isBlank())
                .filter(token -> !PARTICULES.contains(token.toUpperCase()))
                .collect(Collectors.toList());
    }
}
