JAVA_PATH = src/
CLASS_PATH = bin/
CPP_PATH = jni/
DLL_PATH = binaries/
INCLUDE_PATH = C:\MinGW\mingw64\mingw64\include
JNI_PATH = C:\Program Files\Java\jdk-12.0.2\include
GUI_PATH = maxit/gui/

vpath %.class $(CLASS_PATH)

all : $(DLL_PATH)maxit.dll

$(DLL_PATH)maxit.dll : $(CPP_PATH)$(GUI_PATH)GUI.o
	g++ -m64 -Wall -shared -o $(@) $(<) -std=c++11

$(CPP_PATH)$(GUI_PATH)GUI.o : $(CPP_PATH)$(GUI_PATH)GUI.cpp $(CPP_PATH)$(GUI_PATH)GUI.h
	g++ -m64 -Wall -I"$(INCLUDE_PATH)" -I"$(JNI_PATH)" -I"$(JNI_PATH)\win32" -I"$(JNI_PATH)\win32\bridge" -c $(<) -o $(@) -std=c++11

$(CPP_PATH)$(GUI_PATH)GUI.h : $(CLASS_PATH)$(GUI_PATH)GUI.class
	(cd $(JAVA_PATH) && javac -h ../$(CPP_PATH)$(GUI_PATH) $(GUI_PATH)GUI.java)

clean :
	rm $(CPP_PATH)$(GUI_PATH)GUI.h $(CPP_PATH)$(GUI_PATH)GUI.o $(CPP_PATH)$(GUI_PATH)GUI.dll