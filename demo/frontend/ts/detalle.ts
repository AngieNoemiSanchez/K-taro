import { initializeCart } from "./cart";

const quantityInput = document.querySelector<HTMLInputElement>("#detailQuantity");
const decreaseButton = document.querySelector<HTMLButtonElement>("#quantityDecrease");
const increaseButton = document.querySelector<HTMLButtonElement>("#quantityIncrease");
const addButton = document.querySelector<HTMLButtonElement>(".detail-add-button");

function normalizeQuantity(value: number): number {
  return Number.isFinite(value) ? Math.max(1, Math.floor(value)) : 1;
}

function syncQuantity(): void {
  if (!quantityInput || !addButton) return;
  const quantity = normalizeQuantity(Number(quantityInput.value));
  quantityInput.value = String(quantity);
  addButton.dataset.quantity = String(quantity);
}

decreaseButton?.addEventListener("click", () => {
  if (!quantityInput) return;
  quantityInput.value = String(normalizeQuantity(Number(quantityInput.value)) - 1);
  syncQuantity();
});

increaseButton?.addEventListener("click", () => {
  if (!quantityInput) return;
  quantityInput.value = String(normalizeQuantity(Number(quantityInput.value)) + 1);
  syncQuantity();
});

quantityInput?.addEventListener("input", syncQuantity);
quantityInput?.addEventListener("change", syncQuantity);
addButton?.addEventListener("click", syncQuantity);

syncQuantity();
initializeCart();
