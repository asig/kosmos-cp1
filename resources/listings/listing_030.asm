; Listing 30: Telefonzeittakt-Gebührenanzeiger

		.ORG 0
		.DB ?		; Endpreis bzw. Zwischenstand

		.ORG 1

		AKO 000		; lade „0"
		ABS 100		; speichere als Vergleichszahl
		AKO 001		; lade „1"
		ABS 101		; speichere als Schrittweite
		AKO 023		; lade „23"
		ABS 102		; speichere als Gebühreneinheit...
		ABS 105		; und als Anfangswert des Endpreises
A1		AKO 008		; lade „8"
		ABS 103		; speichere als Taktlänge (8-Minuten-Takt)
A2		AKO 240		; lade „240"
		ABS 104		; speichere als Viertelsekundenzähler
		LDA 105		; lade den Endpreis
		ANZ    		; zeige ihn an ...
		ABS 000		; und speichere ihn in 000 zum Anzeigen
A3		LDA 104		; lade den Viertelsekundenzähler
		VZG 219		; warte 219 ms (korrigierte Viertelsekunde)
		SUB 101		; verringere den Zähler um „1"
		ABS 104		; speichere ihn wieder
		VGL 100		; ist er schon bei „0", also Minute vorbei?
		SPB A4 		; falls ja, springe zur Minutenzählung
		SPU A3 		; sonst springe zurück (während der Minute)
A4		LDA 103		; lade den Minutenstand des Taktes
		SUB 101		; verringere ihn um „1"...
		ABS 103		; und speichere ihn wieder
		VGL 100		; ist die Minute bei „0", also Taktende angelangt?
		SPB A5 		; falls ja, springe zum neuen Taktbeginn
		SPU A2 		; sonst springe zurück (während des Taktes)
A5		LDA 105		; ein neuer Takt beginnt! Lade den Gesamtpreis
		ADD 102		; erhöhe ihn um die Gebühreneinheit
		ABS 105		; und speichere ihn wieder
		SPU A1 		; springe zurück zur Taktmessung

		.ORG 100
		.DB 0		; „0" als Vergleichszahl
		.DB 1		; „1" als Schrittweite
		.DB 23		; „23" als Gebühreneinheit
		.DB 8		; „8" als Taktlänge in Minuten
		.DB ?		; Viertelsekundenzähler (240 pro Minute)
		.DB ?		; Endpreis bzw. Zwischenstand
