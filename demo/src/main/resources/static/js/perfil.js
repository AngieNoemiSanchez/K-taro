"use strict";
(() => {
  // frontend/ts/perfil.ts
  document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("formPerfil");
    const editar = document.getElementById("editarPerfil");
    const guardar = document.getElementById("guardarPerfil");
    const mensaje = document.getElementById("mensajePerfil");
    if (!form || !editar || !guardar) return;
    const campos = ["nombre", "apellido", "dni", "telefono", "fecha", "correo"];
    const input = (id) => document.getElementById(id);
    function mostrarPerfil(perfil) {
      const nombreCompleto = `${perfil.nombre} ${perfil.apellido}`.trim();
      const asignar = (id, valor) => {
        const elemento = document.getElementById(id);
        if (elemento) elemento.textContent = valor || "\u2014";
      };
      asignar("nombreMostrado", nombreCompleto || "Mi cuenta");
      asignar("correoMostrado", perfil.correo || "Completa tu informaci\xF3n");
      asignar("resumenNombre", perfil.nombre);
      asignar("resumenApellido", perfil.apellido);
      asignar("resumenDni", perfil.dni);
      asignar("resumenTelefono", perfil.telefono);
      asignar("resumenFecha", perfil.fecha);
      const iniciales = `${perfil.nombre.charAt(0)}${perfil.apellido.charAt(0)}`.toUpperCase();
      asignar("avatarIniciales", iniciales || "?");
    }
    const guardado = localStorage.getItem("ktaro_perfil");
    if (guardado) {
      try {
        const perfil = JSON.parse(guardado);
        campos.forEach((id) => {
          input(id).value = perfil[id] || "";
        });
        mostrarPerfil(perfil);
      } catch {
        localStorage.removeItem("ktaro_perfil");
      }
    }
    editar.addEventListener("click", () => {
      campos.forEach((id) => input(id).disabled = false);
      guardar.hidden = false;
      mensaje.textContent = "";
      input("nombre").focus();
    });
    form.addEventListener("submit", (evento) => {
      evento.preventDefault();
      if (!form.reportValidity()) return;
      const perfil = {
        nombre: input("nombre").value.trim(),
        apellido: input("apellido").value.trim(),
        dni: input("dni").value.trim(),
        telefono: input("telefono").value.trim(),
        fecha: input("fecha").value,
        correo: input("correo").value.trim()
      };
      localStorage.setItem("ktaro_perfil", JSON.stringify(perfil));
      mostrarPerfil(perfil);
      campos.forEach((id) => input(id).disabled = true);
      guardar.hidden = true;
      mensaje.textContent = "\xA1Perfil actualizado correctamente!";
    });
  });
})();
