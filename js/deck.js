function Deck() {
    this.cards = [];
    var index = 1;
    for(var i = 0; i < Rank.length; i++) {
        for(var j = 0; j < Suit.length; j++) {
            this.cards.push(new Card(Rank[i], Suit[j], index++));
        }
    }
    this.length = this.cards.length;
};

// Fisher-Yates (aka Knuth) Shuffle
Deck.prototype.shuffle = function() {
    var currentIndex = this.cards.length, tmp, randomIndex ;

    // While there remain elements to shuffle...
    while (0 !== currentIndex) {

        // Pick a remaining element...
        randomIndex = Math.floor(Math.random() * currentIndex);
        currentIndex -= 1;

        // And swap it with the current element.
        tmp = this.cards[currentIndex];
        this.cards[currentIndex] = this.cards[randomIndex];
        this.cards[randomIndex] = tmp;
    }
};