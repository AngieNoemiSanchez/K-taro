import Swal from "sweetalert2";

interface CartItem {
  id: string;
  name: string;
  price: number;
  quantity: number;
}

let cart: CartItem[] = [];

function readCart(): CartItem[] {
  return cart;
}

function csrfHeaders(): Record<string, string> {
  const token = document.querySelector<HTMLMetaElement>('meta[name="_csrf"]')?.content;
  const header = document.querySelector<HTMLMetaElement>('meta[name="_csrf_header"]')?.content;
  return token && header ? { [header]: token } : {};
}

async function loadCart(): Promise<void> {
  try {
    const response = await fetch("/api/carrito", { headers: { Accept: "application/json" } });
    if (!response.ok || response.redirected) {
      cart = [];
      return;
    }
    const data = (await response.json()) as { items: CartItem[] };
    cart = data.items;
  } catch {
    cart = [];
  }
}

function renderCartCount(): void {
  const cartCount = document.querySelector<HTMLElement>("#cartCount");
  if (cartCount) {
    cartCount.textContent = String(readCart().reduce((sum, item) => sum + item.quantity, 0));
  }
}

function renderResumenPago(): void {
  const items = readCart();
  const pagoItemsList = document.querySelector<HTMLElement>("#pagoItemsList");
  const pagoSubtotal = document.querySelector<HTMLElement>("#pagoSubtotal");
  const pagoIgv = document.querySelector<HTMLElement>("#pagoIgv");
  const pagoTotal = document.querySelector<HTMLElement>("#pagoTotal");

  if (!pagoItemsList) return;

  const totalPrice = items.reduce((sum, item) => sum + item.price * item.quantity, 0);

  pagoItemsList.replaceChildren(...items.map((item) => {
    const div = document.createElement("div");
    div.className = "resumen-item";

    const nameSpan = document.createElement("span");
    nameSpan.textContent = `${item.name} x${item.quantity}`;

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

document.addEventListener("DOMContentLoaded", async () => {
  const numeroTarjeta = document.querySelector<HTMLInputElement>("#numeroTarjeta");
  numeroTarjeta?.addEventListener("input", () => {
    numeroTarjeta.value = numeroTarjeta.value.replace(/\D/g, "").slice(0, 16);
  });

  const cvvTarjeta = document.querySelector<HTMLInputElement>("#cvvTarjeta");
  cvvTarjeta?.addEventListener("input", () => {
    cvvTarjeta.value = cvvTarjeta.value.replace(/\D/g, "").slice(0, 3);
  });

  const fechaVencimiento = document.querySelector<HTMLInputElement>("#fechaVencimiento");
  fechaVencimiento?.addEventListener("input", () => {
    const numeros = fechaVencimiento.value.replace(/\D/g, "").slice(0, 4);
    fechaVencimiento.value =
      numeros.length > 2 ? `${numeros.slice(0, 2)}/${numeros.slice(2)}` : numeros;
  });

  document.querySelector<HTMLButtonElement>("#cartToggle")?.addEventListener("click", (e) => {
    e.preventDefault();
    window.location.href = "/carrito";
  });

  await loadCart();
  renderCartCount();
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
          text: "La fecha de vencimiento debe ser posterior al mes actual.",
        });
        return;
      }
    }

    await loadCart();
    const items = readCart();
    if (items.length === 0) {
      await Swal.fire({ icon: "warning", title: "Carrito vacío", text: "Agrega productos antes de pagar." });
      return;
    }

    const titular = document.querySelector<HTMLInputElement>("#titularTarjeta")?.value.trim() ?? "";
    const numero = document.querySelector<HTMLInputElement>("#numeroTarjeta")?.value ?? "";
    const cvv = document.querySelector<HTMLInputElement>("#cvvTarjeta")?.value ?? "";
    const vencimiento = fechaInput?.value ?? "";

    if (!titular || numero.length !== 16 || cvv.length !== 3 || !/^(0[1-9]|1[0-2])\/\d{2}$/.test(vencimiento)) {
      await Swal.fire({
        icon: "warning",
        title: "Datos incompletos",
        text: "Completa el titular, el número (16 dígitos), el vencimiento (MM/AA) y el CVV (3 dígitos).",
      });
      return;
    }

    const headers: Record<string, string> = { "Content-Type": "application/json", ...csrfHeaders() };

    let pedidoId: number | undefined;

    try {
      const response = await fetch("/api/pedidos/confirmar", {
        method: "POST",
        headers,
        body: JSON.stringify({
          items: items.map((item) => ({ id: item.id, quantity: item.quantity })),
          tarjeta: { numeroTarjeta: numero, cvv, titular, vencimiento },
        }),
      });

      if (response.redirected) {
        window.location.href = "/cliente/login";
        return;
      }

      if (!response.ok) {
        const body = (await response.json().catch(() => ({}))) as { mensaje?: string };
        await Swal.fire({
          icon: "error",
          title: "No se pudo completar la compra",
          text: body.mensaje ?? "Revisa tu carrito e inténtalo nuevamente.",
        });
        return;
      }

      const resultado = (await response.json().catch(() => ({}))) as { pedidoId?: number };
      pedidoId = resultado.pedidoId;
    } catch {
      await Swal.fire({ icon: "error", title: "Sin conexión", text: "No se pudo confirmar la compra. Inténtalo nuevamente." });
      return;
    }

    await fetch("/api/carrito", { method: "DELETE", headers: csrfHeaders() }).catch(() => undefined);
    window.location.replace(pedidoId ? `/compra-exitosa/${pedidoId}` : "/carrito");
  });
});