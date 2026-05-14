package configuration;

import pretraitement.Decomposeur;
import pretraitement.Nettoyeur;
import pretraitement.Normalisation;
import pretraitement.NormalisateurPhonetique;
import pretraitement.PipelinePretraitement;
import pretraitement.Pretraitement;
import pretraitement.SuppAcc;
import comparateur.ComparateurChaine;
import comparateur.ComparateurMeilleurPaire;
import comparateur.ComparateurNom;
import comparateur.JaroWinkler;
import comparateur.Levenshtein;
import livreur.Livreur;
import livreur.LivreurConsole;
import livreur.LivreurCSV;
import selectionneur.SelectionAvancee;
import selectionneur.Selectionneur;
import selectionneur.TopKSelection;

import java.util.ArrayList;
import java.util.List;

public class Configuration {

    private PipelinePretraitement pipeline;
    private ComparateurChaine comparateurChaine;
    private ComparateurNom comparateurNom;
    private Selectionneur selectionneur;
    private Livreur livreur;

    private Configuration(List<Pretraitement> etapes,
                          ComparateurChaine comparateurChaine,
                          ComparateurNom comparateurNom,
                          Selectionneur selectionneur,
                          Livreur livreur) {
        this.pipeline = new PipelinePretraitement(etapes);
        this.comparateurChaine = comparateurChaine;
        this.comparateurNom = comparateurNom;
        this.selectionneur = selectionneur;
        this.livreur = livreur;
    }

    public static Configuration defaut() {
        ComparateurChaine defaultComparateur = new JaroWinkler();
        return new Configuration(
                List.of(
                        new Nettoyeur(),
                        new SuppAcc(),
                        new Normalisation(),
                        new NormalisateurPhonetique(),
                        new Decomposeur()
                ),
                defaultComparateur,
                new ComparateurMeilleurPaire(defaultComparateur),
                new SelectionAvancee(0.85, "SANCTIONS_LIST"),
                new LivreurConsole()
        );
    }

    public static Configuration avancee(String cheminCsv) {
        ComparateurChaine defaultComparateur = new Levenshtein();
        return new Configuration(
                List.of(
                        new Nettoyeur(),
                        new SuppAcc(),
                        new Normalisation(),
                        new NormalisateurPhonetique(),
                        new Decomposeur()
                ),
                defaultComparateur,
                new ComparateurMeilleurPaire(defaultComparateur),
                new SelectionAvancee(0.80, "SANCTIONS_LIST"),
                new LivreurCSV(cheminCsv)
        );
    }

    public PipelinePretraitement getPipeline() { return pipeline; }
    public ComparateurChaine getComparateurChaine() { return comparateurChaine; }
    public ComparateurNom getComparateurNom() { return comparateurNom; }
    public Selectionneur getSelectionneur() { return selectionneur; }
    public Livreur getLivreur() { return livreur; }

    public void setComparateurChaine(ComparateurChaine comparateurChaine) {
        this.comparateurChaine = comparateurChaine;
    }

    public void setComparateurNom(ComparateurNom comparateurNom) {
        this.comparateurNom = comparateurNom;
    }

    public void setSelectionneur(Selectionneur selectionneur) {
        this.selectionneur = selectionneur;
    }

    public void setLivreur(Livreur livreur) {
        this.livreur = livreur;
    }

    public void reinitialiserPretraiteurs() {
        this.pipeline = new PipelinePretraitement(new ArrayList<>());
    }

    public void ajouterPretraiteur(Pretraitement pretraitement) {
        List<Pretraitement> etapes = new ArrayList<>(pipeline.getEtapes());
        etapes.add(pretraitement);
        this.pipeline = new PipelinePretraitement(etapes);
    }
}
