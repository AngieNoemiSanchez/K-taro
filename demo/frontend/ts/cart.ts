import Swal from "sweetalert2";

interface CartItem {
  id: string;
  name: string;
  price: number;
  quantity: number;
  category?: string;
}

const CART_STORAGE_KEY = "ktaro-cart";
const CART_PURPLE = "#3929b5";

let isCartInitialized = false;

function readCart(): CartItem[] {
  try {
    const storedCart = localStorage.getItem(CART_STORAGE_KEY);
    if (!storedCart) return [];

    const parsed: unknown = JSON.parse(storedCart);
    if (!Array.isArray(parsed)) throw new TypeError("El carrito guardado no es una lista.");

    return parsed.filter((item: unknown): item is CartItem => {
      if (typeof item !== "object" || item === null) return false;
      const entry = item as Record<string, unknown>;
      return typeof entry.id === "string"
        && typeof entry.name === "string"
        && typeof entry.price === "number"
        && Number.isFinite(entry.price)
        && typeof entry.quantity === "number"
        && Number.isInteger(entry.quantity)
        && entry.quantity > 0;
    });
  } catch (error) {
    console.error("No se pudo leer el carrito guardado.", error);
    try {
      localStorage.removeItem(CART_STORAGE_KEY);
    } catch (storageError) {
      console.error("No se pudo limpiar el carrito guardado.", storageError);
    }
    return [];
  }
}

function saveCart(cart: CartItem[]): void {
  localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(cart));
}

function formatPrice(value: number): string {
  return `S/ ${value.toFixed(2)}`;
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

  // Elementos de la vista completa /carrito
  const cartTableBody = document.querySelector<HTMLTableSectionElement>("#cartTableBody");
  const itemsCountLabel = document.querySelector<HTMLElement>("#itemsCountLabel");
  const cartSubtotal = document.querySelector<HTMLElement>("#cartSubtotal");
  const cartIgv = document.querySelector<HTMLElement>("#cartIgv");
  const cartTotalFull = document.querySelector<HTMLElement>("#cartTotalFull");

  const btnContinuarCompra = document.querySelector<HTMLButtonElement>("#btnContinuarCompra");
  btnContinuarCompra?.addEventListener("click", () => {
    const cart = readCart();
    if (cart.length === 0) {
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

  function updateCartQuantity(productId: string, change: number): void {
    try {
      const cart = readCart();
      const item = cart.find((product) => product.id === productId);

      if (!item) return;

      item.quantity += change;

      if (item.quantity <= 0) {
        const index = cart.findIndex((product) => product.id === productId);
        cart.splice(index, 1);
      }

      saveCart(cart);
      renderCart();
    } catch (error) {
      console.error("Error al actualizar la cantidad:", error);
      void Swal.fire({
        icon: "error",
        title: "No se pudo actualizar el carrito",
      });
    }
  }

  function renderCart(): void {
    const cart = readCart();
    const totalQuantity = cart.reduce((sum, item) => sum + item.quantity, 0);
    const totalPrice = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);

    if (cartCount) cartCount.textContent = String(totalQuantity);
    if (cartTotal) cartTotal.textContent = formatPrice(totalPrice);
    if (cartEmpty) cartEmpty.hidden = cart.length > 0;
    if (itemsCountLabel) itemsCountLabel.textContent = `${totalQuantity} productos`;

    // Renderizar panel lateral flotante
    if (cartItems) {
      cartItems.replaceChildren(...cart.map((item) => {
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

    // Renderizar vista de tabla completa
    if (cartTableBody) {
      cartTableBody.replaceChildren(...cart.map((item) => {
        const tr = document.createElement("tr");

        const tdDetails = document.createElement("td");
        tdDetails.style.width = "40%";
        const categoryText = item.category ?? "Snack importado";
        tdDetails.innerHTML = `
          <span class="cart-item-title">${item.name}</span>
          <span class="cart-item-category">${categoryText}</span>
        `;

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
      const { productId, productName, productPrice } = button.dataset;
      const price = Number(productPrice);
      const quantity = Number(button.dataset.quantity ?? "1");
      if (!productId || !productName || !Number.isFinite(price) || price < 0
        || !Number.isInteger(quantity) || quantity < 1) {
        console.error("No se pudo agregar el producto: sus datos son inválidos.");
        await Swal.fire({ icon: "error", title: "No se pudo agregar", text: "Revisa los datos del producto e inténtalo nuevamente." });
        return;
      }

      try {
        const cart = readCart();
        const existingItem = cart.find((item) => item.id === productId);
        if (existingItem) existingItem.quantity += quantity;
        else cart.push({ id: productId, name: productName, price, quantity });

        saveCart(cart);
        renderCart();
        await Swal.fire({
          icon: "success",
          title: "¡Agregado al carrito!",
          text: quantity === 1 ? productName : `${productName} (cantidad: ${quantity})`,
          timer: 1500,
          showConfirmButton: false,
        });
      } catch (error) {
        console.error("No se pudo guardar el carrito en este navegador.", error);
        await Swal.fire({ icon: "error", title: "No se pudo guardar", text: "El navegador no permitió guardar el carrito." });
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
      saveCart([]);
      renderCart();
    } catch (error) {
      console.error("No se pudo vaciar el carrito guardado.", error);
      await Swal.fire({ icon: "error", title: "No se pudo vaciar", text: "Inténtalo nuevamente." });
    }
  });

  renderCart();
}

if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", () => initializeCart());
} else {
  initializeCart();
}
