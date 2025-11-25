/**
 * @fileoverview Validaciones y confirmaciones para el formulario de tareas.
 *
 * Este módulo evita que el usuario cambie el total de puntos de una tarea
 * sin ser consciente de que se borrarán las calificaciones existentes.
 *
 * Behavior:
 * - Cuando se pulsa el botón guardar, si el total cambió respecto a
 *   `originalTotal`, se solicita confirmación y, si acepta, se marca
 *   `clearGrades` para que el servidor borre las notas existentes.
 * - No se cambia el comportamiento del envío por defecto del formulario;
 *   sólo se previene el envío cuando el usuario cancela la confirmación.
 */
(function initTareaForm() {
    const form = document.getElementById('tareaForm');
    const saveBtn = document.getElementById('saveBtn');
    if (!form || !saveBtn) return;

    /**
     * Parseo seguro de entero en base 10. Devuelve `null` si no es un número finito.
     * @param {string|number} v
     * @returns {number|null}
     */
    function parseIntSafe(v) {
        const n = parseInt(v, 10);
        return Number.isFinite(n) ? n : null;
    }

    saveBtn.addEventListener('click', function (ev) {
        // only run when saving (not when delete)
        // `_action_input` se puede establecer por el atributo onclick del botón.
        const actionInput = document.getElementById('_action_input');
        const action = actionInput ? actionInput.value : 'save';

        if (action !== 'save') return;

        // Obtener elementos de forma defensiva (pueden faltar en algunas vistas).
        const origEl = document.getElementById('originalTotal');
        const totalEl = form.querySelector('input[name="total"]');
        const clearGradesEl = document.getElementById('clearGrades');

        const orig = origEl ? parseIntSafe(origEl.value) : null;
        const current = totalEl ? parseIntSafe(totalEl.value) : null;

        // Si estamos editando una tarea existente (orig !== null) y el total cambió
        if (orig !== null && current !== null && orig !== current) {
            const confirmed = confirm(
                'Ha cambiado el total de puntos de ' + orig + ' a ' + current + '.\n' +
                'Se borrarán todas las calificaciones existentes para esta tarea. ¿Desea continuar?'
            );
            if (!confirmed) {
                // El usuario canceló -> prevenir envío del formulario.
                ev.preventDefault();
                return;
            }
            // Usuario aceptó -> marcar bandera para que el servidor borre las notas.
            if (clearGradesEl) clearGradesEl.value = 'true';
        } else {
            // Asegurar que la bandera esté en 'false' si existe.
            if (clearGradesEl) clearGradesEl.value = 'false';
        }
        // Permitir el envío normal del formulario (el servidor procesará los campos).
    });
})();
