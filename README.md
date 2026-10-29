# TP3-DAR2026 : Serveur multi-threads, sockets en mode connecté TCP

Fattoum Mohamed Khalil, LSI3 1.2, Développement d'applications réparties

## Structure

- `activite3-1/` : serveur multi-thread, acceptation de plusieurs clients (échange de chaînes)
- `activite3-2/` : service de calculatrice multi-thread, échange d'objets et compteur global synchronisé
- `captures/` : imprimés écran

## Compilation et exécution

Dans chaque terminal : `chcp 65001` (accents), puis, depuis le dossier de l'activité :

```
javac -encoding UTF-8 -d out src/serverPackage/*.java src/clientPackage/*.java
java -cp out serverPackage.Server
java -cp out clientPackage.Client <adresse IP du serveur>
```

Pour l'activité 3-2, ajouter `src/commonPackage/*.java` à la commande `javac`. Le serveur se lance toujours avant les clients ; chaque client se lance dans son propre terminal.

## Activité 3-1 : acceptation de plusieurs clients

### Principe
- `Server` crée un `ServerSocket` sur le port 1234 (toutes les interfaces, donc `localhost`, Telnet et le réseau local fonctionnent). Son thread principal ne fait qu'une chose : boucler sur `accept()`.
- À chaque connexion, `Server` incrémente le numéro d'ordre, affiche l'adresse du client (`getRemoteSocketAddress()`) et son numéro (« Client n°1 connecté : /127.0.0.1:xxxxx »), puis confie la socket à un nouveau thread `ClientProcess`.
- `ClientProcess` (hérite de `Thread`) dialogue seul avec son client : il lui envoie son numéro d'ordre (« Bienvenue ! Vous êtes le client n°N »), lit les opérations, renvoie les résultats, et se termine sur `FIN`.
- Pour qu'un client Telnet puisse aussi utiliser le service, l'opération est envoyée sur **une seule ligne** (`55 * 25`), contrairement au TP2 où chaque élément occupait une ligne.

### Tests locaux
Serveur lancé : ![serveur](captures/a31_serveur.png)

Deux clients Telnet (`telnet localhost 1234`) : ![telnet](captures/a31_telnet.png)

Un client Java en plus, vue du serveur et des clients : ![clients java](captures/a31_clients.png)

### Tests en réseau local
Le client reçoit l'IP du serveur et se connecte avec `InetAddress` et `InetSocketAddress`. Le port 1234 doit être autorisé dans le pare-feu de la machine serveur.

Serveur (avec `ipconfig`) : ![serveur réseau](captures/a31_reseau_serveur.png)

Clients sur d'autres machines : ![clients réseau](captures/a31_reseau_clients.png)

### Synthèse
Le serveur du TP2 traitait un seul client puis s'arrêtait, car `accept()` et le dialogue se faisaient dans le même fil. En déplaçant le dialogue dans un thread par client, le thread principal redevient immédiatement disponible pour `accept()` : plusieurs clients (Telnet, Java, locaux ou distants) sont servis en même temps, chacun avec son numéro d'ordre et sa propre conversation indépendante. L'ordre des réponses entre clients n'est pas déterministe, puisque chaque thread avance à son rythme.

## Activité 3-2 : calculatrice multi-thread

### Principe
- Reprise de l'activité 2-2 : les classes `Operation` et `Resultat` (`Serializable`, package `commonPackage`) sont échangées avec `ObjectOutputStream` et `ObjectInputStream`. L'ordre de création des flux (sortie, `flush()`, puis entrée) est identique des deux côtés pour éviter un blocage.
- `Server` accepte les clients comme en 3-1 et lance un `ClientProcess` par client. Celui-ci envoie d'abord un `Integer` (numéro d'ordre), puis traite chaque `Operation` reçue et renvoie un `Resultat`. Un objet `null` (saisie de `FIN`) termine la connexion.
- **Accès concurrent** : la classe `Compteur` contient le total d'opérations traitées par le serveur, partagé par tous les threads. Sa méthode `incrementerEtAfficher` est `synchronized` : incrémentation et affichage forment une section critique. Sans cela, deux threads pourraient lire la même valeur (`total++` n'est pas atomique : lecture, addition, écriture), perdre une incrémentation, ou afficher les valeurs dans le désordre.

### Tests
Plusieurs clients en parallèle sur la machine locale : ![clients](captures/a32_clients.png)

Console du serveur avec le compteur global : ![serveur](captures/a32_serveur.png)

Test sur plusieurs machines du réseau : ![réseau](captures/a32_reseau.png)

### Synthèse
Chaque client est servi par son propre thread avec un flux d'objets indépendant, et reçoit son numéro d'ordre. Le compteur global croît d'une unité par opération traitée, tous clients confondus (par exemple 5 clients envoyant chacun 5 opérations donnent un compteur final de 25, affiché de 1 à 25 sans doublon ni valeur sautée). Le verrou garantit la cohérence de cette ressource critique ; son coût est négligeable ici, car la section protégée est très courte et le calcul lui-même se fait hors verrou.
