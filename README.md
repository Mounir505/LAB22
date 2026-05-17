JNIDemo - Android JNI Laboratory
================================

Communication entre Java et C++ avec JNI, NDK et CMake

📌 Présentation du projet
-------------------------

Ce laboratoire a pour objectif principal de construire une application Android nommée **JNIDemo** capable de communiquer avec du code natif écrit en C++ grâce à la technologie JNI (Java Native Interface).

L’application Android exécutera plusieurs appels vers une bibliothèque native compilée sous forme de fichier partagé **.so**. Le projet permettra ainsi de comprendre comment Android fait communiquer l’univers Java/Kotlin avec l’univers natif C/C++.

Le laboratoire est volontairement progressif afin de comprendre :

*   la création d’un projet Android avec support natif ;
*   la compilation C++ avec le NDK ;
*   l’utilisation de CMake ;
*   le fonctionnement réel de JNI ;
*   la gestion des chaînes et tableaux entre Java et C++ ;
*   les erreurs fréquentes liées à JNI ;
*   les bonnes pratiques modernes recommandées par Android.

JNI reste très utilisé dans les applications Android modernes pour : les moteurs de jeu, la cybersécurité mobile, OpenCV, le chiffrement, les calculs intensifs, le traitement audio/vidéo, les IA embarquées et certaines protections anti reverse engineering.

🎯 Objectifs pédagogiques
-------------------------

À la fin de ce laboratoire, il sera possible de :

*   Créer un projet Android avec support C++ ;
*   Comprendre le rôle du NDK ;
*   Comprendre l’utilité de CMake ;
*   Déclarer des méthodes natives ;
*   Appeler du code C++ depuis Java ;
*   Manipuler des types JNI ;
*   Gérer correctement les Strings JNI ;
*   Traiter des tableaux Java dans du code natif ;
*   Lire les logs natifs via Logcat ;
*   Diagnostiquer les erreurs JNI classiques ;
*   Comprendre les limites et avantages de JNI.

🧱 Architecture générale du laboratoire
---------------------------------------

Le fonctionnement général de l’application suit le modèle suivant :

    
    Java / MainActivity
            ↓
    Appel méthode native
            ↓
    System.loadLibrary("native-lib")
            ↓
    Chargement de libnative-lib.so
            ↓
    JNI transmet l'appel au C++
            ↓
    Traitement côté natif
            ↓
    Conversion des résultats
            ↓
    Retour vers Java
            ↓
    Affichage dans Android
    

Ce mécanisme est au cœur du fonctionnement du NDK Android.

🛠️ Prérequis
-------------

Composant

Description

Android Studio

IDE principal pour Android

Android SDK

Plateforme de développement Android

NDK

Compilation du code natif C/C++

CMake

Système de build natif

LLDB

Débogueur natif Android

Vérifier dans : **Tools → SDK Manager → SDK Tools** que NDK, CMake et LLDB sont bien installés.

📁 Structure du projet
----------------------

    
    JNIDemo/
    │
    ├── app/
    │   ├── src/
    │   │   ├── main/
    │   │   │   ├── cpp/
    │   │   │   │   ├── native-lib.cpp
    │   │   │   │   └── CMakeLists.txt
    │   │   │   │
    │   │   │   ├── java/
    │   │   │   │   └── MainActivity.java
    │   │   │   │
    │   │   │   └── res/
    │   │   │       └── layout/
    │   │   │           └── activity_main.xml
    │   │
    │   └── build.gradle
    

🚀 Étape 1 — Création du projet Android
---------------------------------------

### Configuration recommandée

Option

Valeur

Name

JNIDemo

Language

Java

Minimum SDK

API 24

Build System

CMake

Include C++ Support

Activé

Android Studio génère automatiquement :

*   le dossier cpp/ ;
*   native-lib.cpp ;
*   CMakeLists.txt ;
*   la configuration Gradle JNI.

🧠 Étape 2 — Comprendre JNI
---------------------------

### Qu’est-ce que JNI ?

JNI signifie Java Native Interface.

Il s’agit d’une interface permettant à du code Java d’interagir avec du code natif C ou C++.

### Pourquoi JNI existe ?

*   réutilisation de bibliothèques C/C++ ;
*   calcul intensif ;
*   optimisation performances ;
*   traitement temps réel ;
*   protection partielle contre le reverse engineering ;
*   accès à certaines couches natives Android.

Android recommande de limiter les allers-retours Java ↔ natif, car JNI ajoute de la complexité et un coût supplémentaire.

⚙️ Étape 3 — Configuration Gradle
---------------------------------

    
    android {
        namespace "com.example.jnidemo"
        compileSdk 35
    
        defaultConfig {
            applicationId "com.example.jnidemo"
            minSdk 24
            targetSdk 35
            versionCode 1
            versionName "1.0"
        }
    
        externalNativeBuild {
            cmake {
                path file("src/main/cpp/CMakeLists.txt")
            }
        }
    }
    

Cette configuration indique à Gradle où se trouve le script CMake responsable de la compilation native.

🧩 Étape 4 — Configuration CMake
--------------------------------

    
    cmake_minimum_required(VERSION 3.22.1)
    
    project("jnidemo")
    
    add_library(
            native-lib
            SHARED
            native-lib.cpp)
    
    find_library(
            log-lib
            log)
    
    target_link_libraries(
            native-lib
            ${log-lib})
    

### Explications détaillées

