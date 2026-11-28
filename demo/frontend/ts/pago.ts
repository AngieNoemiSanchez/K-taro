import Swal from "sweetalert2";

interface CartItem {
  id: string;
  name: string;
  price: number;
  quantity: number;
}

interface TarjetaGuardada {
  id: number;
  tipo: string;
  numeroEnmascarado: string;
}

interface TarjetasResponse {
  tarjetas: TarjetaGuardada[];
  puedeAgregar: boolean;
}

interface TipoTarjeta {
  nombre: string;
  longitud: number;
  cvv: number;
}

type Modo = "registrada" | "nueva";

let cart: CartItem[] = [];
let tarjetasGuardadas: TarjetaGuardada[] = [];
let puedeAgregar = true;
let modo: Modo = "nueva";

function readCart(): CartItem[] {
  return cart;
}

function csrfHeaders(): Record<string, string> {
  const token = document.querySelector<HTMLMetaElement>('meta[name="_csrf"]')?.content;
  const header = document.querySelector<HTMLMetaElement>('meta[name="_csrf_header"]')?.content;
  return token && header ? { [header]: token } : {};
}

function detectarTipo(numero: string): TipoTarjeta | null {
  if (/^(34|37)/.test(numero)) return { nombre: "American Express", longitud: 15, cvv: 4 };
  if (/^(30[0-5]|3095|36|38|39)/.test(numero)) return { nombre: "Diners Club", longitud: 14, cvv: 3 };
  if (/^4/.test(numero)) return { nombre: "Visa", longitud: 16, cvv: 3 };
  if (/^5[1-5]/.test(numero)) return { nombre: "Mastercard", longitud: 16, cvv: 3 };
  if (numero.length >= 4) {
    const prefijo = parseInt(numero.slice(0, 4), 10);
    if (prefijo >= 2221 && prefijo <= 2720) return { nombre: "Mastercard", longitud: 16, cvv: 3 };
  }
  return null;
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

async function loadTarjetas(): Promise<void> {
  try {
    const response = await fetch("/api/pedidos/tarjetas", { headers: { Accept: "application/json" } });
    if (!response.ok || response.redirected) {
      tarjetasGuardadas = [];
      puedeAgregar = true;
      return;
    }
    const data = (await response.json()) as TarjetasResponse;
    tarjetasGuardadas = data.tarjetas;
    puedeAgregar = data.puedeAgregar;
  } catch {
    tarjetasGuardadas = [];
    puedeAgregar = true;
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

function aplicarModo(): void {
  const panelRegistradas = document.querySelector<HTMLElement>("#panelRegistradas");
  const boxNueva = document.querySelector<HTMLElement>("#boxNueva");
  if (panelRegistradas) panelRegistradas.hidden = modo !== "registrada";
  if (boxNueva) boxNueva.hidden = modo !== "nueva";
}

function renderMetodos(): void {
  const radioRegistrada = document.querySelector<HTMLInputElement>("#radioRegistrada");
  const radioNueva = document.querySelector<HTMLInputElement>("#radioNueva");
  const opcionRegistrada = document.querySelector<HTMLElement>("#opcionRegistrada");
  const opcionNueva = document.querySelector<HTMLElement>("#opcionNueva");
  const subRegistrada = document.querySelector<HTMLElement>("#subRegistrada");
  const avisoNueva = document.querySelector<HTMLElement>("#avisoNueva");
  const panelRegistradas = document.querySelector<HTMLElement>("#panelRegistradas");

  if (!radioRegistrada || !radioNueva || !panelRegistradas) return;

  const hayRegistradas = tarjetasGuardadas.length > 0;

  panelRegistradas.replaceChildren(...tarjetasGuardadas.map((t, i) => {
    const label = document.createElement("label");
    label.className = "tarjeta-guardada";

    const radio = document.createElement("input");
    radio.type = "radio";
    radio.name = "tarjetaGuardada";
    radio.value = String(t.id);
    radio.checked = i === 0;

    const tipo = document.createElement("strong");
    tipo.textContent = t.tipo;

    const numero = document.createElement("span");
    numero.textContent = t.numeroEnmascarado;

    label.append(radio, tipo, numero);
    return label;
  }));

  radioRegistrada.disabled = !hayRegistradas;
  opcionRegistrada?.classList.toggle("deshabilitada", !hayRegistradas);
  if (subRegistrada) subRegistrada.hidden = hayRegistradas;

  radioNueva.disabled = !puedeAgregar;
  opcionNueva?.classList.toggle("deshabilitada", !puedeAgregar);
  if (avisoNueva) avisoNueva.hidden = puedeAgregar;

  modo = hayRegistradas ? "registrada" : "nueva";
  radioRegistrada.checked = modo === "registrada";
  radioNueva.checked = modo === "nueva";
  aplicarModo();

  radioRegistrada.addEventListener("change", () => {
    if (radioRegistrada.checked) {
      modo = "registrada";
      aplicarModo();
    }
  });

  radioNueva.addEventListener("change", () => {
    if (radioNueva.checked) {
      modo = "nueva";
      aplicarModo();
    }
  });
}

function configurarFormulario(): void {
  const numeroTarjeta = document.querySelector<HTMLInputElement>("#numeroTarjeta");
  const cvvTarjeta = document.querySelector<HTMLInputElement>("#cvvTarjeta");
  const fechaVencimiento = document.querySelector<HTMLInputElement>("#fechaVencimiento");
  const badge = document.querySelector<HTMLElement>("#tipoTarjeta");

  numeroTarjeta?.addEventListener("input", () => {
    let valor = numeroTarjeta.value.replace(/\D/g, "").slice(0, 16);
    const tipo = detectarTipo(valor);
    if (tipo) {
      valor = valor.slice(0, tipo.longitud);
      if (cvvTarjeta) {
        cvvTarjeta.maxLength = tipo.cvv;
        cvvTarjeta.value = cvvTarjeta.value.slice(0, tipo.cvv);
      }
      if (badge) badge.textContent = tipo.nombre;
    } else {
      if (cvvTarjeta) cvvTarjeta.maxLength = 4;
      if (badge) badge.textContent = "";
    }
    numeroTarjeta.value = valor;
  });

  cvvTarjeta?.addEventListener("input", () => {
    cvvTarjeta.value = cvvTarjeta.value.replace(/\D/g, "").slice(0, cvvTarjeta.maxLength);
  });

  fechaVencimiento?.addEventListener("input", () => {
    const numeros = fechaVencimiento.value.replace(/\D/g, "").slice(0, 4);
    fechaVencimiento.value =
      numeros.length > 2 ? `${numeros.slice(0, 2)}/${numeros.slice(2)}` : numeros;
  });
}

interface TarjetaPayload {
  tarjetaId?: number;
  numeroTarjeta?: string;
  cvv?: string;
  vencimiento?: string;
}

async function construirTarjeta(): Promise<TarjetaPayload | null> {
  if (modo === "registrada") {
    const seleccion = document.querySelector<HTMLInputElement>('input[name="tarjetaGuardada"]:checked');
    if (!seleccion) {
      await Swal.fire({
        icon: "warning",
        title: "Selecciona una tarjeta",
        text: "Elige una de tus tarjetas registradas.",
      });
      return null;
    }
    return { tarjetaId: Number(seleccion.value) };
  }

  const numero = document.querySelector<HTMLInputElement>("#numeroTarjeta")?.value ?? "";
  const cvv = document.querySelector<HTMLInputElement>("#cvvTarjeta")?.value ?? "";
  const vencimiento = document.querySelector<HTMLInputElement>("#fechaVencimiento")?.value ?? "";

  const tipo = detectarTipo(numero);
  if (!tipo) {
    await Swal.fire({
      icon: "warning",
      title: "Tarjeta no reconocida",
      text: "Solo se aceptan tarjetas Visa, Mastercard, American Express o Diners Club.",
    });
    return null;
  }

  if (
    numero.length !== tipo.longitud ||
    cvv.length !== tipo.cvv ||
    !/^(0[1-9]|1[0-2])\/\d{2}$/.test(vencimiento)
  ) {
    await Swal.fire({
      icon: "warning",
      title: "Datos incompletos",
      text: `Completa el número (${tipo.longitud} dígitos), el vencimiento (MM/AA) y el CVV (${tipo.cvv} dígitos).`,
    });
    return null;
  }

  const [mes, anio] = vencimiento.split("/").map(Number);
  const hoy = new Date();
  const anioActual = hoy.getFullYear() % 100;
  const mesActual = hoy.getMonth() + 1;

  if (anio < anioActual || (anio === anioActual && mes < mesActual)) {
    await Swal.fire({
      icon: "error",
      title: "Fecha vencida",
      text: "La fecha de vencimiento debe ser posterior al mes actual.",
    });
    return null;
  }

  return { numeroTarjeta: numero, cvv, vencimiento };
}

document.addEventListener("DOMContentLoaded", async () => {
  configurarFormulario();

  document.querySelector<HTMLButtonElement>("#cartToggle")?.addEventListener("click", (e) => {
    e.preventDefault();
    window.location.href = "/carrito";
  });

  await Promise.all([loadCart(), loadTarjetas()]);
  renderCartCount();
  renderResumenPago();
  renderMetodos();

  const btnContinuarPago = document.querySelector<HTMLButtonElement>("#btnContinuarPago");
  btnContinuarPago?.addEventListener("click", async (e) => {
    e.preventDefault();

    const tarjeta = await construirTarjeta();
    if (!tarjeta) return;

    await loadCart();
    const items = readCart();
    if (items.length === 0) {
      await Swal.fire({ icon: "warning", title: "Carrito vacío", text: "Agrega productos antes de pagar." });
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
          tarjeta,
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