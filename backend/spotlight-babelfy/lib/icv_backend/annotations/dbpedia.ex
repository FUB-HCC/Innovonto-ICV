defmodule ICVBackend.Annotations.DBPedia do
  require Logger

  alias ICVBackend.Annotations.Loader

  def fetch_label_description_thumbnail(uri, lang \\ "en") do
    case lang do
      "en" ->
        Cachex.fetch(:dbpedia, uri, fn uri ->
          {:commit, fetch_label_description_thumbnail_remote(uri, lang)}
        end)
        |> case do
          {:ok, tuple} -> tuple
          {:commit, tuple} -> tuple
          _ -> {}
        end

      _ ->
        fetch_label_description_thumbnail_remote(uri, lang)
    end
  end

  def fetch_label_description_thumbnail_remote(uri, lang \\ "en") do
    backup_label = Regex.replace(~r/_/, uri, " ")

    proxy =
      URI.parse(ICVBackendWeb.Endpoint.config(:proxy, ""))
      |> case do
        %{scheme: scheme, host: host, port: port}
        when not (is_nil(scheme) or is_nil(host) or is_nil(port)) ->
          {String.to_charlist(host), port}

        _ ->
          {}
      end

    """
    prefix dbo: <http://dbpedia.org/ontology/>

    select ?label ?abstract ?thumbnail where { 
      optional { <http://dbpedia.org/resource/#{uri}> rdf:label ?label . filter(langMatches(lang(?label),"#{
      lang
    }")) }
      optional { <http://dbpedia.org/resource/#{uri}> dbo:abstract ?abstract . filter(langMatches(lang(?abstract),"#{
      lang
    }")) }
      optional { <http://dbpedia.org/resource/#{uri}> dbo:thumbnail ?thumbnail . }
    }
    """
    |> SPARQL.Client.query("http://dbpedia.org/sparql", %{proxy: proxy})
    |> case do
      {:ok, %SPARQL.Query.Result{results: results}} ->
        result = List.first(results)

        label =
          if is_nil(result["label"]) do
            backup_label
          else
            result["label"]
          end

        thumb =
          unless is_nil(result["thumbnail"]) do
            # |> URI.encode_www_form()
            reg = ~r/FilePath\/(.+)(?=(\.jpg)|(\.png)|(\.gif))/

            Regex.replace(reg, to_string(result["thumbnail"]), fn _, filename ->
              "FilePath/" <> URI.encode_www_form(filename)
            end)
          else
            Loader.fetch_image_url(label, lang)
          end

        {label, to_string(result["abstract"]), thumb}

      _error ->
        Logger.debug("other result")
        {backup_label, "", nil}
    end
  end

  def fetch_image_and_description(uri, label, lang \\ "en") do
    """
    prefix dbo: <http://dbpedia.org/ontology/>

    select ?abstract ?thumbnail where { 
      optional { <http://dbpedia.org/resource/#{uri}> dbo:abstract ?abstract . filter(langMatches(lang(?abstract),"#{
      lang
    }")) }
      optional { <http://dbpedia.org/resource/#{uri}> dbo:thumbnail ?thumbnail . }
    }
    """
    |> SPARQL.Client.query("http://dbpedia.org/sparql", %{
      proxy: ICVBackendWeb.Endpoint.config(:proxy, "")
    })
    |> case do
      {:ok, %SPARQL.Query.Result{results: results}} ->
        result = List.first(results)

        thumb =
          unless is_nil(result["thumbnail"]) do
            # |> URI.encode_www_form()
            reg = ~r/FilePath\/(.+)(?=(\.jpg)|(\.png))/

            Regex.replace(reg, to_string(result["thumbnail"]), fn _, filename ->
              "FilePath/" <> URI.encode_www_form(filename)
            end)
          else
            Loader.fetch_image_url(label, lang)
          end

        {thumb, "#{result["abstract"]}"}

      _error ->
        Logger.debug("other result")
        {nil, nil}
    end
  end
end