*   **add\_library()** crée la bibliothèque .so ;
*   **SHARED** signifie bibliothèque partagée ;
*   **find\_library()** recherche la bibliothèque Android log ;
*   **target\_link\_libraries()** lie les dépendances natives.

💻 Étape 5 — Code natif C++
---------------------------

Le fichier principal du laboratoire est :

    
    app/src/main/cpp/native-lib.cpp
    

Ce fichier contient plusieurs démonstrations JNI :

*   Hello World JNI ;
*   Calcul de factoriel ;
*   Inversion de chaîne ;
*   Somme de tableau.

### Fonctions JNI importantes

Fonction

Rôle

NewStringUTF()

Créer une String Java

GetStringUTFChars()

Lire une String Java

ReleaseStringUTFChars()

Libérer la mémoire JNI

GetIntArrayElements()

Accéder aux tableaux Java

ReleaseIntArrayElements()

Libération des ressources JNI

☕ Étape 6 — Déclaration des méthodes natives Java
-------------------------------------------------

    
    public native String helloFromJNI();
    public native int factorial(int n);
    public native String reverseString(String s);
    public native int sumArray(int[] values);
    

### Chargement de la bibliothèque native

    
    static {
        System.loadLibrary("native-lib");
    }
    

Le nom doit être : **native-lib** et non : **libnative-lib.so**

🖼️ Étape 7 — Layout XML
------------------------

L’interface Android utilise un ScrollView afin de garantir une meilleure adaptabilité sur différents écrans.

Plusieurs TextView affichent les résultats des fonctions natives.

*   Hello JNI ;
*   Factoriel ;
*   Reverse String ;
*   Somme de tableau.

▶️ Étape 8 — Compilation et exécution
-------------------------------------

### Résultat attendu

    
    Hello from C++ via JNI !
    Factoriel de 10 = 3628800
    Texte inverse : !lufrewop si INJ
    Somme du tableau = 150
    

Si ces résultats apparaissent correctement, alors la communication Java ↔ JNI ↔ C++ fonctionne.

📜 Étape 9 — Vérification Logcat
--------------------------------

Ouvrir :

    
    View → Tool Windows → Logcat
    

Rechercher le tag :

    
    JNI_DEMO
    

Messages attendus :

    
    Appel de helloFromJNI depuis le natif
    Factoriel de 10 calcule en natif = 3628800
    String inversee = !lufrewop si INJ
    Somme du tableau = 150
    

🧪 Étape 10 — Tests guidés
--------------------------

Test

Entrée

Résultat attendu

Test normal

factorial(10)

3628800

Valeur négative

factorial(-5)

\-1

Overflow

factorial(20)

\-2

Chaîne vide

reverseString("")

""

Tableau vide

sumArray(new int\[\]{})

0

🐞 Étape 11 — Débogage des erreurs fréquentes
---------------------------------------------

### Erreur : UnsatisfiedLinkError

Causes possibles :

*   nom incorrect dans loadLibrary ;
*   signature JNI incorrecte ;
*   package Java modifié ;
*   bibliothèque .so absente ;
*   échec compilation C++.

### Erreur : crash sur String

Cause fréquente : oubli de ReleaseStringUTFChars().

### Erreur : compilation native

Vérifier :

*   #include <algorithm>
*   #include <climits>
*   syntaxe C++
*   configuration CMake

🔐 Pourquoi JNI est encore utilisé
----------------------------------

### 1\. Calcul intensif

*   Vision par ordinateur ;
*   Traitement image ;
*   IA embarquée ;
*   Chiffrement ;
*   Moteurs de jeu.

### 2\. Réutilisation de bibliothèques natives

*   OpenCV ;
*   FFmpeg ;
*   bibliothèques audio ;
*   moteurs graphiques ;
*   bibliothèques historiques C/C++.

### 3\. Protection partielle du code

Le code natif reste analysable, mais il est souvent plus difficile à reverse engineer qu’un simple bytecode Java.

🚀 Extensions avancées recommandées
-----------------------------------

Extension

Description

Multiplication matricielle

Calcul matriciel natif

Benchmark Java vs C++

Mesure des performances

Détection caractères interdits

Validation native sécurisée

RegisterNatives

Enregistrement dynamique JNI

Gestion d’exceptions Java

Lever des exceptions depuis C++

📚 Résumé pédagogique
---------------------

Ce laboratoire a permis de comprendre en profondeur :

*   la communication Java ↔ C++ ;
*   la compilation native Android ;
*   le fonctionnement du NDK ;
*   la structure d’une bibliothèque .so ;
*   la gestion mémoire JNI ;
*   les erreurs JNI fréquentes ;
*   les bonnes pratiques Android modernes.

Le projet JNIDemo constitue une excellente base pour : OpenCV, cybersécurité Android, chiffrement natif, anti-debugging, IA embarquée et reverse engineering.

🏁 Conclusion
-------------

JNI est une technologie puissante permettant à Android d’interagir directement avec du code natif C/C++.

Même si cette interface ajoute de la complexité, elle reste incontournable dans de nombreux domaines : performances, sécurité, multimédia, intelligence artificielle, moteurs de jeu et traitement temps réel.

Grâce à ce laboratoire, l’application JNIDemo devient une base solide pour explorer des projets Android avancés :

*   chiffrement natif ;
*   anti-debugging ;
*   détection root ;
*   OpenCV Android ;
*   moteurs IA embarqués ;
*   sécurité applicative mobile.

README — Projet Android JNI Demo | Android NDK • JNI • CMake • C++
