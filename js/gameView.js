function GameView(game) {
    this.game = game;
    this.cardHandWidth = 22;
    this.cardFullWidth = 72;
    this.bidDiv = w.id("bid");
    this.callTrumpDiv = w.id("callTrump");
    this.trumpDoneDiv = w.id("trumpDone");
    this.discardDiv = w.id("discard");
    this.discardDoneDiv = w.id("discardDone");
    this.kittyTakerDiv = w.id("kittyTaker");
    this.winningBidDiv = w.id("winningBid");
    this.trumpDiv = w.id("trump");
    this.scoreDivs = [w.id("score0"), w.id("score1")];
    this.trickDivs = [w.id("tricks0"), w.id("tricks1")];
    this.messageDiv = w.id("message");
    this.playerInfoCoords = {"bottom":{"left":"48%", "right":"", "top":"", "bottom":"110px"},
                             "left"  :{"left":"20px", "right":"", "top":"48%", "bottom":""},
                             "top"   :{"left":"48%", "right":"", "top":"15px", "bottom":""},
                             "right" :{"left":"", "right":"20px", "top":"48%", "bottom":""}};

    this.trickCoords = {"bottom":{"left":"47%", "right":"", "top":"45%", "bottom":""},
                        "left"  :{"left":"41%", "right":"", "top":"35%", "bottom":""},
                        "top"   :{"left":"47%", "right":"", "top":"22%", "bottom":""},
                        "right" :{"left":"53%", "right":"", "top":"35%", "bottom":""}};
    this.directions = ["bottom", "left", "top", "right"];

    this.setDealerChip();
};

GameView.prototype.setDealerChip = function() {
    w.id("player" + this.game.dealerIndex).appendChild(w.id("dealerChip"));
};

GameView.prototype.showHand = function() {
    this.player.sortCards();
    w.hide(this.bidDiv);
    var left = this.getLeftOffset(this.player.cards);
    var bottom = 0;
    for(var i = 0; i < this.player.cards.length; i++) {
        var card = this.player.cards[i];
        card.setCoords(left, bottom);
        card.showFaceUp(i);
        left += this.cardHandWidth;
    }
};

GameView.prototype.getLeftOffset = function(cards) {
    return this.getLeftOffsetFromWidth(((cards.length - 1) * this.cardHandWidth) + this.cardFullWidth);
};

GameView.prototype.getLeftOffsetFromWidth = function(width) {
    var screenWidth = window.screen.width;
    var extraSpace = screenWidth - width;
    if(extraSpace <= 0) return 0;
    return extraSpace / 2;
};

GameView.prototype.showBid = function() {
    var isDealer = this.player.index === this.game.dealerIndex;
    for(var bid = 3; bid <= 7; bid++) {
        var bidEl = w.id("bid" + bid);
        if(bid < this.game.winningBid.bid || (!isDealer && bid === this.game.winningBid.bid)) {
            w.hide(bidEl);
        }
        else {
            w.show(bidEl);
        }
    }
    w.show(this.bidDiv);
};

GameView.prototype.showMadeBid = function(bid) {
    this.player.bidDiv.innerHTML = "Bid: " + bid;
};

GameView.prototype.switchToPlayer = function(player) {
    if(this.player) {
        for(var i = 0; i < this.player.cards.length; i++) {
            this.player.cards[i].hide();
        }
    }
    this.player = player;
    var currentPlayer = this.player;
    for(i = 0; i < this.directions.length; i++) {
        var direction = this.directions[i];
        this.moveElementToCoords(currentPlayer.allHtml, this.playerInfoCoords[direction]);
        if(currentPlayer.playedCard) {
            this.moveElementToCoords(currentPlayer.playedCard.img, this.trickCoords[direction]);
        }
        currentPlayer = this.game.nextPlayer(currentPlayer);
    }
    this.showHand();
};

GameView.prototype.moveElementToCoords = function(el, coords) {
    for(var i = 0; i < this.directions.length; i++) {
        var direction = this.directions[i];
        el.style[direction] = coords[direction];
    }
};

GameView.prototype.callTrump = function() {
    for(var i = 0; i < this.game.players.length; i++) {
        w.hide(w.id("playerBid" + this.game.players[i].index));
    }
    w.hide(this.trumpDoneDiv);
    w.show(this.callTrumpDiv);
};

GameView.prototype.showTrumpDone = function() {
    w.show(this.trumpDoneDiv);
};

GameView.prototype.discard = function() {
    w.show(this.discardDiv);
    for(var i = 0; i < this.player.cards.length; i++) {
        this.player.cards[i].addDiscardOnclick(this.game);
    }
};

GameView.prototype.removeCard = function(card) {
    card.hide();
    this.player.removeCard(card);
    this.showHand();
};

GameView.prototype.playCardOnclick = function() {
    for(var i = 0; i < this.player.cards.length; i++) {
        this.player.cards[i].addPlayOnclick(this.game);
    }
};

GameView.prototype.validatePlay = function(card) {
    var ledCard = this.game.currentTrick[0];
    if(!ledCard) return true; // no cards yet, so anything is valid

    if(card.suit != ledCard.suit && this.player.hasSuit(ledCard.suit)) {
        this.writeMessage("You must follow suit");
        return false;
    }
    else {
        this.hideMessage();
        return true;
    }
};

GameView.prototype.hideMessage = function() {
    w.hide(this.messageDiv);
};

GameView.prototype.writeMessage = function(msg) {
    this.messageDiv.innerHTML = msg;
    w.show(this.messageDiv);
};

GameView.prototype.updateTrickCount = function() {
    this.trickDivs[0].innerHTML = this.game.trickCount[0];
    this.trickDivs[1].innerHTML = this.game.trickCount[1];
};

GameView.prototype.updateScore = function() {
    this.scoreDivs[0].innerHTML = this.game.score[0];
    this.scoreDivs[1].innerHTML = this.game.score[1];
};

GameView.prototype.moveCardIntoPlay = function(card) {
    this.moveElementToCoords(card.img, this.trickCoords.bottom);
    this.player.playCard(card);
};