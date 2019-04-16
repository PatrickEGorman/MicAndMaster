#include <jni.h>
#include <vector>
#include <oboe/Oboe.h>
#include "AudioProcessor.h"

extern "C"
JNIEXPORT jintArray
JNICALL
Java_com_example_micandmaster_audio_WaveForm_array(JNIEnv *env, jobject, jstring jstring1){

    std::string path = "test";
    WaveSampleData waveSampleData(path);
    std::vector<int> data = waveSampleData.GetSampleAmps();
    jintArray jintArray1 = env->NewIntArray(5);
    jsize len = env->GetArrayLength(jintArray1);
    for(int i=0; i<len; i++) {
        env->SetIntArrayRegion(jintArray1, i, 1, &data[i]);
    }
    return jintArray1;
}