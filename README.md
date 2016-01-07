# Short Circuit, created at #DroidconHack15

A NFC enabled card game about battery drain. 1 to 4 players.

Created by [Andrew Jack](https://twitter.com/andr3wjack), [Sam Parkinson](https://twitter.com/samparkinson_), [Laith Nurie](https://twitter.com/laith_nurie), [Ted Eriksson](https://twitter.com/Ted_Eriksson)


## Setting up Google Play Services Nearby
Follow [this](https://developers.google.com/nearby/messages/android/get-started) guide to obtain a API key for the nearby API. Once obtained add it to the manifest file.

I recommend replacing 'keys/shortcircuit.keystore' with a new one before obtaining an API key for it.

## Rules

### Setup

* Shuffle the deck and place it face down within reach of all players
* Each player should be dealt one card
* The player with the lowest battery goes first

### Gameplay

* Draw a card from the top of the deck
* Play a card from your hand, then add it to a discard pile
* When there are no more cards, or there is only 1 player left with a charge the round ends

The winner is the player with the most charge remaining.

Tap cards on the battery that should be affected.

## Card Mappings

Card ID | Card Effect
--------|------------
1 | Charge 10%
2 | Charge 10%
3 | Charge 20%
4 | Charge 30%
5 | Drain 10%
6 | Drain 10%
7 | Drain 20%
8 | Drain 30%
9 | Drain 40%
10 | Drain 50%
11 | Drain half your current charge!
12 | Steal 10% charge from another player
13 | Steal 20% charge from another player
14 | Steal 10% charge from every other player
15 | Select another player to drain 20% charge
16 | Select another player to drain 20% charge
17 | Select another player to gain 10% charge
18 | Select another player to drain 30% charge
19 | Select another player to drain 30% charge
20 | Every player drains 20%!

License
-------

    Copyright 2015 Andrew Jack, Sam Parkinson, Laith Nurie, Ted Eriksson

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
