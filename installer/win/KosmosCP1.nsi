; Copyright (c) 2024 Andreas Signer <asigner@gmail.com>
;
; This file is part of kosmos-cp1.
;
; kosmos-cp1 is free software: you can redistribute it and/or
; modify it under the terms of the GNU General Public License as
; published by the Free Software Foundation, either version 3 of the
; License, or (at your option) any later version.
;
; kosmos-cp1 is distributed in the hope that it will be useful,
; but WITHOUT ANY WARRANTY; without even the implied warranty of
; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
; GNU General Public License for more details.
;
; You should have received a copy of the GNU General Public License
; along with kosmos-cp1.  If not, see <http://www.gnu.org/licenses/>.

; The following values need to be set on the command line.
; - ARCH
; - VERSION
; - BUILD_DIR

Unicode True

!ifndef ARCH
  !error "ARCH must be defined!"
!endif

!ifndef VERSION
  !error "VERSION must be defined!"
!endif

!ifndef BUILD_DIR
  !error "BUILD_DIR must be defined!"
!endif

; HM NIS Edit Wizard helper defines
!define PRODUCT_EXE "kosmos-cp1.exe"
!define PRODUCT_DIR "KosmosCP1"
!define PRODUCT_NAME "Kosmos CP1"
!define PRODUCT_FILENAME "KosmosCP1"
!define PRODUCT_VERSION "${VERSION}"
!define PRODUCT_PUBLISHER "Andreas Signer"
!define PRODUCT_WEB_SITE "https://www.retrozone.ch/cp1"
!define PRODUCT_DIR_REGKEY "Software\Microsoft\Windows\CurrentVersion\App Paths\${PRODUCT_EXE}"
!define PRODUCT_UNINST_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"
!define PRODUCT_UNINST_ROOT_KEY "HKLM"


; MUI 1.67 compatible ------
!include "MUI2.nsh"

; MUI Settings
!define MUI_ABORTWARNING
!define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\modern-install-colorful.ico"
!define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\modern-uninstall-colorful.ico"

; Language Selection Dialog Settings
!define MUI_LANGDLL_REGISTRY_ROOT "${PRODUCT_UNINST_ROOT_KEY}"
!define MUI_LANGDLL_REGISTRY_KEY "${PRODUCT_UNINST_KEY}"
!define MUI_LANGDLL_REGISTRY_VALUENAME "NSIS:Language"

; Welcome page
!insertmacro MUI_PAGE_WELCOME
; License page
!define MUI_LICENSEPAGE_RADIOBUTTONS
!insertmacro MUI_PAGE_LICENSE "LICENSE"
; Directory page
!insertmacro MUI_PAGE_DIRECTORY
; Instfiles page
!insertmacro MUI_PAGE_INSTFILES
; Finish page
!define MUI_FINISHPAGE_RUN "$INSTDIR\${PRODUCT_EXE}"
!insertmacro MUI_PAGE_FINISH

; Uninstaller pages
!insertmacro MUI_UNPAGE_INSTFILES

; Language files
!insertmacro MUI_LANGUAGE "English"
!insertmacro MUI_LANGUAGE "German"

; MUI end ------

Name "${PRODUCT_NAME} ${PRODUCT_VERSION}"
OutFile "build/installers/${PRODUCT_FILENAME}-${PRODUCT_VERSION}-${ARCH}-Setup.exe"
!if ${ARCH} == "x86"
  InstallDir "$PROGRAMFILES32\${PRODUCT_DIR}"
!else if ${ARCH} == "x86_64"
  InstallDir "$PROGRAMFILES64\${PRODUCT_DIR}"
!else
  !error "Unsupported ARCH ${ARCH}. Use x86 or x86_64"
!endif
InstallDirRegKey HKLM "${PRODUCT_DIR_REGKEY}" ""
ShowInstDetails show
ShowUnInstDetails show

Function .onInit
  !insertmacro MUI_LANGDLL_DISPLAY
FunctionEnd

