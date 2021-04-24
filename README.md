# fileupload
file upload API

## Design considerations (thigs I've deliberately left behind for the sake of simplicity.)
 * authentication/authorization
 * more granular error response codes, e.g. 415, 406, 429, 401, 403, 413 etc etc
 * ignore files' contents (i.e. only operate on files' names)
 * avoid storing db credentials in the source code, rather store then in a safe storage e.g. Vault and inject via env vars upon app startup
 * db migration
 * management endpoints
 * only tested with h2 databse, but ideally it needs to be tested on a real db, e.g, things like random_uuid would not work on Postgres for instance..
 * tags are stored as a one single string in the db which might not be very efficient when searching by tags as the time required to check whether a row contains given tag is proportional to the overall length of tags in the row, e.g. somewhat O(N). Storing tags in a hashtable-like structure would greatly reduce that time. Or even better, a full text search engine would this job the best, I think.
