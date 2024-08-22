; Listing 35: Gedächtnistraining

		.ORG 1

		AKO 000		; lade „0"
		ABS 100		; speichere als Trefferquote,
		ABS 125		; als Zähler...
		ABS 122		; und als Vergleichszahl
		AKO 001		; lade„1"
		ABS 121		; speichere als Schrittweite
		AKO 010		; lade „10"
		VKL 123		; ist das kleiner als die gewünschte Anzahl?
		SPB E1		; falls ja, springe zur Fehlermeldung
A1		LDA 125		; lade den Zähler
		ADD 121		; erhöhe um „1"...
		ABS 125		; und speichere wieder
		VGR 123		; Zähler größer als Anzahl, also genug erzeugt?
		SPB 031		;.031 falls ja, springe zum Anzeigen
		AKO 100		; lade „100" ...
		ADD 125		; und addiere den Zähler dazu
		ABS 126		; speichere dies als Adresse (Zeile 029)
		LDA 124		; lade die Zufallszahl
		VGR 122		; ist sie größer als „0" ?
		SPB A2		; falls ja, springe zu 022
		ADD 126		; sonst ist sie „0"! Erhöhe sie um die Adresse
A2		P2A 000		; Ausgabe der Zufallszahl an Port 2
		VZG 010		; warte kurz
		P1E 000		; lies Port 1: bitvertauschte Zufallszahl
		VKL 123		; ist sie kleiner als die Anzahl?
		SPB 028		;.028 falls ja, springe direkt zum Speichern
		SUB 121		; sonst verringere sie um „1"
A3		ABS 124		; speichere als Zufallszahl...
		AIS 126		; und als Computerzahl zum Vorzeigen
		SPU A1		; dann erzeuge die nächste in der Schleife
B1		AKO 001		; Anzeigeroutine - lade „1"
B2		ABS 125		; speichere als Zähler fürs Anzeigen
		VGR 123		; ist er größer als die Anzahl?
		SPB B3		; falls ja, sind alle angezeigt und wir stoppen
		AKO 100		; sonst lade „100" ...
		ADD 125		; und addiere den Zähler dazu
		ABS 126		; speichere als Adresse
		LIA 126		; lade damit indirekt die Computerzahl...
		ANZ			; und zeige sie an
		VZG 200		; \ drei Verzögerungen, die insgesamt eine
		VZG 100		; | halbe Sekunde ausmachen; sie können beliebig
		VZG 200		; / geändert werden (Anzeigedauer)
		LDA 125		; lade den Zähler
		ADD 121		; erhöhe ihn um „1"...
		SPU B2		; und mache weiter in der Anzeigeroutine
B3		HLT			; hier stoppt die Anzeigeroutine
		AKO 001		; Vergleichsroutine - lade „1"
C1		ABS 125		; speichere als Zähler fürs Vergleichen
		VGR 123		; schon mehr als die gewünschte Anzahl?
		SPB D1		; falls ja, zum Endresultat springen
		AKO 100		; sonst lade „100"
		ADD 125		; addiere dazu den Zähler
		ABS 126		; speichere als Adresse der Computerzahl
		LIA 126		; lade damit indirekt die Computerzahl...
		ABS 127		; und speichere sie in 127
		AKO 110		; dann lade „110"
		ADD 125		; addiere dazu den Zähler
		ABS 126		; speichere als Adresse der Benutzerzahl
		LIA 126		; lade damit die vom Spieler geratene Zahl
		VGL 127		; ist sie gleich der Computer-Zahl?
		SPB C3		; falls ja, springe zum Verrechnen
C2		LDA 125		; sonst lade den Zähler
		ADD 121		; erhöhe um „1"...
		SPU C1		; und mache weiter in der Auswertung
C3		LDA 100		; Verrechnungsroutine - lade die Trefferquote
		ADD 121		; erhöhe sie um „1"...
		ABS 100		; und speichere sie wieder
		SPU C2		; springe zum Vergleich der nächsten Zahl
D1		LDA 100		; Anzeigeroutine - lade die Trefferquote ...
D2		ANZ			; und zeige sie an
		SPU D2		; in einer Anzeigeschleife
E1		AKO 111		; Fehlerroutine - lade „111"
E2		ANZ			; zeige sie an: In 123 steht mehr als „10"!!
		SPU E2		; in einer Anzeigeschleife

; --------------------------------------------------

		.ORG 100

		.DB ?		; Trefferquote
		.DB ?		; \
		.DB ?		; |
		.DB ?		; |
		.DB ?		; |
		.DB ?		; |- maximal zehn Computerzahlen
		.DB ?		; |
		.DB ?		; |
		.DB ?		; |
		.DB ?		; |
		.DB ?		; /
		.DB ?		; \
		.DB ?		; |
		.DB ?		; |
		.DB ?		; |
		.DB ?		; |- die maximal zehn (vom Spieler geratenen)
		.DB ?		; |  Benutzerzahlen
		.DB ?		; |
		.DB ?		; |
		.DB ?		; |
		.DB ?		; /
		.DB 1		; ,1" als Schrittweite
		.DB 0		; ,0" als Vergleichszahl
		.DB ?		; Anzahl vorzustellender Zahlen
		.DB ?		; Startwert für Zufallszahlenerzeugung
		.DB ?		; Zähler
		.DB ?		; Adresszelle für indirekte Adressierung
		.DB ?		; Hilfszelle beim Vergleichen
