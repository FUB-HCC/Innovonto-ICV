defmodule ICVBackend.Annotations do
  require Logger
  alias ICVBackend.Annotations.Builder
  alias ICVBackend.Annotations.StopwordFilter

  def fetch(text, confidence, lang \\ "en", type, backend) do
    # Logger.info("Proxy: #{ICVBackendWeb.Endpoint.config(:proxy)}")
    # Logger.info("Spotlight: #{ICVBackendWeb.Endpoint.config(:spotlight_base)}")

    annotation_candidates =
      case backend do
        :spotlight ->
          ICVBackend.Annotations.Spotlight.fetch(text, confidence, lang, type)

        :babelfy ->
          ICVBackend.Annotations.Babelfy.fetch(text, confidence, lang, type)

        :all ->
          spotlight =
            Task.async(fn ->
              ICVBackend.Annotations.Spotlight.fetch(text, confidence, lang, type)
            end)

          babelfy =
            Task.async(fn ->
              ICVBackend.Annotations.Babelfy.fetch(text, confidence, lang, type)
            end)

          spotlight =
            try do
              Task.await(spotlight, 10000)
            catch
              :exit, _ -> []
            end

          babelfy =
            try do
              Task.await(babelfy, 10000)
            catch
              :exit, _ -> []
            end

          Logger.info("Spotlight:")
          IO.inspect(spotlight)

          Logger.info("Babelfy:")
          IO.inspect(babelfy)
          spotlight ++ babelfy
      end
      |> Builder.group_by_buckets(text, :candidates)
      |> StopwordFilter.remove_buckets()

    %{
      text: text,
      annotation_candidates: annotation_candidates
    }
  end
end
