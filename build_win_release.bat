set QT_DIR=C:\Qt
set QT_HOME=%QT_DIR%\6.7.3
set QT_TOOLS=%QT_DIR%\Tools
set NSIS_HOME=%ProgramFiles(x86)%\NSIS

set VERSION=0.1

set PATH_SAVED=%PATH%

set PATH=%QT_TOOLS%\ninja;%PATH%
set PATH=%QT_TOOLS%\CMake_64\bin;%PATH%
set PATH=%QT_TOOLS%\mingw1310_64\bin;%PATH%
set PATH=%QT_HOME%\mingw_64\bin;%PATH%
set PATH=%NSIS_HOME%\Bin;%PATH%

cmake.exe ^
  -DQt6_DIR=%QT_HOME%\mingw_64\lib\cmake\Qt6 ^
  -DQT_DIR=%QT_HOME%\mingw_64\lib\cmake\Qt6 ^
  -DCMAKE_BUILD_TYPE=Release ^
  -G "Ninja" ^
  -B build\win\Release\ ^
  .

cd build\win\Release
ninja
cd ..\..\..

windeployqt.exe ^
  --release ^
  build\win\Release\kosmos-cp1.exe

makensis -DVERSION=%VERSION% -DARCH=x86_64 installer\win\KosmosCP1.nsi

set PATH=%PATH_SAVED%
