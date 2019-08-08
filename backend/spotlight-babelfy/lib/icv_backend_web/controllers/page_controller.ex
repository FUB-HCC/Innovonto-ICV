defmodule ICVBackendWeb.PageController do
  use ICVBackendWeb, :controller

  def index(conn, _params) do
    render(conn, "index.html")
  end
end
