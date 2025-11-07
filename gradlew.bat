@ECHO OFF
SETLOCAL
SET DIR=%~dp0
IF NOT "%JAVA_HOME%" == "" (
  set JAVACMD=%JAVA_HOME%\bin\java.exe
) ELSE (
  set JAVACMD=java.exe
)
"%JAVACMD%" org.gradle.wrapper.GradleWrapperMain %*
