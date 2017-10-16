# antikontingent

Anti-kontingent je java aplikacia, ktora sluzi na roboticke ovladanie mobilnej stranky nakupu listka ZSSK (ikvc). Hlavnym cielom bolo vytvorenie webstranky podobnej cp.sk, avsak ku kazdemu spoju bude automaticky zobrazena aj dostupnost kontingentu (bezplatych listkov). O kontingente sa da dalej zistit aj podrobny prehlad o obsadenych a volnych usekov daneho vlaku. Aplikacia sa sklada z troch hlavnych blokov ikvcAPI, server, webstranka. 

- ikvcAPI je rozhranie na ovladanie webstranky.
- server je SocketIO server v jave, ktory pocuva na poziadavky, spracuje ich a odosle odpoved ako JSON.
- webstranka je webove rozhranie podobne stranke cp.sk, ktore komunikuje so SocketIO java serverom.

Aplikacia je zavisla na strukture ZSSK stranky, takze castokrat moze po jej zmene prestat fungovat. Vsetky chyby vyhadzuju exception s dobrym popisom, takze pripadna oprava nemusi byt problem.

Vytvorili: Simona Backovska, Ondrej Hrusovsky, Oliver Sabik
ako projekt pre predmet Extremne Programovanie
FMFI UK BA

![alt text](https://i.imgur.com/q3G4Qz4.png)
