; Listing 54: Computer-Schiffsschlacht

		.ORG 65

		AKO 000		; Lösche das Spielfeld und belege es neu: lade „0"
		ABS 246		; speichere sie als Startadresse zum Löschen...
		ABS 247		; und als Zähler (allgemein)
A1		AKO 000		; lade „0"...
		AIS 246		; und speichere sie indirekt im Spielfeld
		AKO 001		; lade „1"...
		ADD 246		; und erhöhe so die Spielfeldadresse
		ABS 246		; speichere sie wieder
		VGR 242		; ist sie größer als „64", also fertig?
		SPB A2		; falls ja, springe weiter
		SPU A1		; sonst springe zurück, um erneut zu löschen

		; Erzeuge drei Zweier-Schiffe:
A2		AKO 056		; lade „56"
		ABS 248		; speichere sie als maximale Adresse
		AKO R1		; lade 080 als Rücksprungadresse...
		SPU U1		; und springe in die Zufallszahlenroutine
R1		LDA 249		; lade das gefundene Zufallsfeld...
		ADD 237		; und addiere „8" dazu
		ABS 250		; speichere dies als Adresse der zweiten Hälfte
		LIA 250		; lade den Inhalt des Nachbarfeldes
		VGR 234		; ist dieser größer als „0"?
		SPB A2		; falls ja, ist es schon belegt: neuer Versuch
		AKO 020		; sonst lade „20" als Zweier-Symbol...
		AIS 249		; und speichere es in der ersten und...
		AIS 250		; auch in der zweiten Hälfte des Schiffes
		AKO 001		; dann lade „1"...
		ADD 247		; und erhöhe so den Schiffszähler
		ABS 247		; speichere ihn wieder
		VKL 235		; ist er noch kleiner als „3"?
		SPB A2		; falls ja, suche ein weiteres Feld

		; Erzeuge drei Einer-Schiffe:
L2		AKO 064	; lade „64" als Grenze des Feldes...
		ABS 248		; und speichere sie im Maximalfeld
		AKO R2		; lade 098 als Rücksprungadresse...
		SPU U1		; und springe in die Zufallsroutine
R2		AKO 010		; lade „10" als Einer-Symbol...
		AIS 249		; und speichere sie indirekt im gefundene n Feld
		AKO 001		; dan n lade „1"...
		ADD 247		; und erhöhe den Schiffszähler
		ABS 247		; speichere ihn wieder
		VKL 236		; ist er noch kleiner als „6"?
		SPB L2		; falls ja, verstecke noch ein Schiff
		ANZ			; sonst zeige die „6" als Zeichen , daß die Zufallsbelegung abgeschlossen ist.
L3		P1E 000		; Eingabe der Zeile: lies Port 1 in den Akku
		VGL 243		; ist der Wert „255", also nichts gedrückt?
		SPB L3		; falls ja, warte weiter
		ABS 251		; sonst speichere die Eingabe in der Zeilenzelle
		AKO 001		; lade „1" in den Akku...
		P4A 000		; und gib sie aus am Tongenerator (Quittung)
		ANZ			; zeige sie außerdem an
		AKO 000		; dann lade „0"
		VZG 100		; warte aber noch 100 ms...
		P4A 000		; und schalte so den Ton wieder ab
L4		P1E 000		; lies Port 1 wiederu m
		VKL 243		; ist der Wert noch kleiner als „255"?
		SPB L4		; falls ja, warte aufs Loslassen der Taste
