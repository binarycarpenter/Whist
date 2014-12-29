function Player(index, name) {
    this.index = index;
    this.name = name;
    this.cards = [];
    this.allHtml = w.id("player" + this.index);
    this.nameDiv = w.id("playerName" + this.index);
    this.nameDiv.innerHTML = this.name;
    this.bidDiv = w.id("playerBid" + this.index);
};

Player.prototype.addCard = function(card) {
    this.cards.push(card);
};

Player.prototype.removeCard = function(card) {
    this.cards.splice(this.cards.indexOf(card), 1);
};

Player.prototype.playCard = function(card) {
    this.removeCard(card);
    this.playedCard = card;
};

Player.prototype.sortCards = function() {
    this.cards.sort(function(a, b) {
        if(a.suit.order === b.suit.order) return a.rank.highOrder - b.rank.highOrder;
        return a.suit.order - b.suit.order;
    });
};

Player.prototype.hasSuit = function(suit) {
    for(var i = 0; i < this.cards.length; i++) {
        if(this.cards[i].suit == suit) return true;
    }
    return false;
};