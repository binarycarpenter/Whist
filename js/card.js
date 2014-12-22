Rank = [{name:"Ace"},
        {name:"Two"},
        {name:"Three"},
        {name:"Four"},
        {name:"Five"},
        {name:"Six"},
        {name:"Seven"},
        {name:"Eight"},
        {name:"Nine"},
        {name:"Ten"},
        {name:"Jack"},
        {name:"Queen"},
        {name:"King"}];

Suit = ["Diamonds", "Clubs", "Hearts", "Spades"];

function Card(rank, suit) {
    this.rank = rank;
    this.suit = suit;
    this.owner = null;
};

Card.prototype.addToKitty = function() { this.owner = "Kitty"};

Card.prototype.addToPlayer = function(player) { this.owner = player};