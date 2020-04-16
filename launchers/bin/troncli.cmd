@echo off
SET jarpath=%~dp0
SET jarfile="%jarpath:~0,-1%\troncli.jar"
IF DEFINED JAVA_HOME (
    SET javabin="%JAVA_HOME%\bin\java.exe"
) ELSE (
    javabin=java
)

call %javabin% -jar %jarfile% --full grpc.trongrid.io:50051 %*
