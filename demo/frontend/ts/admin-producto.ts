import { initializeCart } from "./cart";
import { initImagePreview } from "./image-preview";

const form = document.querySelector<HTMLFormElement>("#productForm");
const nameInput = document.querySelector<HTMLInputElement>("#name");
const priceInput = document.querySelector<HTMLInputElement>("#price");
const stockInput = document.querySelector<HTMLInputElement>("#stock");

function setInvalid(input: HTMLInputElement | null, invalid: boolean): void {
  input?.classList.toggle("is-invalid", invalid);
}

function isValidName(): boolean {
  return nameInput !== null && nameInput.value.trim() !== "";
}

function isValidPrice(): boolean {
  const price = Number(priceInput?.value);
  return priceInput?.value !== "" && Number.isFinite(price) && price > 0;
}

function isValidStock(): boolean {
  const stock = Number(stockInput?.value);
  return stockInput?.value !== "" && Number.isInteger(stock) && stock >= 0 && stock <= 100;
}

nameInput?.addEventListener("input", () => setInvalid(nameInput, !isValidName()));
priceInput?.addEventListener("input", () => setInvalid(priceInput, !isValidPrice()));
stockInput?.addEventListener("input", () => setInvalid(stockInput, !isValidStock()));

form?.addEventListener("submit", (event) => {
  const nameOk = isValidName();
  const priceOk = isValidPrice();
  const stockOk = isValidStock();
  setInvalid(nameInput, !nameOk);
  setInvalid(priceInput, !priceOk);
  setInvalid(stockInput, !stockOk);

  if (!nameOk || !priceOk || !stockOk) {
    event.preventDefault();
  }
});

initImagePreview();
initializeCart();