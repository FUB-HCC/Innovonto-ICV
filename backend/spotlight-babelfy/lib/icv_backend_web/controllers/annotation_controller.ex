defmodule ICVBackendWeb.AnnotationController do
  use ICVBackendWeb, :controller

  alias ICVBackend.Annotations

  def annotate(conn, %{"text" => _text} = params) do
    do_fetch(conn, params, :annotate)
  end

  def candidates(conn, %{"text" => _text} = params) do
    do_fetch(conn, params, :candidates)
  end

  defp do_fetch(conn, %{"text" => text} = params, type) do
    confidence = String.to_float(Map.get(params, "confidence", "0.1"))
    lang = "en"

    result =
      case Map.get(params, "backend") do
        "spotlight" -> Annotations.fetch(text, confidence, lang, type, :spotlight)
        "babelfy" -> Annotations.fetch(text, confidence, lang, type, :babelfy)
        "all" -> Annotations.fetch(text, confidence, lang, type, :all)
        _ -> Annotations.fetch(text, confidence, lang, type, :spotlight)
      end

    json(conn, result)
  end
end
