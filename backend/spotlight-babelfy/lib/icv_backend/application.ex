defmodule ICVBackend.Application do
  # See https://hexdocs.pm/elixir/Application.html
  # for more information on OTP Applications
  @moduledoc false

  use Application
  import Supervisor.Spec

  def start(_type, _args) do
    IO.inspect(System.get_env())

    # List all child processes to be supervised
    children = [
      # Start the endpoint when the application starts
      ICVBackendWeb.Endpoint,
      worker(Cachex, [:dbpedia, [interval: 18_000_000, lazy: false]])
      # Starts a worker by calling: ICVBackend.Worker.start_link(arg)
      # {ICVBackend.Worker, arg},
    ]

    # See https://hexdocs.pm/elixir/Supervisor.html
    # for other strategies and supported options
    opts = [strategy: :one_for_one, name: ICVBackend.Supervisor]
    Supervisor.start_link(children, opts)
  end

  # Tell Phoenix to update the endpoint configuration
  # whenever the application is updated.
  def config_change(changed, _new, removed) do
    ICVBackendWeb.Endpoint.config_change(changed, removed)
    :ok
  end
end
