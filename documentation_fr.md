# Documentation de l'Application Test de Charge Serveur

## Vue d'ensemble

L'application Test de Charge Serveur est une application Android développée en Java utilisant l'architecture ViewModel et les design patterns. Elle fournit une solution complète pour tester la connectivité des serveurs via des requêtes HTTP ou des opérations Ping. La version 1.1 introduit des fonctionnalités d'arrière-plan améliorées avec des notifications persistantes, une gestion avancée des autorisations, et des contrôles de temporisation précis.

## Architecture

### Design Patterns Utilisés
- **MVVM (Model-View-ViewModel)** : Sépare la logique métier des composants d'interface utilisateur
- **Pattern Repository** : Gère les opérations de données et fournit une API propre pour l'accès aux données
- **Pattern Observer** : Utilise LiveData pour la programmation réactive
- **Pattern Singleton** : Utilisé dans les classes Database et Repository

### Composants Clés

#### Couche de Données
- **Entité Server** : Représente les informations du serveur avec des champs pour le nom, l'adresse, le port et le type de requête
- **ServerDao** : Objet d'accès aux données pour les opérations de base de données
- **AppDatabase** : Implémentation de base de données Room avec convertisseurs de types
- **Converters** : Convertisseurs de types pour la sérialisation des enums

#### Couche Repository
- **ServerRepository** : Gère les opérations de données et fournit une API propre pour les ViewModels

#### Couche ViewModel
- **ServerViewModel** : Gère la logique métier et fournit des données aux composants d'interface utilisateur

#### Couche UI
- **MainActivity** : Activité principale avec ViewPager2 et TabLayout
- **TestFragment** : Affiche les résultats des tests de serveur et contrôle l'exécution des tests
- **ServerListFragment** : Gère les opérations CRUD pour les serveurs
- **SettingsFragment** : Gère la configuration de l'application et l'import/export de données

#### Couche Service
- **ServerTestService** : Service de premier plan en arrière-plan pour les tests HTTP/Ping

### Fragments et Onglets

#### 1. Onglet Test (`TestFragment`)
- Affiche le nombre de serveurs configurés
- Bouton Lecture/Arrêt pour contrôler l'exécution des tests
- Affichage en temps réel des résultats des tests de serveur avec icônes de statut
- Montre les temps de réponse et les états d'erreur
- **NOUVEAU v1.1** : Compteur de requêtes restantes pour le mode test fini
- **NOUVEAU v1.1** : Mises à jour de progression en temps réel pendant les tests
- **NOUVEAU v1.1** : Vérification des autorisations de notification avant démarrage

**Fonctions Clés :**
- `startTest()` : Initie le test des serveurs avec vérification des autorisations
- `stopTest()` : Arrête les tests en cours
- `handleTestResult()` : Traite les résultats diffusés depuis le service
- `handleRequestProgress()` : **NOUVEAU** - Met à jour le compteur de requêtes en temps réel
- `updateRemainingRequestsDisplay()` : **NOUVEAU** - Affiche la progression pour le mode fini
- `updateUI()` : Met à jour les états du bouton lecture/arrêt

#### 2. Onglet Liste des Serveurs (`ServerListFragment`)
- Liste tous les serveurs configurés dans une RecyclerView
- Bouton d'ajout (+) pour créer de nouvelles entrées de serveur
- Appuyer pour modifier la configuration du serveur
- Glisser vers la gauche pour supprimer les serveurs avec boîte de dialogue de confirmation

**Fonctions Clés :**
- `showAddEditServerDialog()` : Affiche la boîte de dialogue de configuration du serveur
- `validateInput()` : Valide les données de configuration du serveur
- `saveServer()` : Persiste les données du serveur dans la base de données
- `showDeleteConfirmationDialog()` : Confirme la suppression du serveur

**Configuration du Serveur :**
- Nom du Serveur (obligatoire)
- Adresse du Serveur (URL HTTP ou adresse IP, obligatoire)
- Port (optionnel)
- Type de Requête (HTTP ou Ping)

#### 3. Onglet Paramètres (`SettingsFragment`)
- **MODIFIÉ v1.1** : Configuration du temps entre sessions (maintenant en millisecondes pour la précision)
- Bascule pour requêtes infinies
- Nombre de requêtes (quand pas infini)
- Fonctionnalité d'Export/Import/Partage pour les configurations de serveur
- **NOUVEAU v1.1** : Indicateur d'état des autorisations de notification avec correction en un clic
- **NOUVEAU v1.1** : Affichage visuel de l'état (vert/orange) pour l'état des notifications

