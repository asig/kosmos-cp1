; Listing 31: Digitalvoltmeter

		.ORG 1

		AKO 189		; lade „189" (oder einen anderen passenden Wert)...
		ABS 100		; als Zeitgrenze der Schleife speichern
A1		AKO 000		; lade „0"...
		ABS 101		; als Start-Zustand der Leitung,
		ABS 102		; als Flankenzähleranfang . ..
		ABS 103		; und als Zeitnullpunkt speichern
A2		P1E 001		; lies Port 1 Klemme 1: Pulseingabe
		VGL 101		; Zustand noch wie bei der letzten Eingabe?
		ABS 101		; speichere den jetzigen Zustand ab
		SPB A3		; falls ja, ist keine Flanke aufgetaucht: Nur Zeit zählen
		AKO 001		; sonst lade „1"...
		ADD 102		; und erhöhe so den Flankenzähler
		ABS 102		; speichere ihn wieder
A3		AKO 001		; Zeitmessung: lade „1 "
		ADD 103		; erhöhe so die Zeit-Zelle ...
		ABS 103		; und speichere sie wieder
		VKL 100		; ist die Zeit noch kleiner als die gesetzte Grenze?
		SPB A2		; falls ja, weiter einlesen und zählen
		LDA 102		; sonst lade den Flankenzähler
		ANZ 		; zeige ihn an ...
		SPU A1 		; und starte einen neuen Zyklus

		.ORG 100
		.DB ?		; Zeitschleifenlänge (Zeitbasis)
		.DB ?		; letzter Zustand von Port 1 Klemme 1
		.DB ?		; Flankenzähler (Frequenzmessung)
		.DB ?		; Zeitzähler
