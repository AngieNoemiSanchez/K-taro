import Swal from "sweetalert2";

interface CartItem {
  id: string;
  name: string;
  price: number;
  quantity: number;
}

const CART_STORAGE_KEY = "ktaro-cart";
const LAST_TOTAL_KEY = "ktaro-last-total";

function readCart(): CartItem[] {
  try {
    const storedCart = localStorage.getItem(CART_STORAGE_KEY);
    if (!storedCart) return [];
    return JSON.parse(storedCart) as CartItem[];
  } catch {
    return [];
  }
}

function renderResumenPago(): void {
  const cart = readCart();
  const pagoItemsList = document.querySelector<HTMLElement>("#pagoItemsList");
  const pagoSubtotal = document.querySelector<HTMLElement>("#pagoSubtotal");
  const pagoIgv = document.querySelector<HTMLElement>("#pagoIgv");
  const pagoTotal = document.querySelector<HTMLElement>("#pagoTotal");
  const finalTotal = document.querySelector<HTMLElement>("#finalTotal");


if (finalTotal) {
  const lastTotal = localStorage.getItem(LAST_TOTAL_KEY) ?? "s/ 0.00";
  finalTotal.textContent = lastTotal;

  const finalUsuario = document.querySelector<HTMLElement>("#finalUsuario");

  if (finalUsuario) {
    finalUsuario.textContent =
      localStorage.getItem("ktaro-last-usuario") ?? "No especificado";
  }
  const finalCorreo = document.querySelector<HTMLElement>("#finalCorreo");

if (finalCorreo) {
  finalCorreo.textContent =
    localStorage.getItem("ktaro-last-correo") ?? "No especificado";
}

  return;
}

  if (!pagoItemsList) return;

  const totalPrice = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

  pagoItemsList.replaceChildren(...cart.map((item) => {
    const div = document.createElement("div");
    div.className = "resumen-item";

    const nameSpan = document.createElement("span");
    nameSpan.textContent = item.name;

    const priceSpan = document.createElement("span");
    priceSpan.textContent = `s/ ${(item.price * item.quantity).toFixed(2)}`;

    div.append(nameSpan, priceSpan);
    return div;
  }));

  const subtotal = totalPrice / 1.18;
  const igv = totalPrice - subtotal;

  if (pagoSubtotal) pagoSubtotal.textContent = `s/ ${subtotal.toFixed(2)}`;
  if (pagoIgv) pagoIgv.textContent = `s/ ${igv.toFixed(2)}`;
  if (pagoTotal) pagoTotal.textContent = `s/ ${totalPrice.toFixed(2)}`;
}

document.addEventListener("DOMContentLoaded", () => {

  const numeroTarjeta = document.querySelector<HTMLInputElement>("#numeroTarjeta");

  numeroTarjeta?.addEventListener("input", () => {
    numeroTarjeta.value = numeroTarjeta.value.replace(/\D/g, "").slice(0, 16);
  });
  const cvvTarjeta = document.querySelector<HTMLInputElement>("#cvvTarjeta");

cvvTarjeta?.addEventListener("input", () => {
  cvvTarjeta.value = cvvTarjeta.value.replace(/\D/g, "").slice(0, 4);
});const fechaVencimiento = document.querySelector<HTMLInputElement>("#fechaVencimiento");

fechaVencimiento?.addEventListener("input", () => {
  const numeros = fechaVencimiento.value.replace(/\D/g, "").slice(0, 4);

  fechaVencimiento.value =
    numeros.length > 2
      ? `${numeros.slice(0, 2)}/${numeros.slice(2)}`
      : numeros;
});

  renderResumenPago();

  const btnContinuarPago = document.querySelector<HTMLButtonElement>("#btnContinuarPago");
  btnContinuarPago?.addEventListener("click", async (e) => {
    e.preventDefault();

          const fechaInput = document.querySelector<HTMLInputElement>("#fechaVencimiento");

      if (fechaInput) {
        const [mes, anio] = fechaInput.value.split("/").map(Number);

        const hoy = new Date();
        const anioActual = hoy.getFullYear() % 100;
        const mesActual = hoy.getMonth() + 1;

        if (anio < anioActual || (anio === anioActual && mes < mesActual)) {
          Swal.fire({
            icon: "error",
            title: "Fecha vencida",
            text: "La fecha de vencimiento debe ser posterior al mes actual."
          });
          return;
        }
      }

    const cart = readCart();
    if (cart.length === 0) {
      await Swal.fire({ icon: "warning", title: "Carrito vacío", text: "Agrega productos antes de pagar." });
      return;
    }
    try {
      const response = await fetch("/api/pedidos/confirmar", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(cart.map((item) => ({ id: item.id, quantity: item.quantity }))),
      });
      if (!response.ok) {
        const body = (await response.json().catch(() => ({}))) as { mensaje?: string };
        await Swal.fire({
          icon: "error",
          title: "No se pudo completar la compra",
          text: body.mensaje ?? "Revisa tu carrito e inténtalo nuevamente.",
        });
        return;
      }
    } catch {
      await Swal.fire({ icon: "error", title: "Sin conexión", text: "No se pudo confirmar la compra. Inténtalo nuevamente." });
      return;
    }


    const totalPrice = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);
    localStorage.setItem(LAST_TOTAL_KEY, `s/ ${totalPrice.toFixed(2)}`);
    const nombreTitular = document.querySelector<HTMLInputElement>(
  '#formPago input[placeholder="Nombre del titular"]'
);

localStorage.setItem(
  "ktaro-last-usuario",
  nombreTitular?.value.trim() || "No especificado"
);
const correoComprador = document.querySelector<HTMLInputElement>(
  "#correoComprador"
);

localStorage.setItem(
  "ktaro-last-correo",
  correoComprador?.value.trim() || "No especificado"
);


    localStorage.removeItem(CART_STORAGE_KEY);
    window.location.href = "/compra-exitosa";
  });
});