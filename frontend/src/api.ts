export async function readJsonResponse<T>(response: Response, fallback: string): Promise<T> {
  const text = await response.text();
  let data: unknown;
  try { data = text ? JSON.parse(text) : null; } catch { /* Gateway errors may be HTML. */ }
  if (!response.ok) {
    const error = data && typeof data === 'object' && 'error' in data ? data.error : null;
    throw new Error(typeof error === 'string' ? error : `${fallback} (HTTP ${response.status}).`);
  }
  if (!data || typeof data !== 'object') throw new Error('The server returned an invalid response. Check the API configuration.');
  return data as T;
}
