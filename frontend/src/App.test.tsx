// @vitest-environment jsdom
import { act, cleanup, render, screen, waitFor } from '@testing-library/react';
import { afterEach, beforeEach, expect, it, vi } from 'vitest';
import App from './App';

vi.mock('./Navbar.tsx', () => ({ default: () => null }));
vi.mock('./Homepage.tsx', () => ({ HomePage: () => <div>Home</div> }));
vi.mock('./CreateListing.tsx', () => ({ default: () => <div>Create listing</div> }));
vi.mock('./Login.tsx', () => ({ default: () => <div>Login page</div> }));

beforeEach(() => { localStorage.setItem('token', 'test-jwt'); window.history.replaceState({}, '', '/create-listing'); });
afterEach(() => { cleanup(); localStorage.clear(); vi.unstubAllGlobals(); });

it('settles session loading after a network failure and retains the token for retry', async () => {
  vi.stubGlobal('fetch', vi.fn().mockRejectedValue(new Error('Network unavailable')));
  render(<App />);
  await waitFor(() => expect(screen.getByRole('alert').textContent).toContain('Network unavailable'));
  expect(screen.queryByText('Checking your session?')).toBeNull();
  expect(localStorage.getItem('token')).toBe('test-jwt');
});
it('clears expired sessions and returns to login', async () => {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue(new Response('', { status: 401 })));
  render(<App />);
  await waitFor(() => expect(screen.getByText('Login page')).toBeTruthy());
  expect(localStorage.getItem('token')).toBeNull();
});
it('loads the authenticated user before opening a private page', async () => {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue(Response.json({ id: 1, username: 'Student' })));
  render(<App />);
  await waitFor(() => expect(screen.getByText('Create listing')).toBeTruthy());
});
it('ignores a late response after the app unmounts', async () => {
  let finish!: (response: Response) => void;
  const pending = new Promise<Response>((resolve) => { finish = resolve; });
  const fetchMock = vi.fn().mockReturnValue(pending);
  vi.stubGlobal('fetch', fetchMock);
  const view = render(<App />);
  view.unmount();
  expect(fetchMock.mock.calls[0][1].signal.aborted).toBe(true);
  await act(async () => { finish(new Response('', { status: 401 })); await pending; });
  expect(localStorage.getItem('token')).toBe('test-jwt');
});
