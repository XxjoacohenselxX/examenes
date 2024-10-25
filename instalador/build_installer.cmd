ECHO CREACIÓN DE INSTALADOR PARA APLICACIÓN JAVA EN WINDOWS
ECHO Prerrequistos:
ECHO  - Descargar Apache Maven (binary, ejemplo: apache-maven-3.9.9-bin.zip)
ECHO  - Descargar Wix (ejemplo: wix314-binaries.zip)
ECHO  - Instalar DotNet runtime (ejemplo: dotnet-runtime-8.0.10-win-x86.exe)
ECHO  - Instalar DotNet SDK (ejemplo: dotnet-sdk-8.0.403-win-x64.exe)

SET PATH_INSTALADOR=%cd%
SET APP_NAME=examenes
SET JAVA_HOME=C:\Users\ET7\Desktop\profesorfuentes\jdk-21.0.3
SET PROJECT_FOLDER=C:\Users\ET7\git\exameness\examenes
SET MAVEN_HOME=%cd%\apache-maven-3.9.9
SET WIX_TOOLSET_HOME=%cd%\wix
SET PATH=%PATH%;C:\WINDOWS\system32;C:\WINDOWS;
SET PATH=%PATH%;%JAVA_HOME%\bin
SET PATH=%PATH%;%MAVEN_HOME%\bin
SET PATH=%PATH%;%WIX_TOOLSET_HOME%
SET APP_NAME=examenes

CD %PROJECT_FOLDER%
CALL mvn clean package

@echo on
ECHO volviendo a %PATH_INSTALADOR%
cd %PATH_INSTALADOR%

rmdir /s/q custom-jre
jlink --module-path %JAVA_HOME%/jmods --add-modules java.base,java.desktop --output custom-jre
jpackage --name %APP_NAME% --input target/ --main-jar examenes-0.0.1-SNAPSHOT.jar --main-class ar.edu.et7.Main --type exe --icon C:\Users\ET7\Desktop\INSTALADORES\favicon.ico --runtime-image custom-jre --win-shortcut

