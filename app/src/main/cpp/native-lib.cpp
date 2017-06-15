#include <jni.h>
#include <string>

extern "C"
jstring
Java_willsong_cn_commpark_LoginActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
