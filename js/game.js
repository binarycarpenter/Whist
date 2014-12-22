function Game() {
    this.deck = new Deck();
    this.numPlayers = 4;
    this.numKittyCards = 4;
    this.winningScore = 21;
    this.dealerIndex = Math.floor(Math.random() * this.numPlayers);
    this.players = [];
    for(var i = 0; i < this.numPlayers; i++) {
        this.players[i] = new Player(i);
    }
    this.playGame();
};

Game.prototype.playGame = function() {
    this.score = [0, 0];
    while(!this.gameOver()) {
        this.playRound();
    }
};

Game.prototype.gameOver = function() {
    return this.score[0] > this.winningScore || this.score[1] > this.winningScore;
}

Game.prototype.playRound = function() {
    this.deal();
    this.bid();
    this.takeKittyAndCallTrump();
    this.playHand();
};

Game.prototype.deal = function() {
    this.deck.shuffle();
    var playerIndex = this.dealerIndex + 1;
    var numInKitty = 0;
    for(var i = 0; i < this.deck.length; i++) {
        if(playerIndex >= this.numPlayers) {
            if(numInKitty < this.numKittyCards) {
                numInKitty++;
                this.deck[i].addToKitty();
                continue;
            }
            else {
                playerIndex = 0;
            }
        }

        var player = this.players[playerIndex];
        this.deck[i].addToPlayer(player);
        player.addCard(this.deck[i]);
    }
};