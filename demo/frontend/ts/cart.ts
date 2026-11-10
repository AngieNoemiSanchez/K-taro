import Swal from "sweetalert2";

interface CartItem {
  id: string;
  name: string;
  price: number;
  quantity: number;
  category?: string;
  stock?: number;
}

interface CartApiResponse {
  items: CartItem[];
  total: number;
}

interface PedidoPendiente {
  id: number;
  codigo: string;
}

const CART_PURPLE = "#3929b5";

let isCartInitialized = false;

let cart: CartItem[] = [];

class SilentError extends Error {}

function readCart(): CartItem[] {
  return cart;
}

function formatPrice(value: number): string {
  return `S/ ${value.toFixed(2)}`;
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
    const data = (await response.json()) as CartApiResponse;
    cart = data.items;
  } catch (error) {
    console.error("No se pudo cargar el carrito.", error);
    cart = [];
  }
}

async function sendCart(method: string, body?: unknown, path = "/api/carrito/items"): Promise<void> {
  const response = await fetch(path, {
    method,
    headers: { "Content-Type": "application/json", Accept: "application/json", ...csrfHeaders() },
    body: body === undefined ? undefined : JSON.stringify(body),
  });

  if (response.redirected) {
    window.location.href = response.url;
    throw new SilentError("Sesión requerida");
  }

  const data = (await response.json()) as CartApiResponse & { mensaje?: string };
  if (!response.ok) throw new Error(data.mensaje ?? "No se pudo actualizar el carrito.");
  cart = data.items;
}

function showError(title: string, error: unknown): void {
  if (error instanceof SilentError) return;
  console.error(title, error);
  void Swal.fire({
    icon: "error",
    title,
    text: error instanceof Error ? error.message : undefined,
    confirmButtonColor: CART_PURPLE,
  });
}

async function fetchAvailableUnits(productId: string): Promise<number> {
  const response = await fetch(`/api/stock?ids=${encodeURIComponent(productId)}`);
  if (!response.ok) throw new Error("No se pudo consultar el stock.");
  const data = (await response.json()) as Record<string, number>;
  return data[productId] ?? 0;
}

async function ensureStock(productId: string, name: string, wanted: number): Promise<boolean> {
  const available = await fetchAvailableUnits(productId);
  if (wanted <= available) return true;
  await Swal.fire({
    icon: "warning",
    title: available === 0 ? "Producto no disponible" : "Stock insuficiente",
    text: available === 0
      ? `${name} está agotado o ya no está disponible.`
      : `Solo quedan ${available} unidades de ${name}.`,
    confirmButtonColor: CART_PURPLE,
  });
  return false;
}

async function showPendingOrders(): Promise<void> {
  try {
    const response = await fetch("/api/pedidos/pendientes", { headers: { Accept: "application/json" } });
    if (response.redirected) {
      window.location.href = response.url;
      return;
    }
    if (!response.ok) throw new Error("No se pudieron cargar tus pedidos.");

    const pedidos = (await response.json()) as PedidoPendiente[];
    if (pedidos.length === 0) {
      await Swal.fire({
        icon: "info",
        title: "Sin pedidos pendientes",
        text: "No tienes pedidos por recoger.",
        confirmButtonColor: CART_PURPLE,
      });
      return;
    }

    const lista = document.createElement("div");
    lista.className = "pedidos-lista";
    pedidos.forEach((pedido) => {
      const enlace = document.createElement("a");
      enlace.className = "pedido-link";
      enlace.href = `/compra-exitosa/${pedido.id}`;
      enlace.textContent = pedido.codigo;
      lista.append(enlace);
    });

    await Swal.fire({
      title: "Pedidos pendientes",
      html: lista,
      showConfirmButton: false,
      showCloseButton: true,
    });
  } catch (error) {
    showError("No se pudieron cargar los pedidos", error);
  }
}

