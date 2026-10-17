"use strict";
(() => {
  // frontend/ts/contacto.ts
  document.addEventListener("DOMContentLoaded", () => {
    const formulario = document.getElementById("formContacto");
    const estado = document.getElementById("contactoEstado");
    if (!formulario || !estado) return;
    formulario.addEventListener("submit", (evento) => {
      evento.preventDefault();
      if (!formulario.reportValidity()) return;
      const nombre = document.getElementById("contactoNombre").value.trim();
      const mensaje = document.getElementById("contactoMensaje").value.trim();
      if (!nombre || !mensaje) {
        estado.textContent = "Completa los campos obligatorios.";
        return;
      }
      estado.textContent = "Formulario validado correctamente. El env\xEDo real a\xFAn no est\xE1 habilitado.";
      formulario.reset();
    });
  });
})();
