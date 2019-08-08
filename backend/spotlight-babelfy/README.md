# ICVBackend

This Repository contains the Elixir Backend for the ICV Project.

## Usage

Right now this Backend offers exactly one url `/api/candidates`

### `GET: /api/candidates`

GET requests against this URL will give you annotation candidates for a given text. The following parameters are available:

- `text` - required
- `confidence` - default: 0.2
- `backend` - default: spotlight

#### `text` - required

The text is the text for wich the annotation candidates are requested.

##### Example - "Foo Bar"

```sh
curl --request GET \
  --url 'http://localhost:4000/api/candidates?text=Foo Bar'
```
#### `confidence` 

This value is only important for the spotlight backend. It defines how confident Spotlight has to be to return a candidate.

##### Example - "Foo Bar" - Confidence: 0.01

```sh
curl --request GET \
  --url 'http://localhost:4000/api/candidates?text=Foo Bar&confidence=0.01'
```

#### `backend` 

For now, two backends are available. `spotlight` (default) & `babelfy`

##### Examples - "Foo Bar"

```sh
curl --request GET \
  --url 'http://localhost:4000/api/candidates?text=Foo Bar&backend=babelfy'
```

```sh
curl --request GET \
  --url 'http://localhost:4000/api/candidates?text=Foo Bar&backend=all'
```


## Production Server

To start your Phoenix server:

  * Install dependencies with `mix deps.get --only prod`
  * Compile everything with `MIX_ENV=prod mix compile`
  * Build the prod.secret.exs with `mix BuildSecrets`
  * Compress static files with `mix phx.digest`
  * Start Phoenix endpoint with `PORT=4001 MIX_ENV=prod mix phx.server`

Now you can visit [localhost:4001/api/candidates?confidence=0.2&text=foo]() from your browser.

### Important Additional Environment Variables

#### `PROXY`

Sets the Proxy for every HTTP request.

##### Example

```sh
PROXY=http://proxy.fu-berlin.de MIX_ENV=prod mix phx.server
```

#### `BABELFY_API_KEY`

Sets the Babelfy Api Key to use for requests against the Babelfy API. 

A key can be retreived via [http://babelfy.org/guide]()

##### Example

```sh
BABELFY_API_KEY=asdfdsaf145-54fsaadf-asdf-fdgh-asdfthduirds546h MIX_ENV=prod mix phx.server
```

## Development Server

To start your Phoenix server:

  * Install dependencies with `mix deps.get`
  * Start Phoenix endpoint with `mix phx.server`

Now you can visit [localhost:4000/api/candidates?confidence=0.2&text=foo]() from your browser.

### Important Additional Environment Variables

#### `PROXY`

Sets the Proxy for every HTTP request.

##### Example

```sh
PROXY=http://proxy.fu-berlin.de mix phx.server
```

#### `BABELFY_API_KEY`

Sets the Babelfy Api Key to use for requests against the Babelfy API. 

A key can be retreived via [http://babelfy.org/guide]()

##### Example

```sh
BABELFY_API_KEY=asdfdsaf145-54fsaadf-asdf-fdgh-asdfthduirds546h mix phx.server
```


## Docker

To use Docker have a look at the docker file.

### Add the `prod.secret.exs` File

Make sure the `prod.secret.exs` is present in the `config` Folder. If it's not: 

- rename `prod.secret.exs.example` to `prod.secret.exs`
- change the contents of `secret_key_base`

Phoenix offerst a neat generator for secrets. Just use `mix phx.gen.secret`.

### Build

```
docker build -t git.imp.fu-berlin.de:5000/mx-icv/standalone-phx-backend .
```

### Run

```
docker run -u 1000:1000 -p 4000:4000 git.imp.fu-berlin.de:5000/mx-icv/standalone-phx-backend:latest
```

### Push

```
docker push git.imp.fu-berlin.de:5000/mx-icv/standalone-phx-backend
```

### Important Additional Environment Variables

#### `PROXY`

Sets the Proxy for every HTTP request.

##### Example

```sh
docker run -u 1000:1000 -p 4000:4000 -e "PROXY=http://proxy.fu-berlin.de" git.imp.fu-berlin.de:5000/mx-icv/standalone-phx-backend:latest
```

#### `BABELFY_API_KEY`

Sets the Babelfy Api Key to use for requests against the Babelfy API. 

A key can be retreived via [http://babelfy.org/guide]()

##### Example

```sh
docker run -u 1000:1000 -p 4000:4000 -e "BABELFY_API_KEY=asdfdsaf145-54fsaadf-asdf-fdgh-asdfthduirds546h" git.imp.fu-berlin.de:5000/mx-icv/standalone-phx-backend:latest
```

## Known Issues

### `mix phx.digest` doesn't run sucessfully

- [x] `mkdir priv/static` should solve the issue

## Learn more

  * Official website: http://www.phoenixframework.org/
  * Guides: https://hexdocs.pm/phoenix/overview.html
  * Docs: https://hexdocs.pm/phoenix
