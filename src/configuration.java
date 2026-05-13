package configuration;

import configuration.Pretraitement;
import configuration.Nettoyeur;
import configuration.SuppAcc;
import configuration.Normalisation;
import configuration.NormalisateurPhonetique;
import configuration.Decomposeur;

import comparateur.ComparateurNom;
import comparateur.ComparateurMeilleurPaire;
import comparateur.JaroWinkler;
import comparateur.Levenshtein;

import selection.Selectionneur;
import selection.SelectionAvancee;
import selection.TopKSelection;

import livraison.Livreur;
import livraison.LivreurConsole;
import livraison.LivreurCSV;

import java.util.List;

public class Configuration {

    private final PipelinePretraitement pipeline;
    private final ComparateurNom comparateurNom;
    private final Selectionneur selectionneur;
    private final Livreur livreur;

    private Configuration(List<Pretraitement> etapes,
                          ComparateurNom comparateurNom,
                          Selectionneur selectionneur,
                          Livreur livreur) {
        this.pipeline = new PipelinePretraitement(etapes);
        this.comparateurNom = comparateurNom;
        this.selectionneur = selectionneur;
        this.livreur = livreur;
    }

    public static Configuration defaut() {
        return new Configuration(
                List.of(
                        new Nettoyeur(),
                        new SuppAcc(),
                        new Normalisation(),
                        new NormalisateurPhonetique(),
                        new Decomposeur()
                ),
                new ComparateurMeilleurPaire(new JaroWinkler()),
                new SelectionAvancee(0.85),
                new LivreurConsole()
        );
    }

    public static Configuration avancee(String cheminCsv) {
        return new Configuration(
                List.of(
                        new Nettoyeur(),
                        new SuppAcc(),
                        new Normalisation(),
                        new NormalisateurPhonetique(),
                        new Decomposeur()
                ),
                new ComparateurMeilleurPaire(new Levenshtein()),
                new SelectionAvancee(0.80),
                new LivreurCSV(cheminCsv)
        );
    }

    public PipelinePretraitement getPipeline()       { return pipeline; }
    public ComparateurNom getComparateurNom()        { return comparateurNom; }
    public Selectionneur getSelectionneur()          { return selectionneur; }
    public Livreur getLivreur()                      { return livreur; }
}