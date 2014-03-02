#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/ptrace.h>
#include <sys/types.h>

#include "edu_drexel_StatCollector_sensors_SystemCalls.h"

JNIEXPORT jstring JNICALL Java_edu_drexel_StatCollector_sensors_SystemCalls_hello (JNIEnv * env, jobject obj) {
    return (*env)->NewStringUTF(env, "Hello World!");
}

JNIEXPORT jint JNICALL Java_edu_drexel_StatCollector_sensors_SystemCalls_add (JNIEnv * env, jobject obj, jint value1, jint value2) {
	return (value1 + value2);
}

JNIEXPORT jstring JNICALL Java_edu_drexel_StatCollector_sensors_SystemCalls_getSystemCalls (JNIEnv * env, jobject obj, jint pid) {
    int trace = ptrace(PTRACE_ATTACH, pid, NULL, NULL);
    char traceerr[40];
    char tracesuccess[40];

    if (trace < 0) {
        sprintf(traceerr, "PID: %d Trace Result: %d", pid, trace);
        return (*env)->NewStringUTF(env, traceerr);
    }
    else {
        sprintf(tracesuccess, "Success! PID: %d", pid);
        return (*env)->NewStringUTF(env, tracesuccess);
    }

    return (*env)->NewStringUTF(env, "Bad Result");
}
