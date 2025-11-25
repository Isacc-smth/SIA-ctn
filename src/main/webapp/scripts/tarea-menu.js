
/**
 * @fileoverview Manejo de la página de gestión de tareas (guardado/retroceso/eliminado).
 *
 * Funcionalidades:
 * - Marca el formulario como "dirty" si hay cambios y previene la navegación accidental.
 * - Añade la clase `submitted` al formulario al pulsar guardar (si existe).
 * - Provee `confirmDelete()` para confirmar la eliminación y marcar la acción.
 */
(function initTareaMenu() {
    // Buscar el formulario principal de tareas. Ajustar selector si la app cambia.
    const form = document.querySelector('form[action$="/TareaServlet"]') || document.querySelector('form');
    let dirty = false;

    if (form) {
        // Marcar como dirty cuando el usuario modifica inputs dentro del formulario.
        form.addEventListener('input', () => { dirty = true; });
        form.addEventListener('change', () => { dirty = true; });

        // Al enviar el formulario (guardar), limpiar el flag dirty.
        form.addEventListener('submit', () => { dirty = false; });
    }

    const backBtn = document.getElementById('backBtn');
    if (backBtn) {
        backBtn.addEventListener('click', function (e) {
            if (dirty) {
                // Advertir y bloquear navegación si el usuario cancela.
                const leave = confirm('Hay cambios sin guardar. ¿Deseas salir sin guardar?');
                if (!leave) {
                    e.preventDefault();
                }
            }
        });
    }

    // Evitar cerrar/navegar fuera de la página si hay cambios sin guardar.
    window.addEventListener('beforeunload', function (e) {
        if (dirty) {
            e.preventDefault();
            // Los navegadores modernos ignoran el mensaje personalizado; devolver un valor no vacío es suficiente.
            e.returnValue = '';
        }
    });

    // Añadir clase 'submitted' al formulario al hacer click en el botón guardar.
    const saveBtn = document.getElementById('saveBtn');
    const tareaForm = document.getElementById('tareaForm');
    if (saveBtn) {
        saveBtn.addEventListener('click', function () {
            if (tareaForm) tareaForm.className = 'submitted';
        });
    }

})();

/**
 * Confirma la eliminación de la tarea. Si el usuario acepta, marca el campo
 * `_action_input` con el valor 'delete' para que el servlet lo procese.
 *
 * @returns {boolean} true para permitir la acción (submit), false para cancelarla.
 */
function confirmDelete() {
    if (!confirm('¿Desea eliminar esta tarea? Esta acción no se puede deshacer.')) {
        return false;
    }
    // set hidden action value to 'delete' so servlet knows to delete
    var act = document.getElementById('_action_input');
    if (act) act.value = 'delete';
    return true;
}
