/*
 * GUI.cpp
 *
 *  Created on: Mar 3, 2020
 *      Author: embai
 */
//#define _WIN32_WINNT _WIN32_WINNT_WIN7
#include <SDKDDKVer.h>
#define WIN32_LEAN_AND_MEAN             // Exclude rarely-used stuff from Windows headers
// Windows Header Files
#include <windows.h>
// C RunTime Header Files
#include <stdlib.h>
#include <malloc.h>
#include <memory.h>
#include <tchar.h>

#include <iostream>
#include <winuser.h>
#include <jni.h>
#include "maxit_gui_GUI.h"

#ifndef JNICALL
#define JNICALL
#endif

using namespace std;

JNIEXPORT jfloat JNICALL Java_maxit_gui_GUI_getOSScaling(JNIEnv *env,
		jclass gui) {
	cout << "hi" << endl;
	SetProcessDpiAwarenessContext(1);
	printf("Hello World!");
	return 0;
}
