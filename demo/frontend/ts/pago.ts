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

  // Si estamos en la pantalla de COMPRA EXITOSA, muestra el total guardado
  if (finalTotal) {
    const lastTotal = localStorage.getItem(LAST_TOTAL_KEY) ?? "s/ 0.00";
    finalTotal.textContent = lastTotal;
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
  renderResumenPago();

  const btnContinuarPago = document.querySelector<HTMLButtonElement>("#btnContinuarPago");
  btnContinuarPago?.addEventListener("click", (e) => {
    e.preventDefault();

    const formPago = document.querySelector<HTMLFormElement>("#formPago");
    if (formPago && !formPago.checkValidity()) {
      formPago.reportValidity();
      return;
    }

    // Guardar el total pagado para mostrarlo en la vista de exito
    const cart = readCart();
    const totalPrice = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);
    localStorage.setItem(LAST_TOTAL_KEY, `s/ ${totalPrice.toFixed(2)}`);

    // Vaciar productos
    localStorage.removeItem(CART_STORAGE_KEY);
    window.location.href = "/compra-exitosa";
  });
});