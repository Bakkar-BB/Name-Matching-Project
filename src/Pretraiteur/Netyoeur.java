package Pretraiteur;


import Moteur.Nom;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
public class Netyoeur implements  Pretraiteur{
    @Override
    public List<String> traiter (Nom nom){
        String texte = nom.getNomBrut()
                .replaceAll("[^\\p{L}\\s]", "")   // garde uniquement lettres + espaces
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase();
        return split(texte);
    }
    private List<String> split(String s) {
        if (s.isBlank()) return List.of();
        return Arrays.asList(s.split("\\s+"));
    }
}
