# fileupload
file upload API

## Design considerations (thigs I've deliberately left behind for the sake of simplicity.)
 * authentication/authorization
 * more granular error response codes, e.g. 415, 406, 429, 401, 403, 413 etc etc
 * avoid storing db credentials in the source code, rather store then in a safe storage e.g. Vault and inject via env vars upon app startup
 * management endpoints
 * using javax.validation API
 * documenting API (openapi/swagger)
 * more granular unit tests
 * dockerizing the app

## How to run
 - go to the project's root directory
 - run `docker run  -p 27017:27017 -d mongo`  
 - run `mvn spring-boot:run`

one the application is running you can hit it's endpoint as described in `fileupload-spec.yaml`
 
