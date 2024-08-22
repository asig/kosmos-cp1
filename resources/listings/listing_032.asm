; Listing 32: Nim-Spiel

		.ORG 1

		AKO 001		; \
		ABS 111		; |
		AKO 002		; |
		ABS 112		; |
		AKO 003		; |
		ABS 113		; |
		AKO 015		; |
		ABS 100		; |
		AKO 014		; |
		ABS 101		; |
		AKO 013		; |
		ABS 102		; |
		AKO 012		; |
		ABS 103		; |
		AKO 011		; |
		ABS 104		; |_ Vorbelegen des Datenbereichs
		AKO 010		; |
		ABS 105		; |
		AKO 009		; |
		ABS 106		; |
		AKO 008		; |
		ABS 107		; |
		AKO 007		; |
		ABS 108		; |
		AKO 006		; |
		ABS 109		; |
		AKO 005		; |
		ABS 110		; |
		AKO 000		; |
		ABS 117		; /
		LDA 100		; lade den Hölzchenrest („15")
A0		ANZ			; zeige ihn an
		VZG 255		; warte eine Viertelsekunde
A1		P1E 001		; lies Port 1 Klemme 1 in den Akku
		VGL 117		; ist sie „0", also betätigt?
		SPB A2		; falls ja, springe zu 044
		P1E 002		; sonst lies Port 1 Klemme 2
		VGL 117		; ist diese „0", also betätigt?
		SPB A3		; falls ja, springe zu 046
		P1E 003		; sonst lies Port 1 Klemme 3
		VGL 117		; ist diese „0", also betätigt?
		SPB A4		; falls ja, springe zu 048
		SPU A1		; sonst ist gar keine betätigt - springe zu 034
A2		AKO 001		; lade „1 " (ein Hölzchen genommen)
		SPU A5		; springe zu 049
A3		AKO 002		; lade „2 " (zwei Hölzchen genommen)
		SPU A5		; springe zu 049
A4		AKO 003		; lade „3 " (drei Hölzchen genommen)
A5		ABS 114		; speichere die Wegnahme des Spielers in 114
		VZG 255		; warte kurz
		LDA 100		; lade den alten Hölzchenrest aus Zelle 100 ...
		SUB 114		; und subtrahiere, was der Spieler wegnimmt
		ABS 100		; speichere den neuen Hölzchenrest wieder
		VGL 101		; ist der Hölzchenrest jetzt „14"?
		SPB B1		; falls ja, springe zu 076
		VGL 102		; ist der Hölzchenrest jetzt „13"?
		SPB B1		; falls ja, springe zu 076
		VGL 103		; ist der Hölzchenrest jetzt „12"?
		SPB B3		; falls ja, springe zu 080
		VGL 104		; ist der Hölzchenrest jetzt „11"?
		SPB B2		; falls ja, springe zu 078
		VGL 105		; ist der Hölzchenrest jetzt „10"?
		SPB B1		; falls ja, springe zu 076
		VGL 106		; ist der Hölzchenrest jetzt „9"?
		SPB B1		; falls ja, springe zu 076
		VGL 107		; ist der Hölzchenrest jetzt „8"?
		SPB B3		; falls ja, springe zu 080
		VGL 108		; ist der Hölzchenrest jetzt „7"?
		SPB B2		; falls ja, springe zu 078
		VGL 109		; ist der Hölzenchrest jetzt „6"?
		SPB B1		; falls ja, springe zu 076
		VGL 110		; ist der Hölzchenrest jetzt „5"?
		SPB E1		; falls ja, springe zu 090 (Spieler gewinnt)
		VKL 110		; ist der Hölzchenrest kleiner als „5"?
		SPB E3		; falls ja, springe zu 093 (Computer gewinnt)
B1		LDA 111		; lade „1"...
		SPU B4		; und springe zum Speichern
B2		LDA 112		; lade „2"...
		SPU B4		; und springe zum Speichern
B3		LDA 113		; lade „3 "
B4		ABS 115		; speichere als Computer-Wegnahme
		ANZ			; zeige sie an ...
		VZG 255		; \
		VZG 255		; |_für eine Dreiviertelsekunde
		VZG 255		; /
		LDA 100		; lade den alten Hölzchenrest
		SUB 115		; subtrahiere davon die Computer-Wegnahme
		ABS 100		; speichere als neuen Hölzchenrest wieder
		SPU A0 		; springe zurück zu 032 und warte dort auf die nächste Eingabe am Port 1
E1		AKO 000		; lade „0 " als Zeichen des Spieler-Sieges
E2		ANZ			; zeige den Akku-Inhalt an ...
		SPU E2 		; in einer Anzeigeschleife
E3		LDA 116		; lade „11.111" als Zeichen des Computer-Sieges...
		SPU E2 		; und springe zum Anzeigen

		.ORG 100
		.DB 15		; Start-Hölzchenzahl und Zwischenstand
		.DB 14		; \
		.DB 13		; |
		.DB 12		; |
		.DB 11		; |
		.DB 10		; |_ Vergleichszahlen
		.DB 9		; |
		.DB 8		; |
		.DB 7		; |
		.DB 6		; |
		.DB 5		; /
		.DB 1		; „1" Hölzchen zum Wegnehmen
		.DB 2		; „2" Hölzchen zum Wegnehmen
		.DB 3		; „3" Hölzchen zum Wegnehmen
		.DB ?		; Wegnahme des Spielers (über Port 1)
		.DB ?		; Wegnahme des Computers (berechnet)
		.RAW 11.111	; „11.111" als Zeichen des Computer-Sieges
		.RAW 00.000	; „0" als Zeichen des Spieler-Sieges