L5		P1E 000		; ; Eingabe der Spalte: lies Port 1 in den Akku
		VGL 243		; ist der Wert „255", also nichts gedrückt?
		SPB L5		; falls ja, warte weiter
		ABS 252		; sonst speichere die Eingabe in der Spaltenzelle
		AKO 001		; Umwandlung der Dualzahl in Zeile/Spalte: lade „1" als Startwert...
		ABS 253		; und speichere sie in der Vergleichszelle
		AKO 002		; lade „2" in den Akku
		ANZ			; zeige sie als zweiten Tastendruck an
		P4A 000		; gib sie auch als Quittung an de n Tongenerator...
		VZG 100		; und warte 100 ms
		AKO 255		; lade „255"...
		SUB 251		; und subtrahiere die gelesene „Zeile" davon
		ABS 251		; speichere diese veränderte wieder
		AKO 000		; lade „0"...
		ABS 247		; und speichere sie in der Zählerzelle
		P4A 000		; schalte auch den Ton wieder ab
		VZG 250		; und warte ein bißchen („Denkpause")
L6		LDA 251		; lade die veränderte Zeile
		VGL 253		; ist sie gleich der Vergleichszelle?
		SPB A3		; falls ja, ist dies fertig
		LDA 253		; sonst lade die Vergleichszelle...
		ADD 253		; und verdoppel e sie durch Addition
		ABS 253		; speichere sie wieder
		AKO 008		; lade „8"...
		ADD 247		; und erhöhe daru m die Zählerzelle
		ABS 247		; speichere sie wieder...
		SPU L6		; und springe zurück zum Vergleichen
A3		LDA 247		; lade den Zähler...
		ABS 251		; und speichere den Wert (endlich) als Zeile
		AKO 001		; dann lade „1"
		ABS 253		; speichere dies wieder in der Vergleichszelle
		ABS 247		; sowie in der Zählerzelle
		AKO 255		; lade „255"...
		SUB 252		; und subtrahiere die eingelesene Spalte davo n
		ABS 252		; speichere die veränderte Spalte wieder
L7		LDA 252		; lade die Spalte
		VGL 253		; ist sie gleich der Vergleichszelle?
		SPB A4		; falls ja, haben wir sie entschlüsselt
		LDA 253		; sonst lade die Vergleichszelle...
		ADD 253		; und verdoppel e sie durch Addition
		ABS 253		; speichere sie wieder
		AKO 001		; lade „1"...
		ADD 247		; und erhöhe so den Zähler
		ABS 247		; speichere ihn wieder...
		SPU L7		; und springe zurück zum Vergleichen
A4		LDA 251		; lade die Zeile...
		ADD 247		; und addiere den Zähler ( = Spalte) dazu
		ABS 254		; speichere als Adresse für das Feld, das beschossen wurde
		AKO L3		; lade 106 als Rücksprungadresse
		ABS 255		; und speichere sie in der Adreßzelle
		LIA 254		; Prüfe, ob ein Treffer erzielt wurde: lade den Inhalt des beschossenen Feldes
		VGL 234		; ist er „0", also kein Schiff dort?
		SPB HO		; falls ja, springe in die DANEBEN-Routine
		VGL 238		; ist er „10", also ein Einer getroffen?
		SPB H2		; falls ja, springe in die Versenkt-Routine

		; Zweier wurde getroffen:
		LDA 254		; sonst lade die Adresse des Feldes...
		ADD 237		; und erhöhe sie u m „8"
		ABS 254		; speichere dies als zweite Hälfte
		VGR 242		; ist es größer als „64", also außerhalb?
		SPB A7		; falls ja, springe zur Alternative
		LIA 254		; lade den Inhalt der zweiten Hälfte
		VKL 241		; ist er kleiner als „20", also kein Schiff da?
		SPB A7		; falls ja, springe zur Alternative
		SPU H1		; springe zur Trefferanzeige

		; Alternative: die untere Hälfte wurde beschossen,
		; der Rest liegt drüber:
A7		LDA 254		; lade die Adresse der falschen zweiten Hälfte...
		SUB 240		; und verringere sie um „16"
		ABS 254		; speichere sie wiede r
H1		AKO 010		; lade „10"...
		AIS 254		; und speichere dies in der zweiten Hälfte
		LDA 244		; Treffer: lade „;"...
		ANZ			; und zeige sie an : TREFFER
		AKO 001		; lade „1"...
		SPU V1		; und springe zur Tonausgabe

HO		LDA 234		; Fehlschuß: lade „;"
		ANZ			; und zeige sie an : DANEBEN
		AKO 002		; lade „2"...
		SPU V1		; und springe zur Tonausgabe
H2		LDA 245		; Versenkt: lade „;"...
		ANZ			; und zeige sie an : VERSENKT
		AKO R4		; lade 202 als Rücksprungadresse...
		ABS 255		; und speichere sie in der Adreßzelle
		AKO 004		; dann lade „4"...
		SPU V1		; und springe zur Tonausgabe
R4		AKO 001		; lade „1"...
		ADD 000		; und addiere sie zum Versenkungs-Zähler
		ABS 000		; speichere ihn wieder
		VKL 236		; ist er noch kleiner als „6"?
		SPB L3		; falls ja, springe zur Eingabe zurück
		HLT			; sonst: Ende des Programms

		; Zufallszahlenerzeugung zur Feldbelegung:
U1		ABS 255		; speichere die übergebene Rücksprungsadresse
U2		LDA 249		; lade die bisherige Zufallszahl...
U3		ADD 239		; und addiere „11" dazu
		P2A 000		; gib sie an Port 2 parallel aus...
		P3E 000		; und lies sie an Port 3 vertauscht wieder ein
		VGR 248		; ist sie jetzt größer als die maximale?
		SPB U3		; falls Ja, weiter probieren
		VGL 234		; ist sie etwa „0"?
		SPB U3		; auch dann weiter probieren
		ABS 249		; sonst speichere als neue Zufallszahl und Adresse des zu belegenden Feldes
		LIA 249		; lade den Inhalt dieses Feldes
		VGR 234		; ist er größer als „0", also scho n belegt?
		SPB U2		; falls ja, suche weiter
		SIU 255		; sonst springe zurück ins Hauptprogramm

		; Tonausgabe als Trefferwiedergabe:
V1		P4A 000		; gib den übergebenen Wert an Port 4 aus
		VZG 250		; \
		VZG 250		;  |_ und warte insgesamt
		VZG 250		;  |  eine ganze Sekunde
		VZG 250		; /
		AKO 000		; dann lade „0"
		P4A 000		; und schalte den Ton so wieder aus
		SIU 255		; springe zurück ins Hauptprogramm

		.ORG 234
		.DB 0		; „0" als Vergleichszahl
		.DB 3		; „3" als Vergleichszahl
		.DB 6		; „6" als Vergleichszahl
		.DB 8		; „8" zum Addieren
		.DB 10		; „10" als Einer-Symbol
		.DB 11		; „11" für die Zufallszahlen
		.DB 16		; „16" zum Subtrahieren
		.DB 20		; „20" als Zweier-Symbol
		.DB 64		; „64" als Obergrenze des Feldes
		.DB 255		; „255" als Vergleichszahl
		.RAW 11.111	; „11.111" als Anzeige
		.RAW 22.222	; „22.222" als Anzeige

; 246: Adresse beim Löschen des Spielfeldes
; 247: Universalzähler für alle Zweck e
; 248: maximal mögliche Zufallszahl
; 249: erzeugte Zufallszahl (siehe Text!)
; 250: Nachbarfeld (darunter)
; 251: Zeile der Eingabe (erste Eingabe)
; 252: Spalte der Eingabe (zweite Eingabe)
; 253: Vergleichszahl zur Zeile-/Spaltenbestimmung
; 254: Adresse des beschossenen Feldes
; 255: Rücksprungadreßzelle
; 000: Zahl der bisher versenkten Schiffe