import { expect, it } from 'vitest';
import { readJsonResponse } from './api';

it('preserves readable auth errors', async () => {
  await expect(readJsonResponse(Response.json({ error: 'Verify your email' }, { status: 403 }), 'Failed'))
    .rejects.toThrow('Verify your email');
});
it('handles HTML gateway errors without JSON syntax errors', async () => {
  await expect(readJsonResponse(new Response('<html>Bad gateway</html>', { status: 502 }), 'Login unavailable'))
    .rejects.toThrow('Login unavailable (HTTP 502)');
});
it('rejects an HTML success response from an incorrectly routed API', async () => {
  await expect(readJsonResponse(new Response('<html>Frontend</html>'), 'Failed'))
    .rejects.toThrow('Check the API configuration');
});
it('returns successful JSON payloads', async () => {
  await expect(readJsonResponse(Response.json({ token: 'jwt' }), 'Failed')).resolves.toEqual({ token: 'jwt' });
});
