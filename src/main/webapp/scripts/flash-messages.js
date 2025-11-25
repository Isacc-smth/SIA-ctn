/**
 * @fileoverview Manejo de mensajes flash.
 *
 * Detecta nodos con las clases `.flash` y `.flash-errors` y automatiza su
 * desaparición tras un timeout (configurable vía `data-timeout`). También
 * añade comportamiento para pausar el temporizador al pasar el ratón y
 * manejar el botón de cierre.
 *
 * Behaviour summary:
 * - data-timeout: número de milisegundos antes de iniciar la ocultación.
 * - hover: pausa el temporizador.
 * - close button: oculta inmediatamente el flash.
 */
(function initFlashMessages() {
    const nodes = document.querySelectorAll('.flash, .flash-errors');
    if (!nodes || !nodes.length) return;

    nodes.forEach(function (el) {
        // dataset.timeout puede venir como string; parseInt seguro con base 10.
        let timeoutMs = parseInt(el.dataset.timeout, 10);
        if (!Number.isFinite(timeoutMs)) timeoutMs = 4000;

        // start timer
        let timer = setTimeout(() => {
            if (el.classList.contains('flash')) el.classList.add('flash--hide');
            else el.classList.add('flash-errors--hide');
        }, timeoutMs);

        // cleanup after transition
        el.addEventListener('transitionend', function (ev) {
            if (ev.propertyName === 'opacity' || ev.propertyName === 'max-height') {
                try { el.remove(); } catch (e) { /* defensive: ignore removal errors */ }
            }
        });

        // pause on hover (optional nicety)
        el.addEventListener('mouseenter', () => clearTimeout(timer));

        // close button
        const closeBtn = el.querySelector('.flash-close');
        if (closeBtn) {
            closeBtn.addEventListener('click', function (e) {
                e.preventDefault();
                clearTimeout(timer);
                if (el.classList.contains('flash')) el.classList.add('flash--hide');
                else el.classList.add('flash-errors--hide');
            });
        }
    });
})();