**Fonctions Clés :**
- `saveSettings()` : Persiste les paramètres dans la base de données avec sauvegarde automatique
- `exportServers()` : Exporte la liste des serveurs et paramètres vers un fichier JSON
- `importServersFromUri()` : **AMÉLIORÉ** - Import amélioré avec synchronisation appropriée
- `shareServers()` : Crée un fichier JSON partageable
- `updateNotificationStatus()` : **NOUVEAU** - Met à jour l'affichage des autorisations de notification
- `handleNotificationStatusClick()` : **NOUVEAU** - Ouvre les paramètres d'autorisation si nécessaire

### Service en Arrière-plan

#### ServerTestService
**AMÉLIORÉ v1.1** : Service de premier plan avancé avec notifications riches et contrôles utilisateur.

**Caractéristiques Clés :**
- Test concurrent de plusieurs serveurs
- **MODIFIÉ** : Délais configurables en millisecondes pour la précision temporelle
- Support pour les cycles de requêtes infinis et limités
- Diffusion des résultats en temps réel vers l'interface utilisateur
- **NOUVEAU** : Notifications persistantes riches avec état actuel
- **NOUVEAU** : Boutons d'action de notification (Pause/Reprendre/Arrêter)
- **NOUVEAU** : Mises à jour de progression en temps réel dans la notification
- **NOUVEAU** : Fonctionnalité pause/reprise sans arrêter les tests
- **NOUVEAU** : Diffusion de progression des requêtes pour le mode fini

**Méthodes de Test :**
- `testHttpServer()` : Effectue des requêtes HTTP GET avec gestion des timeouts
- `testPingServer()` : Utilise InetAddress.isReachable() pour les tests de ping

**NOUVELLES Fonctionnalités de Notification :**
- `updateNotification()` : Met à jour la notification avec l'état actuel des tests
- `pauseTesting()` : Met en pause les tests sans arrêter le service
- `resumeTesting()` : Reprend les tests mis en pause
- `broadcastRequestProgress()` : Envoie les mises à jour de progression pour les requêtes restantes

### Schéma de Base de Données

#### Table Server
```sql
CREATE TABLE servers (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    address TEXT NOT NULL,
    port INTEGER,
    requestType TEXT NOT NULL
);
```

### Flux de Données

1. L'utilisateur configure les serveurs dans ServerListFragment
2. Les données sont persistées via ServerRepository vers la base de données Room
3. Les paramètres sont stockés dans SharedPreferences
4. L'exécution des tests est contrôlée via TestFragment
5. ServerTestService effectue les tests en arrière-plan
6. Les résultats sont diffusés de retour vers TestFragment pour affichage

### Material Design 3

L'application implémente les principes Material Design 3 avec :
- Thématique de couleurs dynamique (modes clair/sombre)
- Composants Material (Cartes, Boutons, TextInputLayouts)
- Élévation et ombres appropriées
- Espacement et typographie cohérents

### Localisation

L'application supporte :
- Anglais (par défaut)
- Traduction française (fr)

Toutes les chaînes visibles par l'utilisateur sont externalisées dans les fichiers strings.xml.

### Fonctionnalité d'Import/Export

L'application supporte l'échange de données basé sur JSON :
- Export des configurations de serveur vers le stockage externe
- Import des configurations depuis des fichiers JSON
- Partage des configurations via le système de partage Android
- Validation et gestion d'erreur pour les données corrompues

### Permissions

Permissions requises :
- `INTERNET` : Pour les requêtes HTTP
- `ACCESS_NETWORK_STATE` : Pour les vérifications de connectivité réseau
- `FOREGROUND_SERVICE` : Pour l'exécution des tests en arrière-plan
- `FOREGROUND_SERVICE_DATA_SYNC` : Type spécifique de service de premier plan

### Gestion d'Erreurs

L'application inclut une gestion d'erreurs complète :
- Validation des entrées avec messages d'erreur conviviaux
- Gestion des erreurs réseau avec configurations de timeout
- Gestion des erreurs d'opérations de base de données
- Gestion des erreurs d'E/S de fichier pour les opérations d'import/export

### Flux de Travail des Tests

1. L'utilisateur ajoute des serveurs dans l'onglet Liste des Serveurs
2. L'utilisateur configure les paramètres de test dans l'onglet Paramètres
3. L'utilisateur initie les tests dans l'onglet Test
4. Le service en arrière-plan effectue les tests selon la configuration
5. Les résultats sont affichés en temps réel avec des indicateurs visuels
6. L'utilisateur peut arrêter les tests à tout moment

### Considérations de Performance

- Le service en arrière-plan utilise un pool de threads pour les opérations concurrentes
- RecyclerView avec DiffUtil pour des mises à jour de liste efficaces
- Base de données Room avec opérations sur thread en arrière-plan
- Gestion appropriée du cycle de vie pour prévenir les fuites mémoire