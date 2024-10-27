@rem Builds kosmos-cp1 for Windows and creates an Installer

@set QT_DIR=C:\Qt
@set QT_HOME=%QT_DIR%\6.7.3
@set QT_TOOLS=%QT_DIR%\Tools
@set NSIS_HOME=%ProgramFiles(x86)%\NSIS

@set PATH_SAVED=%PATH%

@rem Make sure we *prepend* to PATH just in case another compiler is installed. 
@set PATH=%QT_TOOLS%\ninja;%PATH%
@set PATH=%QT_TOOLS%\CMake_64\bin;%PATH%
@set PATH=%QT_HOME%\mingw_64\bin;%PATH%
@set PATH=%QT_TOOLS%\mingw1310_64\bin;%PATH%
@set PATH=%NSIS_HOME%\Bin;%PATH%

@set ARCH=x86_64
@set BUILD_DIR=build\win\%ARCH%\Release

cmake.exe ^
  -DQt6_DIR=%QT_HOME%\mingw_64\lib\cmake\Qt6 ^
  -DQT_DIR=%QT_HOME%\mingw_64\lib\cmake\Qt6 ^
  -DCMAKE_BUILD_TYPE=Release ^
  -G "Ninja" ^
  -B %BUILD_DIR% ^
  .

call %BUILD_DIR%\set_version.bat

rem cd build\win\%ARCH%\Release
ninja -C %BUILD_DIR%
rem cd ..\..\..\..

@rem windeployqt copies the mingw runtime dlls from %QT_HOME%\mingw_64 which
@rem happens to be the mingw1120... Apparently, there is no way to tell it
@rem to use a different source, so we just tell it to not copy the compiler 
@rem runtime and copy them over manually afterwards...
windeployqt.exe ^
  --release ^
  --no-compiler-runtime ^
  %BUILD_DIR%\kosmos-cp1.exe
copy %QT_TOOLS%\mingw1310_64\bin\*.dll %BUILD_DIR%\

mkdir build\installers
cd .
makensis /NOCD /INPUTCHARSET UTF8 /DVERSION=%VERSION% /DARCH=%ARCH% /DBUILD_DIR=%BUILD_DIR% installer\win\KosmosCP1.nsi

@set PATH=%PATH_SAVED%
