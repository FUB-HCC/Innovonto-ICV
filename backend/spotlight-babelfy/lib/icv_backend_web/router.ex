defmodule ICVBackendWeb.Router do
  use ICVBackendWeb, :router

  pipeline :browser do
    plug :accepts, ["html"]
    plug :fetch_session
    plug :fetch_flash
    plug :protect_from_forgery
    plug :put_secure_browser_headers
  end

  pipeline :api do
    plug :accepts, ["json"]
  end

  scope "/api", ICVBackendWeb do
    pipe_through :api

    get "/candidates", AnnotationController, :candidates
  end

  # Other scopes may use custom stacks.
  # scope "/api", ICVBackendWeb do
  #   pipe_through :api
  # end
end
