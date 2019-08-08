defmodule ICVBackend.Annotations.Spotlight do
  @spotlight_fallback "http://api.dbpedia-spotlight.org"
  @spotlight_header [Accept: "Application/json; Charset=utf-8"]

  @wordsplit_regex ~r/[^\w\-]/

  import Cachex.Spec
  require Logger

  alias ICVBackend.Annotations.Loader
  alias ICVBackend.Annotations.DBPedia
  # alias ICVBackend.Annotations.Builder

  def fetch(text, confidence, lang \\ "en", type) do
    base = ICVBackendWeb.Endpoint.config(:spotlight_base, @spotlight_fallback)
    params = %{text: text, confidence: confidence}

    Logger.info("Base: " <> base)

    Loader.fetch(base <> "/#{lang}/" <> Atom.to_string(type), @spotlight_header, params)
    |> case do
      {:ok, response} ->
        response
        |> Jason.decode!()
        |> transform_candidates(text)

      {:error, response} ->
        Logger.error("#{response.body}", remote: "spotlight")
        []

      _ ->
        []
    end
  end

  def transform_candidates(spotlight_response, _text) do
    #! TODO: split does not work correctly
    _text_list = Regex.split(@wordsplit_regex, spotlight_response["annotation"]["@text"])

    if Map.has_key?(spotlight_response["annotation"], "surfaceForm") do
      surfaceForm =
        if is_list(spotlight_response["annotation"]["surfaceForm"]),
          do: spotlight_response["annotation"]["surfaceForm"],
          else: [spotlight_response["annotation"]["surfaceForm"]]

      Enum.reduce(surfaceForm, [], fn cand, annotation_list ->
        cand =
          if is_list(cand["resource"]),
            do: cand,
            else: Map.update!(cand, "resource", &[&1])

        build_tokens(cand, spotlight_response["annotation"]["@text"], :candidates) ++
          annotation_list
      end)

      # {_list, annotation_candidates} =
      #   Enum.flat_map_reduce(surfaceForm, [], fn cand, bucket ->
      #     cand =
      #       unless is_list(cand["resource"]) do
      #         Map.update!(cand, "resource", &[&1])
      #       else
      #         cand
      #       end

      #     [build_tokens(cand, spotlight_response["annotation"]["@text"], :candidates) | annotation_candidates]

      #     # new_list = build_tokens(cand, spotlight_response["annotation"]["@text"], :candidates)
      #     # {new_list, Builder.insert_list_in_bucket(new_list, bucket, text_list, :candidates)}
      #   end)

      # annotation_candidates
    else
      []
    end
  end

  def build_tokens(spotlight_map, full_text, :candidates) do
    #! TODO: test new regex
    span = Regex.split(@wordsplit_regex, spotlight_map["@name"])
    span_length = length(span)

    offset = String.to_integer(spotlight_map["@offset"])

    token_span_start =
      if offset > 0 do
        String.slice(full_text, 0, offset - 1)
        |> String.split(" ", trim: true)
        |> length()
      else
        0
      end

    Cachex.execute(:dbpedia, fn cache ->
      spotlight_map["resource"]
      |> Enum.map(fn resource ->
        Task.async(fn ->
          {_label, description, thumbnail} =
            Cachex.fetch(cache, resource["@uri"], fn uri ->
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
              text: spotlight_map["@name"],
              offset: offset,
              token_span: %{
                start: token_span_start,
                end: token_span_start + span_length - 1
              },
              label: resource["@label"],
              resource: "http://dbpedia.org/resource/" <> resource["@uri"],
              thumbnail: thumbnail,
              description: description,
              confidence: String.to_float(resource["@finalScore"]),
              source: :spotlight
            }
          end
        end)
      end)
      |> Enum.map(&Task.await(&1, 30000))
      |> Enum.filter(& &1)
    end)
  end
end