export function initializeCart(): void {
  if (isCartInitialized) return;
  isCartInitialized = true;

  const cartCount = document.querySelector<HTMLElement>("#cartCount");
  const cartPanel = document.querySelector<HTMLElement>("#cartPanel");
  const cartToggle = document.querySelector<HTMLButtonElement>("#cartToggle");
  const cartItems = document.querySelector<HTMLUListElement>("#cartItems");
  const cartEmpty = document.querySelector<HTMLElement>("#cartEmpty");
  const cartTotal = document.querySelector<HTMLElement>("#cartTotal");

  const cartTableBody = document.querySelector<HTMLTableSectionElement>("#cartTableBody");
  const itemsCountLabel = document.querySelector<HTMLElement>("#itemsCountLabel");
  const cartSubtotal = document.querySelector<HTMLElement>("#cartSubtotal");
  const cartIgv = document.querySelector<HTMLElement>("#cartIgv");
  const cartTotalFull = document.querySelector<HTMLElement>("#cartTotalFull");

  const btnContinuarCompra = document.querySelector<HTMLButtonElement>("#btnContinuarCompra");
  btnContinuarCompra?.addEventListener("click", () => {
    if (readCart().length === 0) {
      Swal.fire({
        icon: "warning",
        title: "Carrito vacío",
        text: "Agrega productos antes de continuar.",
        confirmButtonColor: CART_PURPLE,
      });
      return;
    }
    window.location.href = "/pago";
  });

  document.querySelector<HTMLButtonElement>("#btnPedidosPendientes")
    ?.addEventListener("click", () => void showPendingOrders());

  async function updateCartQuantity(productId: string, change: number): Promise<void> {
    try {
      const item = readCart().find((product) => product.id === productId);
      if (!item) return;

      const newQuantity = item.quantity + change;
      if (change > 0 && !(await ensureStock(productId, item.name, newQuantity))) return;

      await sendCart("PUT", { id: productId, quantity: Math.max(0, newQuantity) });
      renderCart();
    } catch (error) {
      showError("No se pudo actualizar el carrito", error);
    }
  }

  function renderCart(): void {
    const items = readCart();
    const totalQuantity = items.reduce((sum, item) => sum + item.quantity, 0);
    const totalPrice = items.reduce((sum, item) => sum + item.price * item.quantity, 0);

    if (cartCount) cartCount.textContent = String(totalQuantity);
    if (cartTotal) cartTotal.textContent = formatPrice(totalPrice);
    if (cartEmpty) cartEmpty.hidden = items.length > 0;
    if (itemsCountLabel) itemsCountLabel.textContent = `${totalQuantity} productos`;

    if (cartItems) {
      cartItems.replaceChildren(...items.map((item) => {
        const row = document.createElement("li");
        row.className = "cart-item";

        const details = document.createElement("span");
        details.className = "cart-item-name";
        details.textContent = item.name;

        const quantity = document.createElement("div");
        quantity.className = "cart-quantity";

        const minusButton = document.createElement("button");
        minusButton.type = "button";
        minusButton.textContent = "−";
        minusButton.setAttribute("aria-label", `Disminuir cantidad de ${item.name}`);

        const quantityText = document.createElement("span");
        quantityText.textContent = String(item.quantity);

        const plusButton = document.createElement("button");
        plusButton.type = "button";
        plusButton.textContent = "+";
        plusButton.setAttribute("aria-label", `Aumentar cantidad de ${item.name}`);

        minusButton.addEventListener("click", () => updateCartQuantity(item.id, -1));
        plusButton.addEventListener("click", () => updateCartQuantity(item.id, 1));

        quantity.append(minusButton, quantityText, plusButton);
        details.append(quantity);

        const price = document.createElement("span");
        price.className = "cart-item-price";
        price.textContent = formatPrice(item.price * item.quantity);

        row.append(details, price);
        return row;
      }));
    }

    if (cartTableBody) {
      cartTableBody.replaceChildren(...items.map((item) => {
        const tr = document.createElement("tr");

        const tdDetails = document.createElement("td");
        tdDetails.style.width = "40%";
        const title = document.createElement("span");
        title.className = "cart-item-title";
        title.textContent = item.name;
        const category = document.createElement("span");
        category.className = "cart-item-category";
        category.textContent = item.category ?? "Snack importado";
        tdDetails.append(title, category);

        const tdQty = document.createElement("td");
        tdQty.style.width = "20%";
        const qtyContainer = document.createElement("div");
        qtyContainer.className = "qty-controls";

        const btnPlus = document.createElement("button");
        btnPlus.className = "qty-btn";
        btnPlus.textContent = "+";
        btnPlus.addEventListener("click", () => updateCartQuantity(item.id, 1));

        const qtyNum = document.createElement("span");
        qtyNum.className = "qty-number";
        qtyNum.textContent = String(item.quantity);

        const btnMinus = document.createElement("button");
        btnMinus.className = "qty-btn";
        btnMinus.textContent = "-";
        btnMinus.addEventListener("click", () => updateCartQuantity(item.id, -1));

        qtyContainer.append(btnPlus, qtyNum, btnMinus);
        tdQty.append(qtyContainer);

        const tdPrice = document.createElement("td");
        tdPrice.style.width = "25%";
        tdPrice.className = "cart-item-price";
        tdPrice.textContent = formatPrice(item.price * item.quantity);

        const tdAction = document.createElement("td");
        tdAction.style.width = "15%";
        tdAction.style.textAlign = "right";
        const btnDelete = document.createElement("button");
        btnDelete.className = "cart-delete-btn";
        btnDelete.textContent = "Eliminar";
        btnDelete.addEventListener("click", () => updateCartQuantity(item.id, -item.quantity));
        tdAction.append(btnDelete);

        tr.append(tdDetails, tdQty, tdPrice, tdAction);
        return tr;
      }));

      const subtotal = totalPrice / 1.18;
      const igv = totalPrice - subtotal;

      if (cartSubtotal) cartSubtotal.textContent = `s/ ${subtotal.toFixed(2)}`;
      if (cartIgv) cartIgv.textContent = `s/ ${igv.toFixed(2)}`;
      if (cartTotalFull) cartTotalFull.textContent = `s/ ${totalPrice.toFixed(2)}`;
    }
  }

  function setCartOpen(open: boolean): void {
    if (!cartPanel || !cartToggle) return;
    cartPanel.hidden = !open;
    cartToggle.setAttribute("aria-expanded", String(open));
  }

  document.querySelectorAll<HTMLButtonElement>(".add-to-cart").forEach((button) => {
    button.addEventListener("click", async () => {
      const { productId, productName, productPrice, productStock } = button.dataset;
      const price = Number(productPrice);
      const quantity = Number(button.dataset.quantity ?? "1");
      const stock = productStock === undefined || productStock === "" ? undefined : Number(productStock);
      if (!productId || !productName || !Number.isFinite(price) || price < 0
        || !Number.isInteger(quantity) || quantity < 1
        || (stock !== undefined && !Number.isFinite(stock))) {
        console.error("No se pudo agregar el producto: sus datos son inválidos.");
        await Swal.fire({ icon: "error", title: "No se pudo agregar", text: "Revisa los datos del producto e inténtalo nuevamente." });
        return;
      }

      try {
        const alreadyInCart = readCart().find((item) => item.id === productId)?.quantity ?? 0;
        if (stock !== undefined && alreadyInCart + quantity > stock) {
          await Swal.fire({
            icon: "warning",
            title: "Stock insuficiente",
            text: stock === 0
              ? `${productName} está agotado.`
              : `Solo hay ${stock} unidades disponibles y ya tienes ${alreadyInCart} en el carrito.`,
            confirmButtonColor: CART_PURPLE,
          });
          return;
        }

        await sendCart("POST", { id: productId, quantity });
        renderCart();
        await Swal.fire({
          icon: "success",
          title: "¡Agregado al carrito!",
          text: quantity === 1 ? productName : `${productName} (cantidad: ${quantity})`,
          timer: 1500,
          showConfirmButton: false,
        });
      } catch (error) {
        showError("No se pudo agregar al carrito", error);
      }
    });
  });

  cartToggle?.addEventListener("click", (e) => {
    e.preventDefault();
    window.location.href = "/carrito";
  });

  document.querySelector<HTMLButtonElement>("#cartClose")?.addEventListener("click", () => setCartOpen(false));

  document.querySelector<HTMLButtonElement>("#clearCart")?.addEventListener("click", async () => {
    if (readCart().length === 0) return;
    const result = await Swal.fire({
      icon: "question",
      title: "¿Vaciar el carrito?",
      text: "Se quitarán todos los productos guardados.",
      showCancelButton: true,
      confirmButtonText: "Vaciar",
      cancelButtonText: "Conservar",
      confirmButtonColor: CART_PURPLE,
    });
    if (!result.isConfirmed) return;

    try {
      await sendCart("DELETE", undefined, "/api/carrito");
      renderCart();
    } catch (error) {
      showError("No se pudo vaciar", error);
    }
  });

  void loadCart().then(renderCart);
}

if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", () => initializeCart());
} else {
  initializeCart();
}