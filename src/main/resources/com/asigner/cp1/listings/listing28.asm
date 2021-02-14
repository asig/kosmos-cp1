; Listing 28: Uhr mit wechselnder Stunden- und Minutenanzeige

        ; Vor Assemblierung hier die aktuelle Uhrzeit einstellen:
        .EQU XXX, 11    ; Minute
        .EQU YYY, 09    ; Stunde

        .ORG 0

		AKO 000		; lade „0"
		ABS 118		; speichere als Viertelsekundenzähler...
		ABS 121		; und als Sekundentakt
		AKO XXX		; lade die aktuelle Minute (Uhr!)
		ABS 119		; speichere in 119
		AKO YYY		; lade die aktuelle Stunde (Uhr!)
		ABS 120		; speichere in 120
		SPU B1 		; das Programm fängt so richtig erst bei 044 an
A1		AKO 001		; lade „1"
		ADD 121		; addiere den Sekundentakt zu dieser „1"...
		ABS 121		; und speichere den Sekundentakt wieder
		VGR 092		; ist er größer als „4", also eine Sekunde vorbei?
		SPB A2 		; falls ja, springe zu 015 (Stunde laden)
		LDA 119		; sonst lade die Minute in den Akku ...
		SPU A4 		; und springe zum Anzeigen
A2		VKL 096		; ist der Takt noch kleiner als „8"?
		SPB A3 		; falls ja, springe direkt zum Stunde-Laden
		AKO 000		; sonst lade „0"...
		ABS 121		; und speichere als Sekundentakt: Neubeginn
A3		LDA 117		; lade den Inhalt der Anzeigezelle in den Akku ...
A4		ANZ 		; zeige den Wert an (Minute und Stunde im Sekundentakt)
A5		VZG 217		; warte kurz
		AKO 001		; lade „1"
		ADD 118		; addiere den Viertelsekundenzähler dazu ...
		ABS 118		; und speichere den erhöhten Wert
		VKL 116		; ist er kleiner als „240"?
		SPB A1 		; dann ist die Minute noch nicht rum - weiterzählen
		AKO 000		; sonst lade „0"
		ABS 118		; speichere als Viertelsekundenzähler: Neubeginn
		AKO 001		; lade „1"...
		ADD 119		; und addiere dazu den bisherigen Minutenwert
		ABS 119		; speichere die neue Minute wieder
		VZG 000		; warte - (Zeitkorrektur möglich)
		VKL 115		; ist die Minute kleiner als „60"?
		SPB A5 		; falls ja, weiter bei 021 (neuer Start der Viertelsekunden)
		AKO 000		; sonst lade „0"
		ABS 119		; speichere als Minutenwert: eine Stunde ist rum
		AKO 001		; deshalb lade „1"...
		ADD 120		; und addiere dazu den bisherigen Stundenwert
		ABS 120		; speichere die Stunde wieder
		VKL 114		; ist die Stunde „13" noch nicht erreicht?
		SPB B1 		; falls ja, springe zum Anzeigevorbereiten
		AKO 001		; sonst lade „1"
		ABS 120		; speichere als Stundenwert
B1		VKL 113		; Ist die Stunde (immer noch im Akku!) noch kleiner als „2"?
		SPB C1 		; falls ja, springe mal zu 069: Stundenaufbereitung
		VKL 091		; ist sie kleiner als „3"?
		SPB C2 		; falls ja, zu 071 springen
		VKL 092		; kleiner als „4"?
		SPB C3 		; dann weiter bei 073
		VKL 093		; kleiner als „5"?
		SPB C4 		; dann zu 075
		VKL 094		; kleiner als „6"?
		SPB C5 		; dann zu 077
		VKL 095		; kleiner als „7"?
		SPB C6 		; dann zu 079
		VKL 096		; kleiner als „8"?
		SPB C7 		; dann zu 081
		VKL 097		; kleiner als „9"?
		SPB C8 		; dann zu 083
		VKL 098		; kleiner als „10"?
		SPB C9 		; dann zu 085
		VKL 099		; kleiner als „11"?
		SPB D1 		; dann zu 087
		VKL 100		; kleiner als „12"?
		SPB D2 		; dann geht es bei 089 weiter
		LDA 112		; sonst ist „12" drin - lade deshalb Zelle 112 (12.000)...
