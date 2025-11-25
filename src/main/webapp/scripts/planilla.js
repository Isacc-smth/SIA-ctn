/** 
 * @fileoverview Rastrear cambios para evitar que el usuario salga sin guardarlos
 * */
(function () {

        /**
         * Función auxiliar para ejecutar un callback cuando el DOM esté listo.
         *
         * @param {Function} callback - Función a ejecutar cuando el DOM esté listo.
         * @returns {void}
         */
        function ready(callback) {
            if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', callback);
            else callback();
        }

    ready(function init() {
        // global flag
        window.planillaDirty = false;

            /**
             * Verifica si el elemento objetivo debe ser ignorado por el tracker de "dirty".
             * Utiliza `data-ignore-dirty` en el elemento o en alguno de sus ancestros.
             *
             * @param {HTMLElement|null} target - Elemento objetivo del evento (puede ser null).
             * @returns {boolean} true si debe ignorarse, false en caso contrario.
             */
            function isIgnoreTarget(target) {
                if (!target) return false;
                if (target.dataset && target.dataset.ignoreDirty !== undefined) return true;
                if (target.closest && target.closest('[data-ignore-dirty]')) return true;
                return false;
            }

        // --------------- form dirty tracking ---------------
        const form = document.querySelector('form[action$="/PlanillaServlet"]') || document.querySelector('form');
        if (form) {
            form.addEventListener('input', function (e) {
                if (isIgnoreTarget(e.target)) return;
                window.planillaDirty = true;
            });

            form.addEventListener('change', function (e) {
                if (isIgnoreTarget(e.target)) return;
                window.planillaDirty = true;
            });

            form.addEventListener('submit', function () { window.planillaDirty = false; });
        }

        // back button guard
        const backBtn = document.getElementById('backBtn');
        if (backBtn) {
            backBtn.addEventListener('click', function (e) {
                if (window.planillaDirty) {
                    const leave = confirm('Hay cambios sin guardar. ¿Deseas salir sin guardar?');
                    if (!leave) e.preventDefault();
                }
            });
        }

        // Evitar cerrar/navegar fuera de la página con cambios sin guardar
        window.addEventListener('beforeunload', function (e) {
            if (window.planillaDirty) {
                e.preventDefault();
                e.returnValue = '';
            }
        });


            // --------------- flash messages ---------------
            /**
             * Inicializa el comportamiento automático de los mensajes flash dentro
             * de la página actual (ocultado tras timeout, pausa en hover, y cierre manual).
             *
             * Encapsulado como IIFE para mantener el scope local.
             */
            (function flashHandler() {
                const nodes = document.querySelectorAll('.flash, .flash-errors');
                if (!nodes || !nodes.length) return;

                nodes.forEach(function (el) {
                    let timeoutMs = parseInt(el.dataset.timeout, 10);
                    if (!Number.isFinite(timeoutMs)) timeoutMs = 4000;

                    let timer = setTimeout(() => {
                        if (el.classList.contains('flash')) el.classList.add('flash--hide');
                        else el.classList.add('flash-errors--hide');
                    }, timeoutMs);

                    el.addEventListener('transitionend', function (ev) {
                        if (ev.propertyName === 'opacity' || ev.propertyName === 'max-height') {
                            try { el.remove(); } catch (e) { /* ignore DOM removal errors */ }
                        }
                    });

                    el.addEventListener('mouseenter', () => clearTimeout(timer));

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

        // --------------- download button guard (may not exist) ---------------
        const downloadBtn = document.getElementById('downloadBtn');
        if (downloadBtn) {
            downloadBtn.addEventListener('click', function (e) {
                if (window.planillaDirty) {
                    const cont = confirm('Hay cambios sin guardar. Es recomendable guardar antes de descargar. ¿Desea descargar de todas formas?');
                    if (!cont) e.preventDefault();
                }
            });
        }

            /**
             * Fix para evitar reajustes de tamaño de la tabla de alumnos al activar el checkbox.
             * Guarda la preferencia en localStorage bajo `ctn.freezeAlumnos`.
             */
            (function freezeToggle() {
                const checkbox = document.getElementById('freezeCheckbox');
                const tableResp = document.querySelector('.table-responsive');

                /**
                 * Aplica o quita la clase que congela el tamaño de la tabla.
                 * @param {boolean} enabled
                 */
                function setFreeze(enabled) {
                    if (!tableResp) return;
                    tableResp.classList.toggle('freeze-alumnos', !!enabled);
                }

                try {
                    const saved = localStorage.getItem('ctn.freezeAlumnos');
                    if (saved === '1' && checkbox) {
                        checkbox.checked = true;
                        setFreeze(true);
                    }
                } catch (e) { /* localStorage may be disabled; ignore */ }

                if (checkbox) {
                    checkbox.addEventListener('change', function () {
                        setFreeze(this.checked);
                        try {
                            localStorage.setItem('ctn.freezeAlumnos', this.checked ? '1' : '0');
                        } catch (e) { /* ignore */ }
                    });
                }
            })();

            /**
             * Permite arrastrar horizontalmente la tabla dentro de `.table-responsive`.
             * Ignora targets interactivos (inputs, botones, enlaces) para no interferir
             * con su comportamiento nativo.
             */
            (function horizontalDrag() {
                const wrap = document.querySelector('.table-responsive');
                if (!wrap) return;

                let isDown = false;
                let startX = 0;
                let scrollLeft = 0;

                function isInteractiveTarget(target) {
                    return !!(target && target.closest && target.closest('input, textarea, select, button, a, label'));
                }

                wrap.addEventListener('pointerdown', function (e) {
                    if (isInteractiveTarget(e.target)) return;

                    isDown = true;
                    startX = e.clientX;
                    scrollLeft = wrap.scrollLeft;
                    wrap.classList.add('dragging');

                    if (e.pointerType !== 'mouse') {
                        try { e.target.setPointerCapture(e.pointerId); } catch (err) { /* ignore */ }
                    }
                });

                wrap.addEventListener('pointermove', function (e) {
                    if (!isDown) return;
                    const dx = e.clientX - startX;
                    wrap.scrollLeft = scrollLeft - dx;
                });

                function stopDrag(e) {
                    if (!isDown) return;
                    isDown = false;
                    wrap.classList.remove('dragging');
                    try { e.target && e.target.releasePointerCapture && e.target.releasePointerCapture(e.pointerId); } catch (err) { /* ignore */ }
                }

                wrap.addEventListener('pointerup', stopDrag);
                wrap.addEventListener('pointercancel', stopDrag);
                wrap.addEventListener('pointerleave', function (e) {
                    if (isDown && e.pointerType === 'mouse') stopDrag(e);
                });

                window.addEventListener('blur', function () { isDown = false; wrap.classList.remove('dragging'); });
            })();
    }); // ready/init
})();
