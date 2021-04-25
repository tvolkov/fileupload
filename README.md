# fileupload
file upload API

## Design considerations (thigs I've deliberately left behind for the sake of simplicity.)
 * authentication/authorization
 * more granular error response codes, e.g. 415, 406, 429, 401, 403, 413 etc etc
 * avoid storing db credentials in the source code, rather store then in a safe storage e.g. Vault and inject via env vars upon app startup
 * management endpoints
