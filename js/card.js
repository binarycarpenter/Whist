Rank = [{name:"Ace", highOrder:13, lowOrder:13},
        {name:"King", highOrder:12, lowOrder:1 },
        {name:"Queen", highOrder:11, lowOrder:2},
        {name:"Jack", highOrder:10, lowOrder:3},
        {name:"Ten", highOrder:9, lowOrder:4},
        {name:"Nine", highOrder:8, lowOrder:5},
        {name:"Eight", highOrder:7, lowOrder:6},
        {name:"Seven", highOrder:6, lowOrder:7},
        {name:"Six", highOrder:5, lowOrder:8},
        {name:"Five", highOrder:4, lowOrder:9},
        {name:"Four", highOrder:3, lowOrder:10},
        {name:"Three", highOrder:2, lowOrder:11},
        {name:"Two", highOrder:1, lowOrder:12}];

// the order field is used for display, where we want red and black to alternate, but the array must have the suits in
// the order below so that the index during deck creation matches the image file names
Suit = [{name:"Clubs", order: 1},
        {name:"Spades", order:3},
        {name:"Hearts", order:4},
        {name:"Diamonds", order:2}];

function Card(rank, suit, index) {
    this.rank = rank;
    this.suit = suit;
    this.index = index;
    this.isHidden = true;
    this.isFaceUp = false;
    this.isHorizontal = false;
    this.name = this.rank.name + " of " + this.suit.name;

    this.img = document.createElement("img");
    this.img.src = "../img/vertical-cards/" + this.index + ".png"; // pre-load image
    this.img.style.position = "absolute";
    this.img.setAttribute("name", this.name);
    this.setImageSrc();
    w.id("table").appendChild(this.img);
};

Card.prototype.setImageSrc = function() {
    var dir = "vertical-cards"
    var imgSrc = this.index + ".png";
    var width = 72;
    var height = 96;

    if(!this.isFaceUp) {
        imgSrc = "red_back.png";
        width--; // width of back of card image is 1 pixel less for some reason...
    }

    if(this.isHorizontal) {
        // switch width and height
        var tmp = width;
        width = height;
        height = tmp;
        dir = "horizontal-cards";
    }

    this.img.src = "../img/" + dir + "/" + imgSrc;
    this.img.style.height = height + "px";
    this.img.style.width = width + "px";
    if(this.isHidden) w.hide(this.img);
    else w.show(this.img);
};

Card.prototype.showFaceUp = function(zIndex) {
    this.isFaceUp = true;
    this.isHidden = false;
    this.setImageSrc();
    this.img.style.zIndex = zIndex;
};

Card.prototype.hide = function() {
    this.isHidden = true;
    this.isFaceUp = false;
    this.setImageSrc();
};

Card.prototype.setCoords = function(left, bottom) {
    this.img.style.left = left + "px";
    this.img.style.bottom = bottom + "px";
    this.img.style.top = "";
    this.img.style.right = "";
};

Card.prototype.raise = function() {
    this.img.style.bottom = "15px";
};

Card.prototype.lower = function() {
    this.img.style.bottom = "0px";
};

Card.prototype.addDiscardOnclick = function(game) {
    var self = this;
    this.img.onclick = function() { game.discardSelect(self); };
};

Card.prototype.addPlayOnclick = function(game) {
    var self = this;
    this.img.onclick = function() { game.playCard(self); };
};

Card.prototype.getRankOrder = function(isHigh) {
    return isHigh? this.rank.highOrder : this.rank.lowOrder;
};


