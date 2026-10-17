
document.addEventListener("DOMContentLoaded", () => {
    const formulario = document.getElementById("formContacto") as HTMLFormElement | null;
    const estado = document.getElementById("contactoEstado") as HTMLElement | null;

    if (!formulario || !estado) return;

    formulario.addEventListener("submit", (evento: Event) => {
        evento.preventDefault();

        // Comprobar que los campos obligatorios estén completos
        if (!formulario.reportValidity()) return;

        const nombre = (
            document.getElementById("contactoNombre") as HTMLInputElement
        ).value.trim();

        const mensaje = (
            document.getElementById("contactoMensaje") as HTMLTextAreaElement
        ).value.trim();

        if (!nombre || !mensaje) {
            estado.textContent = "Completa los campos obligatorios.";
            return;
        }

        // Confirmación de prueba: todavía no se envía al servidor
        estado.textContent =
            "Formulario validado correctamente. ";

        formulario.reset();
    });
});
