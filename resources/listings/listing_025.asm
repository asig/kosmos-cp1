; Listing 25: Rucksackprogramm

        .ORG 1

start   AKO 001     ; Lade „1" in den Akku
        ABS 101     ; Speichere den Akku-Inhalt in 101
loop    ABS 000     ; Speichere den Akku-Inhalt in 000
; -----------------------------------------------------------------
        SPU rs      ; Springe zum „Rucksack"
; -----------------------------------------------------------------
back    SPB start   ; Wenn ja, beginne wieder von vorn
        SPU loop    ; Wenn nein, mache bei 003 weiter
; -----------------------------------------------------------------
rs      ADD 101     ; Addiere eine „1"            \
        VGL 100     ; Ist Akku-Inhalt schon „7"?  |_ Rucksack
        SPU back    ; Springe zurück nach 005     /
