# This file is responsible for configuring your application
# and its dependencies with the aid of the Mix.Config module.
#
# This configuration file is loaded before any dependency and
# is restricted to this project.

# General application configuration
use Mix.Config

config :icv_backend,
  namespace: ICVBackend

# Configures the endpoint
config :icv_backend, ICVBackendWeb.Endpoint,
  url: [host: "localhost"],
  secret_key_base: "3C6u4CtpLjCqi3Q4V4Bs4/3YmOVE6b+ywt1fGlvRlIhugr3N43FqBNmAWOmc936q",
  render_errors: [view: ICVBackendWeb.ErrorView, accepts: ~w(html json)],
  pubsub: [name: ICVBackend.PubSub, adapter: Phoenix.PubSub.PG2]

config :icv_backend, ICVBackendWeb.Endpoint,
  proxy: System.get_env("PROXY") || "",
  spotlight_base: System.get_env("DBPEDIA_SPOTLIGHT") || "http://api.dbpedia-spotlight.org",
  babelfy_api_key: System.get_env("BABELFY_API_KEY") || ""

# Configures Elixir's Logger
config :logger, :console,
  format: "$time $metadata[$level] $message\n",
  metadata: [:request_id]

# Use Jason for JSON parsing in Phoenix
config :phoenix, :json_library, Jason

config :tesla, :adapter, Tesla.Adapter.Hackney

# Import environment specific config. This must remain at the bottom
# of this file so it overrides the configuration defined above.
import_config "#{Mix.env()}.exs"
