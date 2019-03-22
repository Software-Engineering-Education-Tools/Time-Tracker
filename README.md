# Time Tracker
## Was ist das?
Dieses Plugin für IntelliJ nimmt Interaktionsdaten des Nutzers auf und stellt diese in geeigneter Form dar. Da sich das Plugin auf die Nutzung durch Studierende fokussiert, kann durch die zusätzliche Bereitstellung einer Konfigurationsdatei weitere Meta-Daten an den Nutzer des Plugins weitergegeben werden.

## Installation des Development-Sourcecodes
Zur Installation (und eventuellen Bearbeitung) des Sourcecodes muss in IntelliJ die Option "Neu -> Projekt von existierenden Resourcen..." ausgewählt und dann der Pfad zum Ordner "plugin" dieses Repositories ausgewählt werden.
Die danach folgenden Dialoge müssen zumeist bestätigt werden. Besonders wichtig dabei ist u.a. die Installation der benötigten Libraries!

## Nutzung
Bei der Programmierung von Plugins für die IntelliJ IDE ermöglicht es IntelliJ nach einer erfolgreichen Kompilation des Codes das Plugin in einem "sauberen" Umfeld zu testen.
Dabei handelt es sich um eine Instanz der Entwicklerumgebung die sich verhält als würde der Nutzer die IDE erstmalig verwenden. Daher werden vermutlich einige Konfigurationseinstellungen wie das Auswählen des Farbschemas nötig sein.
Ist dies geschehen, kann ein Projekt erstellt oder ausgewählt werden.
Die Schaltfläche zum Anzeigen des Trackers befindet sich dann am rechten underen Rand des Fensters.  

Für einige Funktionen ist das Hinzufügen einer Konfigurationsdatei nötig. Diese liegt im Repository als mi-visualizer.config vor und kann einem Projekt hinzugefügt werden.