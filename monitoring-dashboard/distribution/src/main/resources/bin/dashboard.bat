@echo off

REM ---------------------------------------------------------------------------
REM   Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
REM
REM   Licensed under the Apache License, Version 2.0 (the "License");
REM   you may not use this file except in compliance with the License.
REM   You may obtain a copy of the License at
REM
REM   http://www.apache.org/licenses/LICENSE-2.0
REM
REM   Unless required by applicable law or agreed to in writing, software
REM   distributed under the License is distributed on an "AS IS" BASIS,
REM   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM   See the License for the specific language governing permissions and
REM   limitations under the License.

rem ---------------------------------------------------------------------------
rem Main Script for WSO2 Carbon
rem
rem Environment Variable Prerequisites
rem
rem   DASHBOARD_HOME   Home of CARBON installation. If not set I will  try
rem                   to figure it out.
rem
rem   JAVA_HOME       Must point at your Java Development Kit installation.
rem
rem   JAVA_OPTS       (Optional) Java runtime options used when the commands
rem                   is executed.
rem ---------------------------------------------------------------------------

rem ----- if JAVA_HOME is not set we're not happy ------------------------------
:checkJava
if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
goto checkServer

:noJavaHome
echo "You must set the JAVA_HOME variable before running CARBON."
goto end

rem ----- Only set DASHBOARD_HOME if not already set ----------------------------
:checkServer
rem %~sdp0 is expanded pathname of the current script under NT with spaces in the path removed
if "%DASHBOARD_HOME%"=="" set DASHBOARD_HOME=%~sdp0..
goto updateClasspath

:noServerHome
echo DASHBOARD_HOME is set incorrectly or DASHBOARD_HOME could not be located. Please set DASHBOARD_HOME.
goto end

rem ----- update classpath -----------------------------------------------------
:updateClasspath

setlocal EnableDelayedExpansion
set CARBON_CLASSPATH=
FOR %%C in ("%DASHBOARD_HOME%\lib\*.jar") DO set CARBON_CLASSPATH=!CARBON_CLASSPATH!;"%%C"

rem ----- Process the input command -------------------------------------------

rem Slurp the command line arguments. This loop allows for an unlimited number
rem of arguments (up to the command line limit, anyway).

:setupArgs
if ""%1""=="""" goto doneStart

if ""%1""==""-start""     goto commandLifecycle
if ""%1""==""--start""    goto commandLifecycle
if ""%1""==""start""      goto commandLifecycle

if ""%1""==""-restart""  goto commandLifecycle
if ""%1""==""--restart"" goto commandLifecycle
if ""%1""==""restart""   goto commandLifecycle

if ""%1""==""stop""   goto stopServer
if ""%1""==""-stop""  goto stopServer
if ""%1""==""--stop"" goto stopServer

if ""%1""==""debug""    goto commandDebug
if ""%1""==""-debug""   goto commandDebug
if ""%1""==""--debug""  goto commandDebug

if ""%1""==""version""   goto commandVersion
if ""%1""==""-version""  goto commandVersion
if ""%1""==""--version"" goto commandVersion

shift
goto setupArgs

rem ----- commandVersion -------------------------------------------------------
:commandVersion
shift
type "%DASHBOARD_HOME%\bin\version.txt"
goto end

rem ----- commandDebug ---------------------------------------------------------
:commandDebug
shift
set DEBUG_PORT=%1
if "%DEBUG_PORT%"=="" goto noDebugPort
if not "%JAVA_OPTS%"=="" echo Warning !!!. User specified JAVA_OPTS will be ignored, once you give the --debug option.
set JAVA_OPTS=-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=%DEBUG_PORT%
echo Please start the remote debugging client to continue...
goto findJdk

:noDebugPort
echo Please specify the debug port after the --debug option
goto end

:stopServer
set /p processId= < %DASHBOARD_HOME%\runtime.pid
echo Stopping the Micro Integrator Dashboard Server
taskkill /F /PID %processId%
goto end

rem ----- commandLifecycle -----------------------------------------------------
:commandLifecycle
goto findJdk

:doneStart
if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal

:findJdk
set CMD=RUN %*
goto runServer

rem ----------------- Execute The Requested Command ----------------------------

:runServer
PATH %PATH%;%JAVA_HOME%\bin\

:runJava
"%JAVA_HOME%\bin\java" %JAVA_OPTS% -Dlog4j.configurationFile="%DASHBOARD_HOME%/conf/log4j2.properties" -Dwso2.runtime.path="%DASHBOARD_HOME%" -Ddashboard.home="%DASHBOARD_HOME%" -classpath %CARBON_CLASSPATH% org.wso2.ei.dashboard.bootstrap.Bootstrap %CMD%

:end
goto endLocal

:endLocal

:END