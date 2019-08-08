defmodule Mix.Tasks.Icv.ExhaustApikey do
  use Mix.Task

  require Logger
  require HTTPoison

  @impl Mix.Task
  def run(_args) do
    {:ok, _started} = Application.ensure_all_started(:httpoison)

    # build url
    url = "http://localhost:4000/api/candidates?text=foo&backend=babelfy"

    for _ <- 1..1100 do
      # Call Babelfy Api
      case HTTPoison.get(url) do
        {:ok, %{status_code: 200, body: body}} ->
          Logger.info("#{url} : 200")
          {:ok, body}

        {:ok, %{status_code: 404} = response} ->
          Logger.info("#{url} : 404")
          {:error, response}

        {:ok, response} ->
          Logger.error("#{url} : #{response.status_code}")
          {:error, response}

        {:error, error} ->
          Logger.error("#{url} : #{error.reason}")
          {:error, %{error: error, body: error.reason}}
      end
    end
  end
end
