; Listing 21: Countdown

        .ORG 1

        AKO 000     ; \
        ABS 100     ; |
        AKO 001     ; |_ Zahlenwerte im Datenbereich speichern
        ABS 101     ; |
        AKO 240     ; |
        ABS 102     ; /
        ANZ         ; Zeige zu Beginn „240" an
        VZG 250     ; Verzögere 250 ms
        VZG 250     ; Verzögere nochmals 250 ms
        AKO b1      ; Lade die Adresse, zu der aus dem Unterprogramm zurückgesprungen werden soll
        ABS 103     ; Speichere sie in 103
        P1E 008     ; Frage die Alarmtaste ab
        VGL 100     ; Ist sie gedrückt?
        SPB alarm   ; Wenn ja, springe ins Unterprogramm
b1      VZG 250     ; Wenn nein, fahre mit 250 ms Verzögerung fort
        VZG 250     ; Verzögere nochmals 250 ms
loop    LDA 102     ; Lade den Inhalt der Zählzelle in den Akku
        SUB 101     ; Subtrahiere „1 "
        ANZ         ; Zeige den neuen Akku-Inhalt an
        VGL 100     ; Prüfe, ob „0 " bereits erreicht ist
        SPB alarm2  ; Wenn ja, springe zum Raketenstart nach 070
        ABS 102     ; Wenn nein, speichere Akku-Inhalt in Zählzelle
        AKO 000     ; Lade „0 " in den Akku, um das Blinkprogramm zu beginnen
        P2A 001     ; Gib die Information an Klemme 1 des Port 2
        VZG 250     ; Verzögere 250 ms
        NEG         ; Ändere die „0 " im Akku durch Negieren in eine „1 "
        P2A 001     ; Gib diese Information an Klemme 1 des Port 2
        VZG 250     ; Verzögere 250 ms
        AKO b2      ; Lade die Adresse, zu der aus dem Unterprogramm zurückgesprungen werden soll
        ABS 103     ; Speichere sie in 103
        P1E 008     ; Frage die Alarmtaste ab
        VGL 100     ; Ist sie gedrückt?
        SPB alarm   ; Wenn ja, springe ins Unterprogramm
b2      AKO 001     ; Wenn nein, beginne ein weiteres Blinkprogramm
        P2A 002     ; Gib die Akku-Information an Klemme 2 des Port 2
        VZG 250     ; Verzögere 250 ms
        NEG         ; Ändere die „0 " im Akku durch Negieren in eine „1 "
        P2A 002     ; Gib diese Information an Klemme 2 des Port 1
        VZG 250     ; Verzögere 250 ms
        AKO b3      ; Lade die Adresse, zu der aus dem Unterprogramm zurückgesprungen werden soll
        ABS 103     ; Speichere sie auf 103
        P1E 008     ; Frage die Alarmtaste ab
        VGL 100     ; Ist sie gedrückt?
        SPB 050     ; Wenn ja, springe ins Unterprogramm
b3      SPU loop    ; Wenn nein, fahre mit dem Countdown auf Zeile 017 fort

        ; Ab hier: Unterprogramm
        .ORG 50
alarm   AKO 001     ; Lade „1" in den Akku
        P2A 002     ; Gib die Akku-Information auf Klemme 3 des Port 2
        VZG 250     ; Verzögere 250 ms
        NEG         ; Andere die „1" im Akku durch Negieren in eine „0"
        P2A 003     ; Gib diese Information auf Klemme 3 des Port 2
        VZG 250     ; Verzögere 250 ms
        P1E 007     ; Frage die Taste „Alarm aufheben" ab
        VGR 100     ; Ist sie nicht gedrückt?
        SPB alarm   ; Wenn nicht gedrückt, dann springe zum Alarmanfang zurück
        SIU 103     ; Wenn gedrückt, zu der Stelle des Programms zurückkehren, an der unterbrochen wurde

        ; Ab hier: Programmteil „Daueralarm"
        .ORG 70
alarm2  AKO 001     ; Lade „1" in den Akku für den Raketenstart
        P2A 003     ; Gib die Akku-Information auf Klemme 3 des Port 2 (Daueralarm = Raketen-Start)
        HLT         ; Halte an

        .ORG 100
        .DB 0       ; Vergleichszahl
        .DB 1       ; Schrittweite
        .DB ?       ; Speicherzelle für aktuellen Zählerstand
        .DB ?       ; Rücksprungadresse
