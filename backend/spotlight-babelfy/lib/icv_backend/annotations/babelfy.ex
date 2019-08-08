defmodule ICVBackend.Annotations.Babelfy do
  @babelfy_fallback "https://babelfy.io/v1"
  @default_header [Accept: "Application/json; Charset=utf-8"]
  @wordsplit_regex ~r/[^\w\-]/

  require Logger

  alias ICVBackend.Annotations.Loader
  alias ICVBackend.Annotations.DBPedia
  # alias ICVBackend.Annotations.Builder

  def fetch(text, _confidence, lang \\ "en", :candidates) do
    base = ICVBackendWeb.Endpoint.config(:babelfy_base, @babelfy_fallback)

    Logger.info("Base: " <> base)

    params = %{
      text: text,
      lang: String.upcase(lang),
      cands: "ALL",
      extAIDA: true,
      annRes: "WIKI",
      key: ICVBackendWeb.Endpoint.config(:babelfy_api_key, "")
    }

    Loader.fetch(base <> "/disambiguate", @default_header, params)
    |> case do
      {:ok, response} ->
        # IO.inspect(response |> Jason.decode!())

        response
        |> Jason.decode!()
        |> case do
          %{"message" => message} ->
            Logger.error("#{message}")
            []

          response ->
            transform_candidates(response, text)
        end

      {:error, response} ->
        Logger.error("#{response.body}", remote: "babelfy")
        []

      _ ->
        []
    end
  end

  def transform_candidates(response, text) do
    #! TODO: test new regex
    _text_list = Regex.split(@wordsplit_regex, text)

    Cachex.execute(:dbpedia, fn cache ->
      Enum.map(response, fn cand ->
        Task.async(fn ->
          case String.length(cand["DBpediaURL"]) do
            # work only on candidates with DBpedia-resource
            0 ->
              false

            _ ->
              build_token(cand, text, cache, :candidates)
          end
        end)
      end)
      |> Enum.map(&Task.await(&1, 30000))
      |> Enum.filter(& &1)
    end)
  end

  def build_token(babelfy_cand, full_text, cache \\ :dbpedia, :candidates) do
    %{"uri" => uri} =
      Regex.named_captures(~r/(?<=\/resource\/)(?<uri>.*)/, babelfy_cand["DBpediaURL"])

    {label, description, thumbnail} =
      Cachex.fetch(cache, uri, fn uri ->
        {:commit, DBPedia.fetch_label_description_thumbnail_remote(uri)}
      end)
      |> case do
        {:ok, tuple} -> tuple
        {:commit, tuple} -> tuple
        {:error, errmsg} -> Logger.error(errmsg)
      end

    if (is_nil(description) || description == "") && is_nil(thumbnail) do
      false
    else
      %{
        text:
          String.slice(
            full_text,
            babelfy_cand["charFragment"]["start"]..babelfy_cand["charFragment"]["end"]
          ),
        offset: babelfy_cand["charFragment"]["start"],
        token_span: %{
          start: babelfy_cand["tokenFragment"]["start"],
          end: babelfy_cand["tokenFragment"]["end"]
        },
        label: label,
        resource: babelfy_cand["DBpediaURL"],
        thumbnail: thumbnail,
        description: description,
        confidence: babelfy_cand["score"],
        source: :babelfy
      }
    end
  end
end
