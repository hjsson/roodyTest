/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class android_serialport_api_SerialPort */

#ifndef _Included_android_serialport_api_SerialPort
#define _Included_android_serialport_api_SerialPort
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     android_serialport_api_SerialPort
 * Method:    open
 * Signature: (Ljava/lang/String;II)Ljava/io/FileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_android_1serialport_1api_SerialPort_open
        (JNIEnv *, jclass, jstring, jint, jint);

JNIEXPORT jobject JNICALL Java_co_kr_fvn_roodytest_RdSerialPt_open
        (JNIEnv *, jclass, jstring, jint, jint);

/*
 * Class:     android_serialport_api_SerialPort  Java_co_kr_fvn_roodytest_RdSerialPt
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_android_1serialport_1api_SerialPort_close
  (JNIEnv *, jobject);
JNIEXPORT void JNICALL Java_co_kr_fvn_roodytest_RdSerialPt_close
        (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
