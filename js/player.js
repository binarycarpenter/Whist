function Player(index) {
    this.index = index;
    this.cards = [];
    this.cardsBySuit = {};
    for(var i = 0; i < Suit.length; i++) {
        this.cardsBySuit[Suit[i]] = [];
    }
};

Player.prototype.addCard = function(card) {
    this.cards.push(card);
    this.cardsBySuit[card.suit].push(card);
};