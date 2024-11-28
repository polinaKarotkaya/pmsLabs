#include <string>
#include <sstream>
#include <jni.h>

extern "C" {
JNIEXPORT jboolean JNICALL
Java_com_example_cars_MainActivity_validatePrice(JNIEnv *env, jobject obj, jstring price) {
    const char *priceStr = env->GetStringUTFChars(price, nullptr);

    std::istringstream iss(priceStr);
    double d;
    bool isValid = (iss >> d) && (iss.eof());

    env->ReleaseStringUTFChars(price, priceStr);
    return isValid ? JNI_TRUE : JNI_FALSE;
    }
}