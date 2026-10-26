import { initializeCart } from "./cart";
import { initImagePreview } from "./image-preview";

const productSelect = document.querySelector<HTMLSelectElement>("#promoProductSelect");
const addButton = document.querySelector<HTMLButtonElement>("#addPromoProduct");
const rowsBody = document.querySelector<HTMLTableSectionElement>("#promoProductRows");
const regularTotal = document.querySelector<HTMLElement>("#promoRegularTotal");
const productsError = document.querySelector<HTMLElement>("#promoProductsError");
const form = document.querySelector<HTMLFormElement>("#promotionForm");

function hasProduct(id: string): boolean {
  return Array.from(rowsBody?.rows ?? []).some((row) => row.dataset.productId === id);
}

function updateRegularTotal(): void {
  if (!rowsBody || !regularTotal) return;
  const total = Array.from(rowsBody.rows).reduce((sum, row) => sum + Number(row.dataset.price ?? 0), 0);
  regularTotal.textContent = total.toFixed(2);
}

function textCell(text: string): HTMLTableCellElement {
  const cell = document.createElement("td");
  cell.textContent = text;
  return cell;
}

function buildRow(id: string, name: string, category: string, price: string): HTMLTableRowElement {
  const row = document.createElement("tr");
  row.dataset.productId = id;
  row.dataset.price = price;

  const hidden = document.createElement("input");
  hidden.type = "hidden";
  hidden.name = "productIds";
  hidden.value = id;

  const removeButton = document.createElement("button");
  removeButton.type = "button";
  removeButton.className = "btn btn-sm btn-link text-danger promo-remove";
  removeButton.textContent = "Quitar";

  const actions = document.createElement("td");
  actions.append(hidden, removeButton);

  row.append(textCell(id), textCell(name), textCell(category), actions);
  return row;
}

addButton?.addEventListener("click", () => {
  if (!productSelect || !rowsBody) return;
  const option = productSelect.selectedOptions[0];
  const id = productSelect.value;
  if (!option || id === "" || hasProduct(id)) return;

  rowsBody.append(
    buildRow(id, option.dataset.name ?? id, option.dataset.category ?? "", option.dataset.price ?? "0"),
  );
  productSelect.value = "";
  productsError?.remove();
  updateRegularTotal();
});

rowsBody?.addEventListener("click", (event) => {
  const target = event.target as HTMLElement;
  if (!target.classList.contains("promo-remove")) return;
  target.closest("tr")?.remove();
  updateRegularTotal();
});

form?.addEventListener("submit", (event) => {
  if (rowsBody && rowsBody.rows.length === 0) {
    event.preventDefault();
    productSelect?.classList.add("is-invalid");
    productSelect?.focus();
  }
});

productSelect?.addEventListener("change", () => productSelect.classList.remove("is-invalid"));

updateRegularTotal();
initImagePreview();
initializeCart();