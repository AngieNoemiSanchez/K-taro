document.addEventListener('DOMContentLoaded', () => {
    const selectFiltro = document.getElementById('filtroEstado');
    const tablaBody = document.getElementById('tablaPedidosBody');

    const modal = document.getElementById('modalEditar');
    const form = document.getElementById('formEstado');
    const modalNombre = document.getElementById('modalPedidoNombre');
    const modalActual = document.getElementById('modalEstadoActual');
    const selectNuevoEstado = document.getElementById('nuevoEstado');
    const btnCancelar = document.getElementById('btnCancelarModal');

    if (selectFiltro) {
        selectFiltro.addEventListener('change', function () {
            const seleccion = this.value;
            tablaBody.querySelectorAll('tr[data-estado]').forEach(fila => {
                const coincide = seleccion === 'todos' || fila.dataset.estado === seleccion;
                fila.style.display = coincide ? '' : 'none';
            });
        });
    }

    tablaBody.addEventListener('click', (e) => {
        const boton = e.target.closest('.btn-edit-order');
        if (!boton) return;
        e.preventDefault();

        const { id, nombre, actual, siguiente, siguienteEtiqueta } = boton.dataset;

        form.action = form.dataset.base + id + '/estado';
        modalNombre.textContent = nombre;
        modalActual.textContent = actual;

        selectNuevoEstado.replaceChildren(new Option(siguienteEtiqueta, siguiente));
        selectNuevoEstado.value = siguiente;

        modal.classList.add('active');
    });

    const cerrarModal = () => modal.classList.remove('active');
    btnCancelar.addEventListener('click', cerrarModal);
    modal.addEventListener('click', (e) => { if (e.target === modal) cerrarModal(); });
    document.addEventListener('keydown', (e) => { if (e.key === 'Escape') cerrarModal(); });

    form.addEventListener('submit', () => {
        document.getElementById('btnGuardarModal').disabled = true;
    });
});