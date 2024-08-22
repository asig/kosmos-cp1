; Bonus 1: K.I.T.T. Effekt: Hin und her schwingendes LED.
; (C) 2017 Andreas Signer <asigner@gmail.com>

        .ORG 1

		; Setup
		LDA start_addr      ; Start-Adresse...
        ABS cur_addr        ; ... als aktuelle Adresse speichern

loop   	LIA cur_addr        ; Aktuelles Pattern lesen
        P2A 000             ; An P2 ausgeben
        VZG 10              ; und ein bisschen warten.

        ; Berechnung naechste Adresse
        LDA cur_addr        ; Aktuelle Adresse laden
        VGL end_addr        ; Mit End-Adresse vergleichen
        SPB reset           ; Falls gleich, dann resetten
        ADD inc             ; Sonst um eins erhoehen
        SPU endloop
reset   LDA start_addr      ; Start-Adresse laden
endloop ABS cur_addr        ; Neue Adresse als aktuelle Adresse speichern
        SPU loop            ; Und zurueck zur Anzeige

        ; Daten-Bereich
inc         .DB 1       ; Inkrement
start_addr	.DB data_start  ; Start-Adresse
end_addr    .DB data_end    ; End-Adresse
cur_addr    .DB ?           ; Aktuelle Adresse
data_start	.DB  16,  16,  16,  32,  32,  32,  32,  64,  64,  64,  64, 128, 128, 128, 128, 128
            .DB 128, 128, 128, 128, 128, 128,  64,  64,  64,  64,  32,  32,  32,  32,  16,  16
            .DB  16,   8,   8,   4,   4,   4,   4,   2,   2,   2,   2,   1,   1,   1,   1,   1
            .DB   1,   1,   1,   1,   1,   1,   2,   2,   2,   2,   4,   4,   4,   4,   8,   8
data_end

        ; Die Bit-Patterns wurden mit folgendem Python-Skript errechnet:
        ; for i in range (0,64): s=sin(2*pi/64.0*i)/2+.5; b=int(s*7+.5); print "%d," % int(1<<b),
