
interface PerfilUsuario {
    nombre: string;
    apellido: string;
    dni: string;
    telefono: string;
    fecha: string;
    correo: string;
}

document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("formPerfil") as HTMLFormElement;
    const editar = document.getElementById("editarPerfil") as HTMLButtonElement;
    const guardar = document.getElementById("guardarPerfil") as HTMLButtonElement;
    const mensaje = document.getElementById("mensajePerfil") as HTMLElement;

    if (!form || !editar || !guardar) return;

    const campos = ["nombre", "apellido", "dni", "telefono", "fecha", "correo"];

    const input = (id: string) =>
        document.getElementById(id) as HTMLInputElement;

    function mostrarPerfil(perfil: PerfilUsuario) {
        const nombreCompleto = `${perfil.nombre} ${perfil.apellido}`.trim();

        const asignar = (id: string, valor: string) => {
            const elemento = document.getElementById(id);
            if (elemento) elemento.textContent = valor || "—";
        };

        asignar("nombreMostrado", nombreCompleto || "Mi cuenta");
        asignar("correoMostrado", perfil.correo || "Completa tu información");
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
            const perfil = JSON.parse(guardado) as PerfilUsuario;

            campos.forEach(id => {
                input(id).value = perfil[id as keyof PerfilUsuario] || "";
            });

            mostrarPerfil(perfil);
        } catch {
            localStorage.removeItem("ktaro_perfil");
        }
    }

    editar.addEventListener("click", () => {
        campos.forEach(id => input(id).disabled = false);
        guardar.hidden = false;
        mensaje.textContent = "";
        input("nombre").focus();
    });

    form.addEventListener("submit", (evento) => {
        evento.preventDefault();

        if (!form.reportValidity()) return;

        const perfil: PerfilUsuario = {
            nombre: input("nombre").value.trim(),
            apellido: input("apellido").value.trim(),
            dni: input("dni").value.trim(),
            telefono: input("telefono").value.trim(),
            fecha: input("fecha").value,
            correo: input("correo").value.trim()
        };

        localStorage.setItem("ktaro_perfil", JSON.stringify(perfil));
        mostrarPerfil(perfil);

        campos.forEach(id => input(id).disabled = true);
        guardar.hidden = true;
        mensaje.textContent = "¡Perfil actualizado correctamente!";
    });
});
