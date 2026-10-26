import { initializeCart } from "./cart";
import { initImagePreview } from "./image-preview";

const form = document.querySelector<HTMLFormElement>("#productForm");
const priceInput = document.querySelector<HTMLInputElement>("#price");
const stockInput = document.querySelector<HTMLInputElement>("#stock");

function setInvalid(input: HTMLInputElement | null, invalid: boolean): void {
  input?.classList.toggle("is-invalid", invalid);
}

function isValidPrice(): boolean {
  const price = Number(priceInput?.value);
  return priceInput?.value !== "" && Number.isFinite(price) && price > 0;
}

function isValidStock(): boolean {
  const stock = Number(stockInput?.value);
  return stockInput?.value !== "" && Number.isInteger(stock) && stock >= 0;
}

priceInput?.addEventListener("input", () => setInvalid(priceInput, !isValidPrice()));
stockInput?.addEventListener("input", () => setInvalid(stockInput, !isValidStock()));

form?.addEventListener("submit", (event) => {
  const priceOk = isValidPrice();
  const stockOk = isValidStock();
  setInvalid(priceInput, !priceOk);
  setInvalid(stockInput, !stockOk);

  if (!priceOk || !stockOk) {
    event.preventDefault();
  }
});

initImagePreview();
initializeCart();