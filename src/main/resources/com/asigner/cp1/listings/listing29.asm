; Listing 29: Reaktionstester mit variabler Vorlaufzeit

		.ORG 0
		.DB ?		; Ergebniszelle (Zeitzähler)

		.ORG 1

		AKO 001		; lade „1" in den Akku ...
		ABS 102		; und speichere sie in 102 (Zählerschrittweite)
		AKO 000		; lade „0 " in den Akku ...
		ABS 101		; und speichere sie in 101 (Vergleichszahl)
		AKO 010		; lade „10"...
		ABS 100		; und speichere sie in 100 (zum Zehnerabtrennen)
		LDA 000		; lade Inhalt von 000: letzter Zählerstand
A0		VGR 100		; ist er größer als „10"?
		SPB A1		; falls ja, Sprung zu 011 (Abtrennen des Zehnerteils)
		SPU A2		; sonst Sprung zu 013
A1		SUB 100		; subtrahiere „10 " vom Akkuinhalt...
		SPU A0		; und mache weiter beim Vergleich
A2		VGL 101		; Akku ist kleiner als „10"! Ist er schon „0"?
		VZG 200		; warte ein bißchen
		SPB A3		; falls ja, springe zu 018 (Ende der Verzögerung)
		SUB 102		; sonst subtrahiere „1 " vom Akku ...
		SPU A2		; und springe zurück zum Vergleich mit „0"
A3		AKO 000		; variable Verzögerung zuende - „0" als Zeitanfang der Reaktionszeit laden
		ANZ 		; Anzeige des Akkuinhalts
A4		ADD 102		; addiere „1"...
		ANZ 		; und zeige erneut an
		ABS 000		; speichere den Zählerstand in Zelle 000
		VZG 005		; warte kurz ...
		SPU A4		; und springe dann zum Weiterzählen

		.ORG 100

		.DB 10		; „10" zum Zehnerabtrennen
		.DB 0		; Vergleichszahl „0"
		.DB 1		; Zählerschrittweite „1"

