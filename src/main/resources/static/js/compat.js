/* Lets markup written for Bootstrap 5 (data-bs-*) drive the Bootstrap 4 components we ship. */
(function () {
    document.querySelectorAll('[data-bs-toggle],[data-bs-target],[data-bs-dismiss],[data-bs-dismiss]').forEach(function (el) {
        ['toggle', 'target', 'dismiss', 'placement', 'content'].forEach(function (k) {
            var v = el.getAttribute('data-bs-' + k);
            if (v !== null) { el.setAttribute('data-' + k, v); }
        });
    });
})();

/* Bootstrap modals must live directly under <body>; inside a page section or grid row they end up
   behind the dark overlay (unclickable) and distort the layout. */
(function () {
    document.querySelectorAll('.modal').forEach(function (m) { document.body.appendChild(m); });
})();
