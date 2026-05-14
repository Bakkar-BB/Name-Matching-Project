# Name Matching Engine

## Overview

Name Matching Engine is a Java-based application inspired by real Know Your Customer (KYC) systems used in banking and compliance. The project focuses on intelligent name preprocessing, candidate generation, similarity comparison, and result selection using multiple algorithms and object-oriented design principles.

The application allows users to enter a name, preprocess it, generate possible matching candidates from large datasets, compare similarity scores using different algorithms, and return the best results.

---

# Features

- Name preprocessing pipeline
- Candidate generation using multiple strategies
- String similarity comparison algorithms
- Advanced selection and ranking system
- Modular OOP architecture
- Benchmark system for performance evaluation
- CSV dataset support
- Polymorphism-based design

---

# Technologies Used

- Java
- Object-Oriented Programming (OOP)
- Collections Framework
- CSV Processing
- Levenshtein Algorithm
- Jaro-Winkler Similarity
- Soundex Normalization

---

# Project Architecture

The application follows this processing pipeline:

```text
User Input
    ↓
Preprocessing
    ↓
Candidate Generation
    ↓
Similarity Comparison
    ↓
Selection & Ranking
    ↓
Result Delivery
```

---

# Main Modules

## 1. Preprocessing

Responsible for cleaning and normalizing names before comparison.

### Implemented preprocessing strategies:

- `Nettoyeur`
- `SuppAcc`
- `Decomposeur`
- `Normalisation`
- `NormalisateurPhonetique`
- `PipelinePretraitement`

---

## 2. Candidate Generation

### Implemented generators:

- `GenerateurCandidatsArbre`
- `GenerateurCandidatsInvertedIndex`
- `GenerateurCandidatsLinkedHashSet`
- `GenerateurCandidatsSoundex`

---

## 3. Similarity Comparison

### String comparison algorithms:

- `Levenshtein`
- `JaroWinkler`
- `ExacteChaine`

### Name comparison strategies:

- `ComparaisonExacteNom`
- `ComparateurMeilleurPaire`
- `ComparateurOrdrePositionnel`
- `CompositeComparateurNom`

---

## 4. Selection System

### Selection strategies:

- `SelectionAvancee`
- `TopKSelection`

---

## 5. Delivery System

### Delivery methods:

- `LivreurConsole`
- `LivreurCSV`

---

# Project Structure

```text
src/
│
├── benchmark/
├── ComparateurChaine/
├── ComparateurNom/
├── generateurcondidat/
├── Livraison/
├── Model/
├── Pretraitement/
├── Selectionneur/
├── Main.java
├── Moteur.java
└── datasets CSV
```

---

# Example Workflow

## Input

```text
Mohamed Ben Ali
```

## Generated Candidate

```text
Mohamed Benali
```

## Similarity Score

```text
92%
```

---

# Benchmark System

Datasets:

- `peps_names_800.csv`
- `peps_names_100.csv`
- `peps_names_256k.csv`
- `peps_names_512k.csv`

Benchmark classes:

- `BenchmarkResult`
- `MainBenchmark`

---

# Installation

## Clone the repository

```bash
git clone https://github.com/your-username/your-repository.git
```

## Open the project

Use:
- Visual Studio Code
- IntelliJ IDEA
- Eclipse

---

# Run the Project

```bash
javac Main.java
java Main
```

---

# Object-Oriented Concepts Used

- Abstraction
- Inheritance
- Polymorphism
- Encapsulation
- Strategy Design Pattern
- Composite Pattern

---

# Future Improvements

- Add GUI
- Add database integration
- Add REST API
- Improve indexing performance
- Add AI-based ranking
- Add multilingual support

---

# Project Team

- Aboubaker Bouaine
- Montaha Jemai
- Mehdi Kaddech
- Mohamed Ridha Charrad


---

# Academic Context

This project was developed as a mini-project for studying advanced Java programming concepts, algorithmic comparison methods, and software architecture inspired by KYC systems.
