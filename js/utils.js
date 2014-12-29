w = {
    show: function(el) {
        el.style.display = el.tagName.toLowerCase() == "span"? "inline" : "block";
    },

    hide: function(el) { el.style.display = "none"; },

    id : function(idName) { return document.getElementById(idName); }
};