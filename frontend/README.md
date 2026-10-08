# Cornell Marketplace frontend

React, TypeScript, and Vite. Run `npm ci` and `npm run dev` from this directory.

The development server proxies `/auth`, `/users`, and `/listings` to port 8080. For hosting, set `VITE_API_URL` to the backend origin before running `npm run build`, or configure those paths through your host's reverse proxy.

Run `npm test`, `npm run build`, and `npm run lint` for checks. See [the project README](../README.md) for backend setup and configuration.
