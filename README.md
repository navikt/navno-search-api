# navno-search-api
Søkebackend for nav.no

Secrets ligger i [Google Secret Manager](https://doc.nais.io/security/secrets/google-secrets-manager/).

## Henting av felles bibliotek

Et felles bibliotek publiseres av [navno-search-admin-api](https://github.com/navikt/navno-search-admin-api). Biblioteket inneholder diverse konstanter, samt klassen som brukes for å opprette Opensearch-indexen. Ved deling og versjonering av denne er det mulig å opprette og populere en ny index før man skrur apiet over til å søke mot denne.

For å kunne hente dette biblioteket lokalt, må man opprette et PAT i Github og sette dette i `~/.gradle/gradle.properties`.

```
githubPassword=<PAT>
```

## Lokal kjøring
For å kjøre appen lokalt må man opprette en application-local.yml-fil og populere denne med opensearch-credentials, som ligger i kubernetes.

```
opensearch:
  uris: <uri fra secret>
  username: <brukernavn fra secret>
  password: <passord fra secret>
```

Husk å starte applikasjonen med profile "local".

## Deploy til dev

[Actions](https://github.com/navikt/navno-search-api/actions) -> Velg workflow -> Run workflow -> Velg branch -> Run workflow

## Prodsetting

-   Lag en PR til main, og merge inn etter godkjenning
-   Lag en release på master med versjon-bump, beskrivende tittel og oppsummering av endringene dine
-   Publiser release-en for å starte deploy til prod

## Logging

[Kibana](https://logs.adeo.no/app/discover#/view/c7ebebe0-aa35-11ee-991c-09effcd7b5da)

## Henvendelser

Spørsmål knyttet til koden eller prosjektet kan rettes mot https://github.com/orgs/navikt/teams/personbruker

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #team-personbruker.