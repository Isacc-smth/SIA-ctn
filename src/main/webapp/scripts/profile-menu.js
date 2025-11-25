(function () {
    /**
     * Módulo de control de la página de perfil.
     * - Marca el formulario como "dirty" cuando hay cambios no guardados.
     * - Pregunta al usuario antes de abandonar la página si hay cambios.
     * - Marca la clase del formulario al pulsar el botón guardar.
     */
    // Encontrar el formulario principal. Ajustar selector si es necesario.
    const form = document.querySelector('form[action$="/ProfileServlet"]') || document.querySelector('form');
    let dirty = false;

    if (form) {
        // Marcar como dirty cuando cambian entradas dentro del formulario.
        form.addEventListener('input', () => { dirty = true; });
        form.addEventListener('change', () => { dirty = true; });

        // Cuando el usuario envía (guarda), limpiar el flag dirty.
        form.addEventListener('submit', () => { dirty = false; });
    }

    const backBtn = document.getElementById('backBtn');
    if (backBtn) {
        backBtn.addEventListener('click', function (e) {
            if (dirty) {
                // advertir y bloquear navegación si el usuario cancela
                const leave = confirm('Hay cambios sin guardar. ¿Deseas salir sin guardar?');
                if (!leave) {
                    e.preventDefault();
                }
            }
        });
    }

    // Evita cerrar/navegar fuera de la página si hay cambios sin guardar
    window.addEventListener('beforeunload', function (e) {
        if (dirty) {
            e.preventDefault();
            // NOTE: navegadores modernos ignoran el mensaje personalizado, devolver un valor no vacío es suficiente
            e.returnValue = '';
        }
    });

    // Guardar: proteger accesos al DOM (podrían no existir en algunas páginas)
    const saveBtn = document.getElementById('saveBtn');
    const profileForm = document.getElementById('profileForm');
    if (saveBtn) {
        saveBtn.addEventListener('click', function () {
            if (profileForm) profileForm.className = 'submitted';
        });
    }
})();
