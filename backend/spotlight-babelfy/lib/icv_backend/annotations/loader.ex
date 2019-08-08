defmodule ICVBackend.Annotations.Loader do
  require Logger

  def fetch(url, headers, params) do
    text = params.text

    proxy =
      URI.parse(ICVBackendWeb.Endpoint.config(:proxy, ""))
      |> case do
        %{scheme: scheme, host: host, port: port}
        when not (is_nil(scheme) or is_nil(host) or is_nil(port)) ->
          Logger.info("Proxy: #{scheme} #{host} #{port}")
          {String.to_charlist(host), port}

        _ ->
          Logger.info("No Proxy (or wrong config)")
          {}
      end

    # Logger.info(proxy)

    case HTTPoison.get(url, headers, params: params, proxy: proxy) do
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

  def fetch_image_url(label, lang \\ "en") do
    params = [
      action: "query",
      formatversion: 2,
      prop: "pageimages",
      titles: label,
      format: "json"
    ]

    proxy =
      URI.parse(ICVBackendWeb.Endpoint.config(:proxy, ""))
      |> case do
        %{scheme: scheme, host: host, port: port}
        when not (is_nil(scheme) or is_nil(host) or is_nil(port)) ->
          Logger.info("Proxy: #{scheme} #{host} #{port} for 'fetch_image'")
          {String.to_atom(scheme), String.to_charlist(host), port}

        _ ->
          Logger.info("No Proxy (or wrong config) for 'fetch_image'")
          {}
      end

    case HTTPoison.get(
           "https://#{lang}.wikipedia.org/w/api.php",
           [Accept: "Application/json; Charset=utf-8"],
           params: params,
           proxy: proxy
         ) do
      {:ok, %{status_code: 200, body: body}} ->
        # IO.inspect(Poison.decode!(body))

        case Jason.decode(body) do
          {:ok, %{"batchcomplete" => true, "query" => %{"pages" => pages}}} ->
            if !Enum.empty?(pages) do
              page = Enum.at(pages, 0)

              if !is_nil(page["pageimage"]) do
                pageimage = page["pageimage"] |> URI.encode_www_form()

                # regex = ~r/thumb\/(\w+|\d+)\/(\w+|\d+)\/#{page["pageimage"]}/
                regex = ~r/\/(\w+|\d+)\/(\w+|\d+)\/#{pageimage}/

                url =
                  page
                  |> Map.get("thumbnail", %{})
                  |> Map.get("source")

                unless is_nil(url) do
                  uri = Regex.run(regex, url) |> Enum.at(0)
                  "https://upload.wikimedia.org/wikipedia/commons" <> uri
                end
              end
            end

          _ ->
            nil
        end

      _ ->
        nil
    end
  end
end
