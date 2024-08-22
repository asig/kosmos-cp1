; Listing 55: Ein „immerwährender" Kalender

        .ORG 1

        AKO 000 ; Vorbelegen des Datenbereichs: lade „0"
        ABS 106 ; speichere sie in Hilfszelle 2 (HZ2)...
        ABS 108 ; und der Hilfszelle 4 (HZ4)
        LDA 116 ; lade den Jahreskennziffernstart...
        ABS 118 ; und speichere ihn in der Adreßzelle
        LDA 102 ; lade das Jahrhundert
A1      VKL 111 ; ist der Akkuinhalt kleiner als „4"?
        SPB A2  ; falls ja, springe zum Speichern
        SUB 111 ; sonst subtrahiere „4"...
        SPU A1  ; und vergleiche wieder
A2      ABS 105 ; speichere den Jahrhundertrest in HZ1

        ; Berechnung des „Jahrhundertspeichers":
        AKO 000 ; lade „0"
A3      ABS 104 ; speichere den Akkuinhalt als Zähler
        LDA 118 ; lade die Adreßzelle
        ADD 105 ; addiere den Jahrhundertrest dazu...
        ABS 118 ; und speichere ihn wieder
        AKO 001 ; lade „1"
        ADD 104 ; addiere den Zähler dazu
        VKL 114 ; ist „29" noch nicht erreicht?
        SPB A3  ; falls ja, zurück zum Addieren

        ; 28er-Rest-Bildung beim Jahr:
        LDA 103 ; lade das Jahr
        VGL 108 ; ist es „0", also glattes Jahrhundert?
        SPB A5  ; falls ja, springe zum Flag-Setzen
A4      ABS 106 ; speichere den Rest in HZ2
        VKL 113 ; ist er kleiner als „28"?
        SPB A6  ; falls ja, Restbildung abgeschlossen
        SUB 113 ; sonst „28" subtrahieren...
        SPU A4  ; und zurückspringen
A5      AKO 099 ; lade „99" als „Jahrhundert-Flag"...
        ABS 106 ; und speichere es in HZ2
        LDA 118 ; dann lade die Adreßzelle...
        SUB 109 ; und verringere den Inhalt u m „1"
        SPU A7  ; springe zum Ende der Adreßberechnung
A6      ADD 118 ; addiere zum Akku die bisherige Adresse
A7      ABS 118 ; speichere dies als Endadresse
        LDA 106 ; lade HZ2 (28er-Rest des Jahres)
        VKL 115 ; ist er kleiner als „99"?
        SPB B1  ; falls ja, springe zur Schaltjahrprüfung
        LDA 105 ; sonst lade den Jahrhundert-Rest aus HZ1...
        SPU B2  ; und springe zur Schaltjahrkontrolle

        ; 4er-Rest-Bildung für Schaltjahre:
B1      VKL 111 ; ist der Akkuinhalt kleiner als „4"?
        SPB B2  ; falls ja, fertig: Sprung heraus
        SUB 111 ; sonst subtrahiere „4"...
        SPU B1  ; und vergleiche wieder
B2      VGR 108 ; ist der Rest größer als „0"?
        SPB B3  ; falls ja, ist es kein Schaltjahr
        LDA 101 ; sonst lade de n Monat
        VKL 110 ; ist er kleiner als „3", also Januar/Februar?
        SPB B3  ; falls ja, ist keine Korrektur nötig
        AKO 001 ; sonst lade „1"...
        ABS 108 ; und merke sie in HZ4

        ; Berechnung der Monatskennziffer:
B3      LIA 118 ; lade indirekt die Kennziffer für das Jahr
        SUB 108 ; subtrahiere HZ4 davon („0" oder„1")...
        ABS 107 ; und speichere den Wert in HZ3
        LDA 117 ; lade die Anfangsadresse der Monatskennziffern..
        ADD 101 ; und addiere den Monat dazu
        ABS 118 ; speichere es in der Adreßzelle...
        LIA 118 ; und lade so indirekt die Monatskennziffer
        VGR 107 ; ist diese größer als die Jahreskennziffer?
        SPB B4  ; falls ja, sofort subtrahieren
        ADD 112 ; sonst addiere noch eine „7" dazu
