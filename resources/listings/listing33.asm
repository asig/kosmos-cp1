; Listing 33: Code-Knacker

		.ORG 1
		LDA 108		; lade die aktuelle Rundenzahl in den Akku...
		ADD 111		; und addiere den Zufallszahlenwert dazu
A1		SUB 110		; subtrahiere „10" davon
		VGR 110		; ist jetzt noch mehr als „10 " im Akku?
		SPB A1		; falls ja, weiterhin subtrahieren bei 003
		ABS 104		; sonst ist eine Ziffer zwischen 0 und 9 übrig; speichere sie als 1. Computerziffer
		AKO 014		; lade „14"
		ADD 104		; addiere die 1. Computerziffer dazu
		ADD 108		; addiere die Rundenzahl dazu
A2		SUB 110		; subtrahiere „10" vom Akku-Inhalt
A5		VGR 110		; ist der Wert jetzt noch größer als „10"?
		SPB A2		; falls ja, weiterhin subtrahieren bei 010
		VGL 104		; sonst vergleiche es mit der 1. Computerziffer
		SPB B2		; alls Gleichheit, springe zu 032 (Korrektur)
		ABS 105		; sonst speichere Akku als 2. Computerziffer
		AKO 017		; lade „17"
		ADD 104		; addiere die 1. Computerziffer...
		ADD 105		; und die 2. Computerziffer
A3		SUB 110		; subtrahiere „10" vom Akku-Inhalt
A6		VGR 110		; ist der Wert jetzt noch größer als „10"?
		SPB A3		; falls ja, subtrahiere weiter bei 019
		VGL 104		; sonst - ist der Wert gleich der 1. Computerziffer?
		SPB B3		; falls ja, springe zu 034 (Korrektur)
		VGL 105		; ist der Wert gleich der 2. Computerziffer?
		SPB B3		; falls ja, springe zu 034 (Korrektur)
		ABS 106		; sonst speichere als 3. Computerziffer
		AKO 000		; lade „0"
		ABS 108		; speichere als Rundenzahl fürs Raten
		AKO 111		; lade „111"
A4		ANZ			; zeige dies an als Ende der Zufallszahlenerzeugung
		SPU A4 		; springe zum Anzeigen zurück (Schleife)
B2		ADD 109		; Addiere „1" zum Akku-Inhalt (2. Computerziffer)...
		SPU A5 		; und springe zurück zum Test des Wertes
B3		ADD 109		; Addiere „1" zum Akku-Inhalt (3. Computerziffer)...
		SPU A6 		; und springe zurück zum Test des Wertes

		; Auswertung der Eingabe:
C1		AKO 000		; lade „0" und...
		ABS 107		; speichere als Ergebnis (Ausgangswert)
		AKO 001		; lade „1"...
		ADD 108		; und erhöhe so den Rundenzähler
		ABS 108		; speichere ihn wieder
		LDA 101		; lade die 1. Spielerziffer
		VGL 104		; ist sie gleich der 1. Computerziffer?
		SPB X1		; falls ja, springe zu 065 (addiere „10")
		VGL 105		; sonst: ist sie gleich der 2. Computerziffer?
		SPB Y1		; falls ja, springe zu 077 (addiere „1")
		VGL 106		; sonst: ist sie gleich der 3. Computerziffer?
		SPB Y1		; falls ja, springe zu 077
C2		LDA 102		; lade die 2. Spielerziffer
		VGL 105		; ist sie gleich der 2. Computerziffer?
		SPB X2		; falls ja, springe zu 069 (addiere „10")
		VGL 104		; sonst: ist sie gleich der 1. Computerziffer?
		SPB Y2		; falls ja, springe zu 081 (addiere „1")
		VGL 106		; sonst: ist die gleich der 3. Computerziffer?
		SPB Y2		; falls ja, springe zu 081
C3		LDA 103		; lade die 3. Spielerziffer
		VGL 106		; ist sie gleich der 3. Computerziffer?
		SPB X3		; falls ja, springe zu 073 (addiere „10")
		VGL 104		; sonst: ist sie gleich der 1. Computerziffer?
		SPB Y3		; falls ja, springe zu 085 (addiere „1")
		VGL 105		; sonst: ist sie gleich der 2. Computerziffer?
		SPB Y3		; falls ja, springe zu 085
		LDA 107		; lade das Ergebnis der Auswertung
E1		ANZ			; zeige es an
		SPU E1		; springe zurück zum Anzeigen (Schleife)
X1		LDA 107		; lade die Ergebniszelle...
		ADD 110		; und erhöhe sie um „10": Platz stimmt!
		ABS 107		; speichere das Ergebnis wieder.. .
		SPU C2		; und komme zum Test der 2. Ziffer
X2		LDA 107		; lade die Ergebniszelle...
		ADD 110		; und erhöhe sie um „10": Platz stimmt!
		ABS 107		; speichere das Ergebnis wieder.. .
		SPU C3		; und komme zum Test der 3. Ziffer
X3		LDA 107		; lade die Ergebniszelle...
		ADD 110		; und erhöhe sie um „10": Platz stimmt!
		ABS 107		; speichere das Ergebnis wieder...
		SPU E1		; und springe zum Anzeigen des Ergebnisses
Y1		LDA 107		; lade die Ergebniszelle...
		ADD 109		; und erhöhe sie um „1" : Ziffer stimmt!
		ABS 107		; speichere das Ergebnis wieder...
		SPU C2		; und komme zum Test der 2. Ziffer
Y2		LDA 107		; lade die Ergebniszelle...
		ADD 109		; und erhöhe sie um „1" : Ziffer stimmt!
		ABS 107		; speichere das Ergebnis wieder...
		SPU C3		; und komme zum Test der 3. Ziffer
Y3		LDA 107		; lade die Ergebniszelle...
		ADD 109		; und erhöhe sie um „1" : Ziffer stimmt!
		ABS 107		; speichere das Ergebnis wieder...
		SPU E1		; und springe zum Anzeigen des Ergebnisses

		.ORG 100
		.DB ?		; 1. Spielerziffer
		.DB ?		; 2. Spielerziffer
		.DB ?		; 3. Spielerziffer
		.DB ?		; 1. Computerziffer
		.DB ?		; 2. Computerziffer
		.DB ?		; 3. Computerziffer
		.DB ?		; Ergebnis der Auswertung
		.DB ?		; Rundenzähler
		.DB 1		; „1" zum Weiterzählen
		.DB 10 		; „10" zum Weiterzählen
		.DB 23 		; Zufallszahlenstartwert(min. „10", max. „220")
