Rapport de Projet : Système de Gestion d’Inventaire avec Intégration Blockchain

 # 1. Contexte du Projet
 cette projet est dans le context d'un gestion de stock dans un depo, et on a un stock manager qui supervise les stocks(les produits fournis par un fournisseur) entrer et sortie dans le depo.
## 1.1. Problème
Les entreprises souhaitant gérer efficacement leurs stocks se heurtent à plusieurs difficultés :

Manque de visibilité sur l’historique des entrées et sorties de produits. <br/>
Risques de pertes ou de fraudes liés à l’absence de traçabilité fiable.<br/>
Gestion incomplète des fournisseurs et catégories, rendant la chaîne logistique moins transparente.
## 1.2. Solution
Le projet propose un système de gestion d’inventaire complet, qui couple :

Une application JavaFX pour la gestion quotidienne (produits, fournisseurs, catégories, transactions).<br/>
Une base de données MySQL permettant la persistance rapide et la consultation en temps réel des informations.<br/>
Une intégration blockchain (via Truffle et Ganache) afin de garantir la traçabilité immuable des transactions critiques (entrées/sorties de stock).<br/>
L’objectif est de répondre aux exigences de performance, de fiabilité et d’auditabilité, tout en offrant une interface simple à utiliser.

# 2. Analyse des Besoins
Gestion des Produits

CRUD (Create, Read, Update, Delete) des informations de base : nom, description, prix, seuil de réapprovisionnement, stock courant.
Liaison avec une catégorie et un fournisseur (intégrité référentielle).
Gestion des Fournisseurs et Catégories

Possibilité de créer, consulter, modifier et supprimer les fournisseurs (nom, contact, etc.).
Classement des produits par catégories (nom, description).
Suivi des Transactions

Entrées et sorties de stock (IN/OUT).
Mise à jour automatique du stock grâce à un trigger en base de données.
Historique des transactions : produit concerné, quantité, date, fournisseur, type de mouvement.
Traçabilité et Sécurité

Enregistrement des transactions sur la blockchain via un contrat intelligent.
Vérification de l’immutabilité et de la cohérence des données entre MySQL et la blockchain.
Rapports et Exportation

Génération de rapports PDF pour documenter l’historique des transactions.
Mise en évidence des produits en situation de stock faible.
Facilité d’Utilisation

Interface intuitive, basée sur JavaFX, avec onglets pour chaque fonctionnalité (produits, fournisseurs, catégories, historique).
Alertes visuelles (texte en rouge) pour les stocks critiques.
# 3. Architecture du Projet
## 3.1. Architecture Logicielle
Couche Présentation (UI)

Développée en JavaFX, divisée en plusieurs onglets (produits, fournisseurs, catégories, transactions).
Composants graphiques (TableView, TextFields, Buttons) pour consulter et manipuler les données.
Couche Métier (DAO & Logique)

Data Access Objects (DAO) gérant la communication avec la base MySQL (ex. ProductDAO, SupplierDAO, TransactionDAO).
Business Logic : Validation, mise à jour du stock via triggers, gestion des règles (reorder level, etc.).
Couche Persistance (MySQL & Blockchain)

MySQL : Stockage principal des données (tables products, suppliers, categories, transactions).
Trigger : Met à jour automatiquement current_stock dès qu’une transaction est insérée.
Blockchain (Ganache) : Contrat intelligent (Inventory.sol) gérant la preuve immuable des transactions (fonction storeTransaction(...)).
Web3j (côté Java) : Envoie les données clés au contrat lors de chaque transaction pour permettre l’enregistrement “on-chain”.
## 3.2. Schéma Simplifié

![image](https://github.com/user-attachments/assets/61bfa3e0-4b99-4740-b327-e163b98acad6)


                                                 
# 4. Gestion du Projet
## 4.1. Planification et Méthodologie
Méthode Agile recommandée (Scrum ou Kanban) afin de livrer progressivement :
Sprint 1 : Mise en place du schéma MySQL et d’une première version CRUD basique (produits, fournisseurs).
Sprint 2 : Ajout de la gestion des catégories, du trigger, et de l’interface JavaFX.
Sprint 3 : Intégration blockchain (contrat intelligent, Ganache, Truffle), vérification de l’immutabilité.
Sprint 4 : Finalisation (export PDF, contrôles de saisie, test unitaire, documentation).
## 4.2. Suivi de l’Avancement
Outils de gestion :
Trello ou Jira pour suivre les tâches (backlog, to-do, in progress, done).
Git pour la gestion de versions du code (feature branches, merges, pull requests).
Revue et Tests :
Tests unitaires (JUnit) sur la couche DAO.
Tests spécifiques avec Truffle pour les fonctions du contrat intelligent (storeTransaction(...)).
Test de l’interface graphique pour vérifier la fluidité de l’expérience utilisateur.
## 4.3. Ressources et Équipe
Rôles clés :
Développeur Backend : Conception base de données, triggers, DAO.
Développeur Blockchain : Création et déploiement du contrat Solidity, intégration Web3j.
Développeur Frontend JavaFX : Interface utilisateur, gestion des tables, export PDF.
Risques :
Gestion des clés privées (sécurité) : ne pas stocker en clair dans le code en production.
Complexité de la blockchain : frais de gas, limites d’évolutivité si usage intensif.
# 5. Perspectives
Montée en Production

Migration vers un réseau test public (ex. Sepolia) ou un autre protocole plus adapté pour le stockage longue durée.
Hébergement de la base MySQL et de l’application JavaFX (ou distribution d’un exécutable Java).
Évolutions Fonctionnelles

Ajout d’une gestion des rôles et des permissions (ex. opérateur de stock, administrateur, etc.).
Automatisation de la génération et de l’envoi des rapports PDF (ex. e-mails quotidiens).
Optimisations

Implémenter un mécanisme de batch pour éviter de publier trop de transactions individuelles sur la blockchain (réduction des coûts de gas).
Intégration de microservices si la charge devient importante (API REST pour la gestion d’inventaire).
Sécurisation Avancée

Système d’authentification et de chiffrement renforcé.
Stockage sécurisé de la clé privée (Vault, HSM, ou service de gestion de clés).
Extension du Contrat Intelligent

Possibilité de certifier l’origine ou la provenance des produits, en ajoutant des modules pour la traçabilité approfondie (livraisons, retours, etc.).
Création de tokens ou intégration à un écosystème DeFi/ERP, selon la vision de l’entreprise.
# Conclusion
Ce projet répond à la problématique de la gestion d’inventaire de manière robuste, grâce à :

Une base de données relationnelle garantissant performance et simplicité d’exploitation.
Un contrat intelligent sur une blockchain permettant de sceller les transactions et de garantir la confiance et l’auditabilité.
Une interface JavaFX ergonomique offrant un cycle complet (CRUD, visualisation, export PDF).
À terme, cette approche hybride (base SQL + blockchain) renforce la fiabilité des processus et ouvre la porte à de nouvelles perspectives, que ce soit sur des réseaux publics ou d’autres blockchains adaptées à l’industrie.
