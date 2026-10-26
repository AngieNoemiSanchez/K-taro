const MAX_IMAGE_BYTES = 5 * 1024 * 1024;
const ALLOWED_TYPES = ["image/png", "image/jpeg", "image/webp", "image/gif"];

export function initImagePreview(): void {
  const input = document.querySelector<HTMLInputElement>("#image");
  const preview = document.querySelector<HTMLImageElement>("#imagePreview");
  const error = document.querySelector<HTMLElement>("#imageClientError");
  if (!input || !preview) return;

  const originalSrc = preview.getAttribute("src");
  let objectUrl: string | null = null;

  function showError(message: string): void {
    if (!error) return;
    error.textContent = message;
    error.hidden = message === "";
  }

  input.addEventListener("change", () => {
    if (objectUrl) {
      URL.revokeObjectURL(objectUrl);
      objectUrl = null;
    }
    showError("");
    input.classList.remove("is-invalid");

    const file = input.files?.[0];
    if (!file) {
      if (originalSrc) preview.src = originalSrc;
      preview.hidden = !originalSrc;
      return;
    }

    if (!ALLOWED_TYPES.includes(file.type)) {
      input.value = "";
      input.classList.add("is-invalid");
      showError("Formato no válido. Usa PNG, JPG, WEBP o GIF.");
      return;
    }
    if (file.size > MAX_IMAGE_BYTES) {
      input.value = "";
      input.classList.add("is-invalid");
      showError("La imagen supera los 5 MB.");
      return;
    }

    objectUrl = URL.createObjectURL(file);
    preview.src = objectUrl;
    preview.hidden = false;
  });
}