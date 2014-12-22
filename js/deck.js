function Deck() {
    this.cards = [];
    for(var i = 0; i < Suit.length; i++) {
        for(var j = 0; j < Rank.length; j++) {
            this.cards.push(new Card(Rank[j], Suit[i]));
        }
    }
    this.length = this.cards.length;
};

Deck.prototype.shuffle = function() {
    this.cards.shuffle();
}