B2		ABS 117		; und speichere in 117 (Anzeigezelle)
		SPU A1 		; Dann fange wieder bei 008 an
C1		LDA 101		; lade Zelle 101 (1. Stunde, also 01.000) in den Akku
		SPU B2 		; springe zum Umspeichern in die Anzeigezelle bei 067
C2		LDA 102		; lade Zelle 102 (2. Stunde, also 02.000) in den Akku
		SPU B2 		; springe zum Umspeichern in die Anzeigezelle bei 067
C3		LDA 103		; lade Zelle 103 (3. Stunde, also 03.000) in den Akku
		SPU B2 		; springe zum Umspeichern in die Anzeigezelle bei 067
C4		LDA 104		; lade Zelle 104 (4. Stunde, also 04.000) in den Akku
		SPU B2 		; springe zum Umspeichern in die Anzeigezelle bei 067
C5		LDA 105		; lade Zelle 105 (5. Stunde, also 05.000) in den Akku
		SPU B2 		; springe zum Umspeichern in die Anzeigezelle bei 067
C6		LDA 106		; lade Zelle 106 (6. Stunde, also 06.000) in den Akku
		SPU B2 		; springe zum Umspeichern in die Anzeigezelle bei 067
C7		LDA 107		; lade Zelle 107 (7. Stunde, also 07.000) in den Akku
		SPU B2 		; springe zum Umspeichern in die Anzeigezelle bei 067
C8		LDA 108		; lade Zelle 108 (8. Stunde, also 08.000) in den Akku
		SPU B2 		; springe zum Umspeichern in die Anzeigezelle bei 067
C9		LDA 109		; lade Zelle 109 (9. Stunde, also 09.000) in den Akku
		SPU B2 		; springe zum Umspeichern in die Anzeigezelle bei 067
D1		LDA 110		; lade Zelle 110 (10. Stunde, also 10.000) in den Akku
		SPU B2 		; springe zum Umspeichern in die Anzeigezelle bei 067
D2		LDA 111		; lade Zelle 111 (11. Stunde, also 11.000) in den Akku
		SPU B2 		; springe zum Umspeichern in die Anzeigezeile bei 067

        .ORG 91
		.DB 3		; Vergleichszahlen für den Stundenwert: Stunde 3
		.DB 4		; Stunde 4
		.DB 5		; Stunde 5
		.DB 6		; Stunde 6
		.DB 7		; Stunde 7
		.DB 8		; Stunde 8
		.DB 9		; Stunde 9
		.DB 10		; Stunde 10
		.DB 11		; Stunde 11
		.DB 12		; Stunde 12
		.RAW 01.000	; Anzeigezahlen für den Stundenwert: Stunde 1
		.RAW 02.000	; Stunde 2
		.RAW 03.000	; Stunde 3
		.RAW 04.000	; Stunde 4
		.RAW 05.000	; Stunde 5
		.RAW 06.000	; Stunde 6
		.RAW 07.000	; Stunde 7
		.RAW 08.000	; Stunde 8
		.RAW 09.000	; Stunde 9
		.RAW 10.000	; Stunde 10
		.RAW 11.000	; Stunde n
		.RAW 12.000 ; Stunde 12
		.DB 2		; noch ein Stundenvergleich: „2"
		.DB 13		; „13 " als Obergrenze der Stundenanzeige
		.DB 60		; „60 " als Obergrenze für Minutenzähler
		.DB 240		; „240" als Grenze für Viertelsekundenzähler
		.DB ?		; Anzeigezelle (Stundenwert als „Befehl")
		.DB ?		; Viertelsekundenzähler
		.DB ?		; momentane Minute
		.DB ?		; momentane Stunde
		.DB ?		; Sekundentakt zum Anzeigewechsel
