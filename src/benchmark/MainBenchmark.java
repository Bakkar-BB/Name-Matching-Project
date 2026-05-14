package benchmark;

import pretraitement.PipelinePretraitement;
import candidat.CandidateGenerator;
import configuration.Configuration;
import comparateur.CompositeComparateurNom;
import comparateur.ComparateurMeilleurPaire;
import comparateur.ComparaisonExacteNom;
import comparateur.ComparateurNom;
import comparateur.ComparateurOrdrePositionnel;
import model.Nom;
import model.ResultatComparaison;
import selectionneur.SelectionAvancee;
import selectionneur.Selectionneur;
import selectionneur.TopKSelection;
import candidat.GenerateurCandidatsInvertedIndex;
import candidat.GenerateurCandidatsArbre;
import candidat.GenerateurCandidatsSoundex;
import candidat.GenerateurCandidatsLinkedHashSet;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class MainBenchmark {

    private static final List<String> DEFAULT_DATASETS       = List.of(
            "src/peps_names_100.csv",
            "src/peps_names_256k.csv"
    );
    private static final String DEFAULT_OUTPUT_CSV   = "benchmark_results.csv";
    private static final int    DEFAULT_QUERY_COUNT  = 20;
    private static final int    DEFAULT_MIN_TOKENS   = 1;
    private static final int    DEFAULT_MAX_DISTANCE = 2;

    public static void main(String[] args) throws Exception {
        List<String> datasetPaths = args.length > 0 ? Arrays.asList(args) : DEFAULT_DATASETS;

        List<BenchmarkResult> results = new ArrayList<>();
        for (String path : datasetPaths) {
            Path dataset = Paths.get(path);
            if (!Files.exists(dataset)) { System.out.println("⚠ Introuvable : " + dataset); continue; }
            System.out.println("Chargement : " + dataset);
            List<Nom> sanctions = loadSanctions(dataset);
            if (sanctions.isEmpty()) { System.out.println("⚠ Vide : " + dataset); continue; }
            results.addAll(runBenchmark(sanctions, dataset.getFileName().toString()));
        }

        if (results.isEmpty()) { System.out.println("Aucun benchmark exécuté."); return; }
        writeCsv(DEFAULT_OUTPUT_CSV, results);
        printSummary(results, DEFAULT_OUTPUT_CSV);
    }

    private static List<Nom> loadSanctions(Path path) throws Exception {
        List<Nom> sanctions = new ArrayList<>();
        boolean premiere = true;
        for (String ligne : Files.readAllLines(path, StandardCharsets.UTF_8)) {
            if (ligne == null || ligne.isBlank()) continue;
            String[] cols = ligne.split(",", -1);
            if (premiere && cols.length >= 2
                    && cols[0].trim().equalsIgnoreCase("id")
                    && cols[1].trim().equalsIgnoreCase("name")) {
                premiere = false; continue;
            }
            premiere = false;
            if (cols.length >= 2) sanctions.add(new Nom(cols[1].trim(), cols[0].trim()));
            else                  sanctions.add(new Nom(ligne.trim()));
        }
        return sanctions;
    }

    private static List<BenchmarkResult> runBenchmark(List<Nom> sanctions, String datasetName) {
        Configuration         config   = Configuration.defaut();
        PipelinePretraitement pipeline = config.getPipeline();

        List<Nom> sanctionsTraitees = sanctions.stream()
                .map(n -> pipeline.traiter(new Nom(n.getNomBrut(), n.getIdNom())))
                .collect(Collectors.toList());

        List<Nom> queries = sanctionsTraitees.size() <= DEFAULT_QUERY_COUNT
                ? new ArrayList<>(sanctionsTraitees)
                : sanctionsTraitees.subList(0, DEFAULT_QUERY_COUNT);

        List<CandidateGenerator> generators = List.of(
                new GenerateurCandidatsInvertedIndex(sanctionsTraitees, DEFAULT_MIN_TOKENS),
                new GenerateurCandidatsArbre(sanctionsTraitees,         DEFAULT_MAX_DISTANCE),
                new GenerateurCandidatsSoundex(sanctionsTraitees),
                new GenerateurCandidatsLinkedHashSet(sanctionsTraitees)
        );

        List<ComparateurNom> comparateurs = List.of(
                new ComparateurMeilleurPaire(config.getComparateurChaine()),
                new ComparaisonExacteNom(),
                new ComparateurOrdrePositionnel(config.getComparateurChaine())
        );
        List<String> comparateurNoms = List.of(
                "ComparateurMeilleurPaire", "ComparaisonExacteNom", "ComparateurOrdrePositionnel"
        );

        List<Selectionneur> selectionneurs    = List.of(new SelectionAvancee(0.85, "SANCTIONS_LIST"), new TopKSelection(10));
        List<String>        selectionneurNoms = List.of("SelectionAvancee", "TopKSelection");

        List<BenchmarkResult> results = new ArrayList<>();
        for (CandidateGenerator generator : generators) {
            for (int i = 0; i < comparateurs.size(); i++) {
                for (int j = i + 1; j < comparateurs.size(); j++) {
                    for (int k = 0; k < selectionneurs.size(); k++) {
                        results.add(executePipeline(
                                generator,
                                comparateurNoms.get(i), comparateurNoms.get(j),
                                new CompositeComparateurNom(comparateurs.get(i), comparateurs.get(j)),
                                selectionneurs.get(k),
                                sanctionsTraitees, queries, datasetName
                        ));
                    }
                }
            }
        }
        return results;
    }

    private static BenchmarkResult executePipeline(
            CandidateGenerator generator,
            String comp1Name, String comp2Name,
            ComparateurNom comparateur,
            Selectionneur selectionneur,
            List<Nom> sanctionsTraitees, List<Nom> queries, String datasetName) {

        long totalCandidats = 0, totalResultats = 0;
        long start = System.nanoTime();

        for (Nom query : queries) {
            List<Nom> candidats = generator.generateCandidates(query);
            totalCandidats += candidats.size();
            List<ResultatComparaison> resultats = new ArrayList<>();
            for (Nom sanction : candidats) {
                double score = comparateur.comparer(query.getNomPretraite(), sanction.getNomPretraite());
                if (score > 0.0)
                    resultats.add(new ResultatComparaison(sanction.getIdNom(), query, sanction.getNomBrut(), score, "SANCTIONS_LIST"));
            }
            totalResultats += selectionneur.filtrer(resultats).size();
        }
        long runtimeMs = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("%-30s | %-25s + %-25s | %-20s | %5dms | cand=%d | res=%d%n",
                generator.getName(), comp1Name, comp2Name,
                selectionneur.getClass().getSimpleName(),
                runtimeMs, totalCandidats, totalResultats);

        return new BenchmarkResult(
                generator.getName(), comp1Name, comp2Name,
                selectionneur.getClass().getSimpleName(),
                runtimeMs, totalCandidats, totalResultats,
                sanctionsTraitees.size(), queries.size(), datasetName
        );
    }

    private static void writeCsv(String outputCsv, List<BenchmarkResult> results) throws Exception {
        try (var writer = Files.newBufferedWriter(Paths.get(outputCsv), StandardCharsets.UTF_8)) {
            writer.write("generator,comparateur1,comparateur2,selectionneur,runtimeMs,totalCandidates,totalResults,sanctionsSize,querySize,datasetName");
            writer.newLine();
            for (BenchmarkResult r : results) { writer.write(r.toCsvLine()); writer.newLine(); }
        }
    }

    private static void printSummary(List<BenchmarkResult> results, String outputCsv) {
        System.out.println("\n=== Benchmark terminé ===");
        System.out.println("Fichier CSV : " + outputCsv + " | Combinaisons : " + results.size());
        results.forEach(r -> System.out.printf("%s | %s + %s | %s => %dms | cand=%d | res=%d%n",
                r.generatorName, r.comparateur1, r.comparateur2,
                r.selectionneur, r.runtimeMs, r.totalCandidates, r.totalResults));
    }
}