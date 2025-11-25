/**
 * @fileoverview Configura y controla el diálogo de envío por email.
 *
 * Este archivo gestiona el comportamiento del diálogo modal con id "email-form".
 * Cuando el usuario pulsa un botón con la clase `.email-button` se abre el diálogo.
 * El botón con id `dialogClose` cierra el diálogo.
 *
 * Notas:
 * - El elemento con clase `email-button` puede no existir en todas las vistas.
 * - Se validan las referencias al DOM para evitar errores cuando elementos faltan.
 */

/**
 * Inicializa los handlers del formulario de email.
 * Busca los elementos relevantes, añade listeners y protege contra referencias nulas.
 *
 * @returns {void}
 */
function initEmailForm() {
    const dialog = document.getElementById('email-form');
    const closeBtn = document.getElementById('dialogClose');

    // Query puede devolver NodeList vacío; comprobamos longitud antes de usarlo.
    const showBtns = document.querySelectorAll('.email-button');

    if (showBtns && showBtns.length) {
        showBtns.forEach((showBtn) => {
            showBtn.addEventListener('click', () => {
                // Si no hay diálogo definido, no hacemos nada.
                if (dialog && typeof dialog.showModal === 'function') dialog.showModal();
            });
        });
    }

    // Close button: proteger por si no existe en la página actual.
    if (closeBtn) {
        closeBtn.addEventListener('click', () => {
            if (dialog && typeof dialog.close === 'function') dialog.close();
        });
    }
}

// Ejecutar cuando el DOM esté listo.
document.addEventListener('DOMContentLoaded', initEmailForm);
