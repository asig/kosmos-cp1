; CP5-Listing 3: Wanderndes Lichtband

        .ORG 1

start   LIA 028     ; indirekt Ausgabewert laden...
        P2A 000     ; und ausgeben
        VZG 030     ; 30 ms verzögern
        LDA 028     ; Adreßzelle laden
        VGL 029     ; ist sie gleich der Endadresse?
        SPB sub     ; wenn ja, zum Subtrahieren springen
        ADD 020     ; sonst "1" addieren und...
        ABS 028     ; als neue Adresse speichern
        SPU start   ; einen neuen Durchgang beginnen
sub     SUB 020     ; "1" subtrahieren
        ABS 028     ; als neue Adresse speichern
        VGL 030     ; ist Akku gleich Anfangadresse?
        SPB start   ; wenn ja, von vorn beginnen
        LIA 028     ; sonst neuen Ausgabewert laden...
        P2A 000     ; und ausgeben
        VZG 030     ; 30 ms verzögern
        LDA 028     ; Adreßzelle laden und...
        SPU sub     ; zum Subtrahieren springen

        .DB 000     ; \
        .DB 001     ; |
        .DB 003     ; |
        .DB 007     ; |
        .DB 015     ; |_ Ausgabewerte
        .DB 031     ; |
        .DB 063     ; |
        .DB 127     ; |
        .DB 255     ; /
        .DB 019     ; Anfangsadresse
        .DB 027     ; Endadresse
        .DB 019     ; Anfangsadresse