import { initializeCart } from "./cart";

const searchForm = document.querySelector<HTMLFormElement>("#snackSearch");
const searchInput = document.querySelector<HTMLInputElement>("#snackQuery");
const snackCards = Array.from(document.querySelectorAll<HTMLElement>(".snack-card"));
const emptyMessage = document.querySelector<HTMLElement>("#snackEmpty");

function filterSnacks(query: string): void {
  const normalizedQuery = query.trim().toLocaleLowerCase("es");
  let visibleCount = 0;

  snackCards.forEach((card) => {
    const name = card.dataset.productName?.toLocaleLowerCase("es") ?? "";
    const matches = name.includes(normalizedQuery);
    card.hidden = !matches;
    if (matches) visibleCount += 1;
  });

  if (emptyMessage) emptyMessage.hidden = visibleCount > 0;
}

searchInput?.addEventListener("input", () => filterSnacks(searchInput.value));
searchForm?.addEventListener("submit", (event) => {
  event.preventDefault();
  filterSnacks(searchInput?.value ?? "");
});

initializeCart();
