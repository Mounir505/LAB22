#include <jni.h>
#include <string>
#include <algorithm>
#include <climits>
#include <android/log.h>

#define LOG_TAG "JNI_PREMIUM_DEMO"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" {

// 1) Implémentation : Hello World Natif
jstring helloFromJNI(JNIEnv* env, jobject thiz) {
    LOGI("-> [C++] helloFromJNI exécuté.");
    return env->NewStringUTF("Hello from C++ via JNI (Signature Dynamique) !");
}

// 2) Implémentation : Factoriel Sécurisé (Retourne un jlong pour éviter l'overflow immédiat)
jlong factorial(JNIEnv* env, jobject thiz, jint n) {
    if (n < 0) {
        LOGE(" [Erreur] Valeur négative interdite (%d)", n);
        return -1; // Code d'erreur : Valeur négative
    }
    if (n > 20) {
        LOGE(" [Erreur] Risque d'overflow détecté pour n = %d", n);
        return -2; // Code d'erreur : Overflow int32/int64
    }

    jlong res = 1;
    for (int i = 1; i <= n; ++i) {
        res *= i;
    }
    LOGI(" [Succès] Factoriel calculé : %lld", (long long)res);
    return res;
}

// 3) Implémentation : Inversion de Chaîne de Caractères
jstring reverseString(JNIEnv* env, jobject thiz, jstring javaString) {
    if (javaString == nullptr) {
        LOGE(" [Erreur] Chaîne Java nulle reçue");
        return env->NewStringUTF("");
    }

    const char* chars = env->GetStringUTFChars(javaString, nullptr);
    if (chars == nullptr) {
        LOGE(" [Erreur] Échec d'allocation GetStringUTFChars");
        return nullptr;
    }

    std::string s(chars);
    // Libération immédiate pour éviter les fuites de mémoire (Memory Leaks)
    env->ReleaseStringUTFChars(javaString, chars);

    std::reverse(s.begin(), s.end());
    return env->NewStringUTF(s.c_str());
}

// 4) Implémentation : Somme d'un Tableau d'Entiers (Optimisé en lecture seule)
jint sumArray(JNIEnv* env, jobject thiz, jintArray array) {
    if (array == nullptr) {
        LOGE(" [Erreur] Tableau nul");
        return -1;
    }

    jsize len = env->GetArrayLength(array);
    jint* elements = env->GetIntArrayElements(array, nullptr);
    if (elements == nullptr) {
        LOGE(" [Erreur] Accès aux éléments impossible");
        return -2;
    }

    long long sum = 0;
    for (jsize i = 0; i < len; ++i) {
        sum += elements[i];
    }

    // Utilisation de JNI_ABORT : pas de copie de retour vers Java car le tableau est en lecture seule
    env->ReleaseIntArrayElements(array, elements, JNI_ABORT);

    if (sum > INT_MAX || sum < INT_MIN) {
        LOGE(" [Erreur] Overflow sur la somme du tableau");
        return -3;
    }

    return static_cast<jint>(sum);
}

// 5) Extension C : Algorithme lourd dédié au Benchmark de performance
jlong intenseCalculationNative(JNIEnv* env, jobject thiz, jint iterations) {
    jlong count = 0;
    for (int i = 0; i < iterations; ++i) {
        count += (i % 3 == 0) ? (jlong)i * 2 : (jlong)i / 2;
    }
    return count;
}

// =========================================================================
// EXTENSION D : SYSTEME D'ENREGISTREMENT DYNAMIQUE (RegisterNatives)
// =========================================================================

static JNINativeMethod gMethods[] = {
        {"helloFromJNI", "()Ljava/lang/String;", (void*)helloFromJNI},
        {"factorial", "(I)J", (void*)factorial},
        {"reverseString", "(Ljava/lang/String;)Ljava/lang/String;", (void*)reverseString},
        {"sumArray", "([I)I", (void*)sumArray},
        {"intenseCalculationNative", "(I)J", (void*)intenseCalculationNative}
};

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env;
    if (vm->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    // Recherche de la classe Java cible par son chemin de package exact
    jclass clazz = env->FindClass("com/example/jnidemo/MainActivity");
    if (clazz == nullptr) {
        return JNI_ERR;
    }

    // Enregistrement explicite du tableau de méthodes auprès de la JVM
    if (env->RegisterNatives(clazz, gMethods, sizeof(gMethods) / sizeof(gMethods[0])) < 0) {
        return JNI_ERR;
    }

    LOGI("[Succès] RegisterNatives : Liaison dynamique effectuée avec succès !");
    return JNI_VERSION_1_6;
}

} // extern "C"