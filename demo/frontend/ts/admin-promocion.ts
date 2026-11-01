import { initializeCart } from "./cart";
import { initImagePreview } from "./image-preview";

const productSelect = document.querySelector<HTMLSelectElement>("#promoProductSelect");
const addButton = document.querySelector<HTMLButtonElement>("#addPromoProduct");
const rowsBody = document.querySelector<HTMLTableSectionElement>("#promoProductRows");
const regularTotal = document.querySelector<HTMLElement>("#promoRegularTotal");
const productsError = document.querySelector<HTMLElement>("#promoProductsError");
const form = document.querySelector<HTMLFormElement>("#promotionForm");

const typeSelect = document.querySelector<HTMLSelectElement>("#promoType");
const descriptionGroup = document.querySelector<HTMLElement>("#descriptionGroup");
const imageGroup = document.querySelector<HTMLElement>("#imageGroup");
const priceLabel = document.querySelector<HTMLElement>("#priceLabel");
const priceInput = document.querySelector<HTMLInputElement>("#price");
const discountRow = document.querySelector<HTMLElement>("#promoDiscountRow");
const discountText = document.querySelector<HTMLElement>("#promoDiscount");

function isOffer(): boolean {
  return typeSelect?.value === "OFERTA";
}

function hasProduct(id: string): boolean {
  return Array.from(rowsBody?.rows ?? []).some((row) => row.dataset.productId === id);
}

function currentTotal(): number {
  if (!rowsBody) return 0;
  return Array.from(rowsBody.rows).reduce((sum, row) => sum + Number(row.dataset.price ?? 0), 0);
}

function updateDiscount(): void {
  if (!discountRow || !discountText) return;
  const total = currentTotal();
  const price = Number(priceInput?.value ?? 0);
  if (isOffer() && total > 0 && price > 0 && price < total) {
    discountText.textContent = `${Math.round((1 - price / total) * 100)}%`;
    discountRow.hidden = false;
  } else {
    discountRow.hidden = true;
  }
}

function updateRegularTotal(): void {
  if (!regularTotal) return;
  regularTotal.textContent = currentTotal().toFixed(2);
  updateDiscount();
}

function applyMode(): void {
  const offer = isOffer();
  if (descriptionGroup) descriptionGroup.hidden = offer;
  if (imageGroup) imageGroup.hidden = offer;
  if (priceLabel) priceLabel.textContent = offer ? "Precio de oferta (S/)" : "Precio promoción (S/)";
  if (addButton) addButton.textContent = offer ? "Elegir producto" : "+ Agregar producto";
  if (offer && rowsBody) {
    while (rowsBody.rows.length > 1) {
      rowsBody.deleteRow(rowsBody.rows.length - 1);
    }
  }
  updateRegularTotal();
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

  row.append(textCell("P" + id.padStart(2, "0")), textCell(name), textCell(category), actions);
  return row;
}

addButton?.addEventListener("click", () => {
  if (!productSelect || !rowsBody) return;
  const option = productSelect.selectedOptions[0];
  const id = productSelect.value;
  if (!option || id === "" || hasProduct(id)) return;

  if (isOffer()) rowsBody.replaceChildren();

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
  const count = rowsBody?.rows.length ?? 0;
  if (count === 0 || (isOffer() && count !== 1)) {
    event.preventDefault();
    productSelect?.classList.add("is-invalid");
    productSelect?.focus();
  }
});

productSelect?.addEventListener("change", () => productSelect.classList.remove("is-invalid"));
typeSelect?.addEventListener("change", applyMode);
priceInput?.addEventListener("input", updateDiscount);

applyMode();
initImagePreview();
initializeCart();