Section "Hauptgruppe" SEC01
  SetOutPath "$INSTDIR"
  SetOverwrite ifnewer
  File "${BUILD_DIR}\kosmos-cp1.exe"
  File "${BUILD_DIR}\libgcc_s_seh-1.dll"
  File "${BUILD_DIR}\libstdc++-6.dll"
  File "${BUILD_DIR}\libwinpthread-1.dll"
  File "${BUILD_DIR}\Qt6Core.dll"
  File "${BUILD_DIR}\Qt6Gui.dll"
  File "${BUILD_DIR}\Qt6Svg.dll"
  File "${BUILD_DIR}\Qt6SvgWidgets.dll"
  File "${BUILD_DIR}\Qt6Widgets.dll"

  SetOutPath "$INSTDIR\platforms"
  File "${BUILD_DIR}\platforms\qwindows.dll"

  # Set out path to $INSTDIR again so that we don't get the "Execute in" entry in the Desktop shortcut set to "platform".
  # No idea why this is necessary...
  SetOutPath "$INSTDIR"

  CreateDirectory "$SMPROGRAMS\${PRODUCT_DIR}"
  CreateShortCut "$SMPROGRAMS\${PRODUCT_DIR}\${PRODUCT_NAME}.lnk" "$INSTDIR\${PRODUCT_EXE}"
  CreateShortCut "$DESKTOP\${PRODUCT_NAME}.lnk" "$INSTDIR\${PRODUCT_EXE}"
#  File "..\..\path\to\file\Example.file"
SectionEnd

Section -AdditionalIcons
  WriteIniStr "$INSTDIR\${PRODUCT_NAME}.url" "InternetShortcut" "URL" "${PRODUCT_WEB_SITE}"
  CreateShortCut "$SMPROGRAMS\${PRODUCT_DIR}\Website.lnk" "$INSTDIR\${PRODUCT_NAME}.url"
  CreateShortCut "$SMPROGRAMS\${PRODUCT_DIR}\Uninstall.lnk" "$INSTDIR\uninst.exe"
SectionEnd

Section -Post
  WriteUninstaller "$INSTDIR\uninst.exe"
  WriteRegStr HKLM "${PRODUCT_DIR_REGKEY}" "" "$INSTDIR\${PRODUCT_EXE}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayName" "$(^Name)"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "UninstallString" "$INSTDIR\uninst.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayIcon" "$INSTDIR\${PRODUCT_EXE}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayVersion" "${PRODUCT_VERSION}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "URLInfoAbout" "${PRODUCT_WEB_SITE}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "Publisher" "${PRODUCT_PUBLISHER}"
SectionEnd


Function un.onUninstSuccess
  HideWindow
  MessageBox MB_ICONINFORMATION|MB_OK "$(^Name) wurde erfolgreich deinstalliert."
FunctionEnd

Function un.onInit
!insertmacro MUI_UNGETLANGUAGE
  MessageBox MB_ICONQUESTION|MB_YESNO|MB_DEFBUTTON2 "Möchten Sie $(^Name) und alle seinen Komponenten deinstallieren?" IDYES +2
  Abort
FunctionEnd

Section Uninstall
  Delete "$INSTDIR\${PRODUCT_NAME}.url"
  Delete "$INSTDIR\uninst.exe"
  Delete "$INSTDIR\${PRODUCT_EXE}"

  Delete "$INSTDIR\libgcc_s_seh-1.dll"
  Delete "$INSTDIR\libstdc++-6.dll"
  Delete "$INSTDIR\libwinpthread-1.dll"
  Delete "$INSTDIR\Qt6Core.dll"
  Delete "$INSTDIR\Qt6Gui.dll"
  Delete "$INSTDIR\Qt6Svg.dll"
  Delete "$INSTDIR\Qt6SvgWidgets.dll"
  Delete "$INSTDIR\Qt6Widgets.dll"
  Delete "$INSTDIR\platforms\qwindows.dll"

  Delete "$SMPROGRAMS\${PRODUCT_DIR}\Uninstall.lnk"
  Delete "$SMPROGRAMS\${PRODUCT_DIR}\Website.lnk"
  Delete "$DESKTOP\${PRODUCT_NAME}.lnk"
  Delete "$SMPROGRAMS\${PRODUCT_DIR}\${PRODUCT_NAME}.lnk"

  RMDir "$SMPROGRAMS\${PRODUCT_DIR}"
  RMDir "$INSTDIR\platforms"
  RMDir "$INSTDIR"

  DeleteRegKey ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}"
  DeleteRegKey HKLM "${PRODUCT_DIR_REGKEY}"
  SetAutoClose true
SectionEnd
