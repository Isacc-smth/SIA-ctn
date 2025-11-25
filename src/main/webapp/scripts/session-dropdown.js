/** 
 * Ajustes globales para el menú desplegable de sesión de usuario.
 *
 * @module SessionDropdown
 * */
(function () {
    /** 
     * Función para gestionar el menú desplegable de sesión de usuario.
     * */
    (function sessionDropdown() {
        const dropdown = document.getElementById('sessionDropdown');
        if (!dropdown) return;
        const button = document.getElementById('sessionButton');
        const menu = document.getElementById('sessionMenu');
        if (!button) return;

        function openMenu() {
            dropdown.classList.add('open');
            button.classList.add('open');
            button.setAttribute('aria-expanded', 'true');
        }
        function closeMenu() {
            dropdown.classList.remove('open');
            button.classList.remove('open');
            button.setAttribute('aria-expanded', 'false');
        }

        button.addEventListener('click', function (e) {
            e.stopPropagation();
            if (dropdown.classList.contains('open')) closeMenu();
            else openMenu();
        });

        document.addEventListener('click', function (e) {
            if (!dropdown.contains(e.target)) closeMenu();
        });

        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape') closeMenu();
        });

        if (menu) {
            menu.addEventListener('click', function (e) {
                const t = e.target;
                if (t.matches && t.matches('a')) closeMenu();
            });
        }
    })();
})();
