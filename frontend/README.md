# OneViewMed Frontend (Dashboard) Project

# Requirements

- [Node.js](https://nodejs.org/) 18+
- [Yarn](https://yarnpkg.com/) berry

# Configure Registry Access

There is a separate NPM package for easy access to the backend API.
This package can be found in the package registry of the [backend project on GitLab](https://erbenschell.iese.fraunhofer.de/oneviewmed/backend/-/packages).

Access to the repository must be set up so that the package can be retrieved.
To do this, you must add the following entry to the Yarn Config file in your home directory `C:\Users\<user>\.yarnrc.yml` and replace the token in it with your private GitLab token.

```yaml
npmRegistries:
  https://erbenschell.iese.fraunhofer.de/api/v4/projects/1678/packages/npm:
    npmAuthToken: <your private gitlab token>
```

# Run

```shell
yarn install
cd ../..
yarn dev
```

# Build

```shell
yarn install
yarn build
```

# Update API

1. Update `packages/api-docs.json`
2. Generate API code
   ```shell
   yarn generate-api
   ```

> The `packages/client-api/tsconfig.json` and `packages/client-api/package.json` files are generated incorrectly by OpenApi Generator. This file does not work with Yarn workspaces and Typescript. If it is changed during generation, these changes must be reverted.
