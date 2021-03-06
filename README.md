# Anti-kontingent

Anti-kontingent je java aplikacia, ktora sluzi na roboticke ovladanie mobilnej stranky nakupu listka ZSSK (ikvc). Hlavnym cielom bolo vytvorenie webstranky podobnej cp.sk, avsak ku kazdemu spoju bude automaticky zobrazena aj dostupnost kontingentu (bezplatych listkov). O kontingente sa da dalej zistit aj podrobny prehlad o obsadenych a volnych usekov daneho vlaku. Aplikacia sa sklada zo styroch hlavnych blokov ikvcAPI, server, webstranka, konzolova aplikacia. 

- ikvcAPI je rozhranie na ovladanie webstranky (HTMLUnit).
- server je SocketIO server v jave, ktory pocuva na poziadavky, spracuje ich a odosle odpoved ako JSON (netty-socketio).
- webstranka je webove rozhranie podobne stranke cp.sk, ktore komunikuje so SocketIO java serverom.
- ako alternativa k webovemu rozhraniu je dostupna aj primitivna konzolova aplikacia.

Aplikacia je zavisla na strukture ZSSK stranky, takze castokrat moze po jej zmene prestat fungovat. Vsetky chyby vyhadzuju exception s dobrym popisom, takze pripadna oprava nemusi byt problem. Pripadne Pull requesty su vitane :)

Ak si programator a chces vyuzit ikvcAPI pre vlastnu aplikaciu, odporucame pozriet zdrojovy kod konzolovej aplikacie.

Vytvorili: Simona Backovska, Ondrej Hrusovsky, Oliver Sabik
ako projekt pre predmet Extremne Programovanie (2016/2017)
FMFI UK BA

Webove rozhranie:

![alt text](https://i.imgur.com/q3G4Qz4.png)

![alt text](https://i.imgur.com/ZYyB6T3.png)

Konzolova aplikacia:

![alt text](https://i.imgur.com/hm2C6rq.png)

![alt text](https://i.imgur.com/OkggCK9.png)

Spustitelne subory:

Aplikacia vyzaduje Java Runtime 64bit. V oboch balikoch je prilozeny jednoduchy navod. Konzolova aplikacia je spolahlivejsia.

Stiahnutie webovej aplikacie:

https://github.com/Skylonxe/antikontingent/raw/master/Binaries/AntikontingentWeb(923152a).zip

Stiahnutie konzolovej aplikacie:

https://github.com/Skylonxe/antikontingent/raw/master/Binaries/AntikontingentKonzola(923152a).zip
