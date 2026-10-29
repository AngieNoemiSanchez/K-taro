document.addEventListener("DOMContentLoaded", () => {
    const btnEditar = document.getElementById("btnEditarPerfil");
    const modal = document.getElementById("modalEditar");
    const btnCerrar = document.getElementById("btnCerrarModal");
    const btnCancelar = document.getElementById("btnCancelarModal");

    if (!modal || !btnEditar) return;

    const abrirModal = () => {
        modal.style.display = "flex";
    };

    const cerrarModal = () => {
        modal.style.display = "none";
    };

    btnEditar.addEventListener("click", abrirModal);
    
    if (btnCerrar) btnCerrar.addEventListener("click", cerrarModal);
    if (btnCancelar) btnCancelar.addEventListener("click", cerrarModal);

    modal.addEventListener("click", (e) => {
        if (e.target === modal) cerrarModal();
    });

    const hayErrores = document.querySelectorAll('.field-error').length > 0;
    if (hayErrores) {
        abrirModal();
    }
});