B4      SUB 107 ; subtrahiere von der Monatskennziffer die Jahreskennziffer

        ; Einbeziehung des Tages:
        ADD 100 ; addiere zur Jahr-/Monatskennziffer den Tag...
        SUB 109 ; und subtrahiere „1" davon
C1      VGR 112 ; ist der Wert größer als „7"?
        SPB C2  ; falls ja, springe zur Korrektur
        SPU C3  ; sonst springe zum Anzeigen
C2      SUB 112 ; subtrahiere „7" zur 7er-Rest-Bildung
        SPU C1  ; dan n kontrolliere diesen Wert
C3      ANZ     ; zeige den Akkuinhalt als Wochentag an...
        SPU C3  ; und zwar ständig!

        .ORG 100
        .DB 021 ; Tag (z. B. 00.021)         \
        .DB 007 ; Monat (z. B. 00.007)       |_ für die erste Mondlandung
        .DB 019 ; Jahrhundert (z. B. 00.019) |  am 21. 7.1969
        .DB 069 ; Jahr (z. B. 00.069)        /

        .ORG 109
        .DB 001 ; „1"
        .DB 003 ; „3"
        .DB 004 ; „4"
        .DB 007 ; „7"
        .DB 028 ; „28"
        .DB 029 ; „29"
        .DB 099 ; „99"
        .DB 140 ; Startadresse für Jahreskennziffern
        .DB 118 ; Startadresse für Monatskennziffern

        .ORG 119

        ; Monatskennziffern:
        .DB 001 ; Januar
        .DB 004 ; Februar
        .DB 004 ; März
        .DB 007 ; April
        .DB 002 ; Mai
        .DB 005 ; Juni
        .DB 007 ; Juli
        .DB 003 ; August
        .DB 006 ; September
        .DB 001 ; Oktober
        .DB 004 ; November
        .DB 006 ; Dezember

        .ORG 139

        ; Jahreskennziffern:
        .DB 002
        .DB 001
        .DB 007
        .DB 006
        .DB 005
        .DB 004
        .DB 002
        .DB 001
        .DB 007
        .DB 006
        .DB 004
        .DB 003
        .DB 002
        .DB 001
        .DB 006
        .DB 005
        .DB 004
        .DB 003
        .DB 001
        .DB 007
        .DB 006
        .DB 005
        .DB 003
        .DB 002
        .DB 001
        .DB 007
        .DB 005
        .DB 004
        .DB 003
        .DB 004
        .DB 004
        .DB 002
        .DB 001
        .DB 007
        .DB 006
        .DB 004
        .DB 003
        .DB 002
        .DB 001
        .DB 006
        .DB 005
        .DB 004
        .DB 003
        .DB 001
        .DB 007
        .DB 006
        .DB 005
        .DB 003
        .DB 002
        .DB 001
        .DB 007
        .DB 005
        .DB 004
        .DB 003
        .DB 002
        .DB 007
        .DB 006
        .DB 005
        .DB 006
        .DB 006
        .DB 004
        .DB 003
        .DB 002
        .DB 001
        .DB 006
        .DB 005
        .DB 004
        .DB 003
        .DB 001
        .DB 007
        .DB 006
        .DB 005
        .DB 003
        .DB 002
        .DB 001
        .DB 007
        .DB 005
        .DB 004
        .DB 003
        .DB 002
        .DB 007
        .DB 006
        .DB 005
        .DB 004
        .DB 002
        .DB 001
        .DB 007
        .DB 001
        .DB 001
        .DB 006
        .DB 005
        .DB 004
        .DB 003
        .DB 001
        .DB 007
        .DB 006
        .DB 005
        .DB 003
        .DB 002
        .DB 001
        .DB 007
        .DB 005
        .DB 004
        .DB 003
        .DB 002
        .DB 007
        .DB 006
        .DB 005
        .DB 004
        .DB 002
        .DB 001
        .DB 007
        .DB 006
        .DB 004
        .DB 003
        .DB 002
