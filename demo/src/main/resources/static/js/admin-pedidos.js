document.addEventListener('DOMContentLoaded', () => {
    const selectFiltro = document.getElementById('filtroEstado');
    const tablaBody = document.getElementById('tablaPedidosBody');

    // Referencias del Modal
    const modal = document.getElementById('modalEditar');
    const modalNombre = document.getElementById('modalPedidoNombre');
    const selectNuevoEstado = document.getElementById('nuevoEstado');
    const btnCancelar = document.getElementById('btnCancelarModal');
    const btnGuardar = document.getElementById('btnGuardarModal');

    let filaSeleccionada = null;

    // 1. Cargar estado guardado desde localStorage
    function cargarEstadoGuardado() {
        const pedidosGuardados = JSON.parse(localStorage.getItem('ktaro_pedidos_estado'));
        if (!pedidosGuardados) return;

        const filas = tablaBody.querySelectorAll('tr');
        filas.forEach((fila, index) => {
            if (pedidosGuardados[index]) {
                const nuevoEstado = pedidosGuardados[index];
                actualizarFilaEstado(fila, nuevoEstado);
            }
        });
    }

    // 2. Guardar estado actual de la tabla en localStorage
    function guardarEstadoTabla() {
        const filas = tablaBody.querySelectorAll('tr');
        const estados = Array.from(filas).map(fila => fila.getAttribute('data-estado'));
        localStorage.setItem('ktaro_pedidos_estado', JSON.stringify(estados));
    }

    // 3. Helper para actualizar DOM de una fila
    function actualizarFilaEstado(fila, nuevoEstado) {
        fila.setAttribute('data-estado', nuevoEstado);
        const badge = fila.querySelector('.badge-status');
        badge.innerText = nuevoEstado;
        badge.className = 'badge-status';

        if (nuevoEstado === 'Aceptado') {
            badge.classList.add('badge-accepted');
        } else if (nuevoEstado === 'Listo') {
            badge.classList.add('badge-ready');
        } else if (nuevoEstado === 'En curso') {
            badge.classList.add('badge-in-progress');
        }
    }

    // 4. Lógica del Filtro por Estado
    selectFiltro.addEventListener('change', function () {
        const seleccion = this.value.trim().toLowerCase();
        const filas = tablaBody.querySelectorAll('tr');

        filas.forEach(fila => {
            const estadoFila = (fila.getAttribute('data-estado') || '').trim().toLowerCase();

            if (seleccion === 'todos' || estadoFila === seleccion) {
                fila.style.display = '';
            } else {
                fila.style.display = 'none';
            }
        });
    });

    // 5. Abrir Modal al pulsar "Editar"
    tablaBody.addEventListener('click', (e) => {
        if (e.target.classList.contains('btn-edit-order')) {
            e.preventDefault();
            filaSeleccionada = e.target.closest('tr');

            const nombrePedido = filaSeleccionada.querySelector('.order-name').innerText;
            const estadoActual = filaSeleccionada.getAttribute('data-estado');

            modalNombre.innerText = nombrePedido;
            selectNuevoEstado.value = estadoActual;

            modal.classList.add('active');
        }
    });

    // 6. Cerrar Modal
    btnCancelar.addEventListener('click', () => {
        modal.classList.remove('active');
        filaSeleccionada = null;
    });

    // 7. Guardar Cambios de Estado
    btnGuardar.addEventListener('click', () => {
        if (!filaSeleccionada) return;

        const nuevoEstado = selectNuevoEstado.value;
        actualizarFilaEstado(filaSeleccionada, nuevoEstado);
        guardarEstadoTabla();

        modal.classList.remove('active');
        filaSeleccionada = null;

        // Re-aplicar el filtro activo
        selectFiltro.dispatchEvent(new Event('change'));
    });

    // Ejecución inicial al cargar la vista
    cargarEstadoGuardado();
});