export const API_BASE = 'http://localhost:8080';

export function resolveApiUrl(path?: string | null): string {
  if (!path) {
    return `${API_BASE}/api/files/product-fa-01.svg`;
  }
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path;
  }
  return `${API_BASE}${path.startsWith('/') ? path : `/${path}`}`;
}
