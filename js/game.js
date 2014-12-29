function Game(players) {
    this.deck = new Deck();
    this.numPlayers = 4;
    this.numKittyCards = 4;
    this.winningScore = 21;
    this.dealerIndex = Math.floor(Math.random() * this.numPlayers);
    this.players = [];
    this.defaultPlayerNames = ["Greg", "Tom", "Erin", "Fred"];
    for(var i = 0; i < this.numPlayers; i++) {
        var name = players && players.length && players.length == this.numPlayers? players[i] : this.defaultPlayerNames[i];
        this.players[i] = new Player(i, name);
    }
    this.gameView = new GameView(this);
    this.score = [0, 0];
    this.gameView.updateScore();
    this.playRound();
};

Game.prototype.gameOver = function() {
    return this.score[0] >= this.winningScore || this.score[1] >= this.winningScore;
};

Game.prototype.playRound = function() {
    this.gameView.switchToPlayer(this.players[(this.dealerIndex + 1) % this.numPlayers]);
    this.deal();
    this.bid();
    //this.takeKittyAndCallTrump();
    //this.playHand();
};

Game.prototype.deal = function() {
    this.deck.shuffle();
    this.kitty = [];
    var playerIndex = this.dealerIndex + 1;
    for(var i = 0; i < this.deck.length; i++) {
        if(playerIndex >= this.numPlayers) {
            if(this.kitty.length < this.numKittyCards) {
                this.kitty.push(this.deck.cards[i]);
                continue;
            }
            else {
                playerIndex = 0;
            }
        }

        var player = this.players[playerIndex];
        player.addCard(this.deck.cards[i]);
        playerIndex++;
    }
    this.gameView.showHand();
};

Game.prototype.nextPlayer = function(player) {
    var nextIndex = (player.index + 1) % this.numPlayers;
    return this.players[nextIndex];
};

Game.prototype.bid = function() {
    this.bids = [];
    this.winningBid = {"bid":0};
    this.gameView.showBid();
};

Game.prototype.makeBid = function(bid) {
    this.gameView.showMadeBid(bid);
    this.bids.push(bid);
    if(bid > 2) this.winningBid = {"bid":bid, "player":this.gameView.player};
    if(this.bids.length < this.numPlayers) {
        this.gameView.switchToPlayer(this.nextPlayer(this.gameView.player));
        this.gameView.showBid();
    }
    else if(this.winningBid.bid >= 3) {
        this.takeKittyAndCallTrump(this.winningBid);
    }
    else {
        this.playRound(); // start over if no bids high enough to play
    }
};

Game.prototype.takeKittyAndCallTrump = function(winningBid) {
    this.gameView.kittyTakerDiv.innerHTML = winningBid.player.name;
    this.gameView.winningBidDiv.innerHTML = winningBid.bid;
    for(var i = 0; i < this.kitty.length; i++) { winningBid.player.addCard(this.kitty[i]); }
    this.gameView.switchToPlayer(winningBid.player);
    this.gameView.showHand();
    this.trump = {};
    this.gameView.callTrump();
};

Game.prototype.setTrumpHigh = function(isHigh) {
    this.trump["isHigh"] = isHigh;
    if(this.isValidTrump()) this.gameView.showTrumpDone();
};

Game.prototype.setTrumpSuit = function(suitName) {
    this.trump["suitName"] = suitName;
    if(this.isValidTrump()) this.gameView.showTrumpDone();
};

Game.prototype.isValidTrump = function() {
    return this.trump.hasOwnProperty("isHigh") && this.trump.hasOwnProperty("suitName");
};

Game.prototype.discard = function() {
    this.gameView.trumpDiv.innerHTML = (this.trump.isHigh? "High " : "Low ") + this.trump.suitName;
    w.hide(this.gameView.callTrumpDiv);
    if(this.trump.suitName === "No Trump") {
        this.startHand();
    }
    else {
        this.discarded = [];
        this.gameView.discard();
    }
};

Game.prototype.discardSelect = function(card) {
    var existingIndex = this.discarded.indexOf(card);
    if(existingIndex >= 0) {
        this.discarded.splice(existingIndex, 1); // remove card from discarded array
        w.hide(this.gameView.discardDoneDiv);
        card.lower();
    }
    else {
        if(this.discarded.length == this.numKittyCards) return;
        this.discarded.push(card);
        if(this.discarded.length == this.numKittyCards) w.show(this.gameView.discardDoneDiv);
        card.raise();
    }
};

Game.prototype.discardDone = function() {
    w.hide(this.gameView.discardDiv);
    for(var i = 0; i < this.discarded.length; i++) {
        this.gameView.removeCard(this.discarded[i]);
    }
    this.startHand();
};

Game.prototype.startHand = function() {
    this.trickCount = [0, 0];
    this.gameView.updateTrickCount();
    this.newTrick();
};

Game.prototype.newTrick = function() {
    this.currentTrick = [];
    this.winningCard = {};
    for(var i = 0; i < this.players.length; i++) {
        if(this.players[i].playedCard) {
            this.players[i].playedCard.hide();
            this.players[i].playedCard = null;
        }
    }
    this.gameView.playCardOnclick();
};

Game.prototype.playCard = function(card) {
    if(!this.gameView.validatePlay(card)) return;

    this.gameView.moveCardIntoPlay(card);
    this.currentTrick.push(card);
    if(this.isWinningCard(card)) {
        this.winningCard = {"card":card, "player":this.gameView.player};
    }

    if(this.currentTrick.length == this.numPlayers) { // if this trick is done
        this.trickCount[this.winningCard.player.index % 2]++;
        this.gameView.updateTrickCount();
        if(this.trickCount[0] + this.trickCount[1] == 12) { // this round is done
            this.handDone();
            if(this.gameOver()) {
                this.gameView.writeMessage("Game Over!");
            }
            else {
                this.startHand();
                this.playRound();
            }
        }
        else {
            this.gameView.switchToPlayer(this.winningCard.player);
            this.newTrick();
        }
    }
    else { // trick is not done, next play...
        this.gameView.switchToPlayer(this.nextPlayer(this.gameView.player));
        this.gameView.playCardOnclick();
    }
};

Game.prototype.handDone = function() {
    var kittyTakingTeamIndex = this.winningBid.player.index % 2 === 0? 0 : 1;
    var opposingTeamIndex = kittyTakingTeamIndex === 0? 1 : 0;
    var tricksToDefend = 8 - this.winningBid.bid;
    var score = this.winningBid.bid;
    var winningIndex;
    var extras = 0;
    if(this.trickCount[opposingTeamIndex] >= tricksToDefend) {
        winningIndex = opposingTeamIndex;
        var extraTricks = this.trickCount[opposingTeamIndex] - tricksToDefend;
        if(extraTricks > 1) extras = extraTricks - 1;
    }
    else {
        winningIndex = kittyTakingTeamIndex;
        if(this.trickCount[opposingTeamIndex] === 0) score = 7;
    }
    if(this.trump.suitName == "No Trump") score *= 2;
    this.score[winningIndex] += (score + extras);
    this.gameView.updateScore();
};

Game.prototype.isWinningCard = function(card) {
    var winner = this.winningCard.card;
    if(!winner) return true; // no cards played yet

    if(card.suit == winner.suit) {
        return card.getRankOrder(this.trump.isHigh) > winner.getRankOrder(this.trump.isHigh);
    }
    else {
        return card.suit.name == this.trump.suitName;
    }
};