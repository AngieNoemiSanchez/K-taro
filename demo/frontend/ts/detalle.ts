import { initializeCart } from "./cart";

const quantityInput = document.querySelector<HTMLInputElement>("#detailQuantity");
const decreaseButton = document.querySelector<HTMLButtonElement>("#quantityDecrease");
const increaseButton = document.querySelector<HTMLButtonElement>("#quantityIncrease");
const addButton = document.querySelector<HTMLButtonElement>(".detail-add-button");

function maxQuantity(): number {
  const max = Number(quantityInput?.max);
  return Number.isFinite(max) && max >= 1 ? Math.floor(max) : Number.MAX_SAFE_INTEGER;
}

function normalizeQuantity(value: number): number {
  const quantity = Number.isFinite(value) ? Math.max(1, Math.floor(value)) : 1;
  return Math.min(quantity, maxQuantity());
}

function syncQuantity(): void {
  if (!quantityInput || !addButton) return;
  const quantity = normalizeQuantity(Number(quantityInput.value));
  quantityInput.value = String(quantity);
  addButton.dataset.quantity = String(quantity);
}

decreaseButton?.addEventListener("click", () => {
  if (!quantityInput) return;
  quantityInput.value = String(Math.max(1, normalizeQuantity(Number(quantityInput.value)) - 1));
  syncQuantity();
});

increaseButton?.addEventListener("click", () => {
  if (!quantityInput) return;
  quantityInput.value = String(Math.min(maxQuantity(), normalizeQuantity(Number(quantityInput.value)) + 1));
  syncQuantity();
});

quantityInput?.addEventListener("input", syncQuantity);
quantityInput?.addEventListener("change", syncQuantity);
addButton?.addEventListener("click", syncQuantity);

syncQuantity();
initializeCart();