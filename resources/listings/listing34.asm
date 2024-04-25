; Listing 34: Computer-Schaltuhr

		.ORG 1
		AKO 024		; lade „24"
		ABS 120		; speichere als Stundenvergleichszahl
		AKO 060		; lade „60"
		ABS 119		; speichere als Minuten-/Sekundenvergleich
		AKO 000		; lade „0"
		ABS 118		; speichere als Vergleichszahl...
		ABS 117		; und als AUS-Zustand des Gerätes
		P1A 000		; gib die „0" aus an Port 1 ...
		P2A 000		; und an Port 2: alles ausschalten
		LDA 120		; lade „24"
		VKL 127		; ist dies kleiner als die aktuelle Stunde?
		SPB E0		; falls ja, springe zu 027 (Fehleingabe-Halt)
		VKL 124		; ist dies kleiner als die Einschalt-Stunde?
		SPB E0		; falls ja, springe zu 027 (Fehleingabe-Halt)
		VKL 122		; ist dies kleiner als die Ausschalt-Stunde?
		SPB E0		; falls ja, springe zu 027 (Fehleingabe-Halt)
		LDA 119		; lade „60"
		VKL 126		; ist dies kleiner als die aktuelle Minute?
		SPB E0		; falls ja, springe zu 027 (Fehleingabe-Halt)
		VKL 125		; ist dies kleiner als die aktuelle Sekunde?
		SPB E0		; falls ja, springe zu 027 (Fehleingabe-Halt)
		VKL 123		; ist dies kleiner als die Einschalt-Minute?
		SPB E0		; falls ja, springe zu 027 (Fehleingabe-Halt)
		VKL 121		; ist dies kleiner als die Ausschalt-Minute?
		SPB E0		; falls ja, springe zu 027 (Fehleingabe-Halt)
		SPU C1		; sonst springe zu 028 (Zeit läuft)
E0		HLT			; anhalten - fehlerhafte Eingabe einer Zeit
C1		VZG 200		; \
C2		VZG 187		; | warte 1 Sekunde
		VZG 250		; |
		VZG 250		; /
		P1E 001		; Lies Port 1 Klemme 1: Tasteninformation
		VGL 118		; ist sie „0", also Taste gedrückt?
		SPB Z1		; falls ja, springe zum Anzeigen bei 082
		AKO 001		; sonst lade „1"
		ADD 125		; und erhöhe damit die Sekunden
		ANZ			; und zeige sie an ...
		ABS 125		; und speichere sie wieder
		VKL 119		; sind sie noch kleiner als „60"?
		SPB C1		; falls ja, Rücksprung zum Weiterzählen
C3		AKO 000		; sonst lade „0"
		ABS 125		; als Sekundenstand speichern
		AKO 001		; lade „1"
		ADD 126		; addiere dazu den Minutenstand
		ABS 126		; und speichere die neue Minute wieder
		VKL 119		; ist die Minute noch kleiner als „60"?
		SPB S1		; falls ja, zur nächsten Sekunde springen
		VZG 180		; Zeitkorrektur - siehe Text
		AKO 000		; eine neue Stunde bricht an: lade „0"
		ABS 126		; speichere sie als Minutenstand
		AKO 001		; lade „1"
		P1A 002		; Ausgabe an Port 1 Klemme 2: Tongenerator an
		VZG 200		; für 200 ms
		ADD 127		; addiere zur „1" im Akku die bisherige Stunde ...
		ABS 127		; und speichere sie wieder
		VKL 120		; ist die Stunde noch kleiner als „24"?
		AKO 000		; lade mal schon „0"
		P1A 002		; Ausgabe an Port 1 Klemme 2: Tongenerator aus
		SPB S2		; falls Vergleich in 056 richtig, springe zu 063
		ABS 127		; sonst speichere die „0" als neue Stunde
		SPU S2		; fertig mit dem Ton: weiter in der Zeitschleife
S1		VZG 160		; Zeitkorrektur - siehe Text
S2		LDA 124		; lade die Schaltstunde
		VGL 127		; ist sie gleich der aktuellen Stunde?
		SPB S3		; falls ja, springe zur Minutenkontrolle
		SPU C2		; sonst Rücksprung in die Sekundenroutine, da Schaltzeit noch nicht erreicht
S3		LDA 123		; lade die Schaltminute
		VGR 126		; ist sie noch größer als die aktuelle Minute?
		SPB 029		; falls ja, wird noch nicht geschaltet: weiter in der Schleife
		LDA 117		; sonst lade den Gerätezustand („1" oder „0")
		NEG			; invertiere ihn: AUS wird EIN und umgekehrt
		P2A 001		; Ausgabe an Port 2 Klemme 1: Relaissteuerung
		ABS 117		; dann speichere den Zustand wieder
		VGL 118		; ist er „0", das Gerät also aus?
		SPB E0		; falls ja, springe zum HALT
		LDA 122		; sonst lade die Ausschaltstunde ...
		ABS 124		; und speichere sie in der Einschalt-Zelle!
		LDA 121		; lade auch die Ausschaltminute ...
		ABS 123		; und speichere sie: Einschaltzeit ist jetzt weg!
		AKO 001		; lade „1" zur Zeitkorrektur
		SPU K0		; dann zur Korrekturroutine springen
Z1		LDA 127		; Unterprogramm zur Zeitanzeige: Stunde laden
		ANZ			; anzeigen
		VZG 250		; \
		VZG 250		; | 750 ms warten
		VZG 250		; /
		LDA 126		; Minute laden .. .
		ANZ			; und anzeigen
		VZG 250		; \
		VZG 250		; | 750 ms warten
		VZG 250		; /
		AKO 002		; Zeitkorrektur: 2 Sekunden mehr sind vergangen
K0		ADD 125		; diese zur aktuellen Sekunde addieren .. .
		ABS 125		; und wieder speichern
		VKL 119		; ist sie noch kleiner als „60"?
		SPB C2		; falls ja, weiterzählen in der normalen Schleife
		SUB 119		; sonst um „60" verringern
		ABS 125		; speichern der Sekunde .. .
		SPU C3		; und zur Minutenkontrolle springen

		.ORG 117
		.DB 0		; Vergleichszahl
		.DB 0		; Vergleichszahl
		.DB 60		; Vergleichszahl (Minuten u. Sekunden)
		.DB 24		; Vergleichszahl (Stunden)
		.DB ?		; Minuten \ Ausschaltzeit
		.DB ?		; Stunden /
		.DB ?		; Minuten \ Einschaltzeit
		.DB ?		; Stunden /
		.DB ?		; Sekunden \
		.DB ? 		; Minuten  | aktuelle Uhrzeit
		.DB ?		; Stunden  /
