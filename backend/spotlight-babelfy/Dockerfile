FROM elixir:1.8-alpine

ENV MIX_ARCHIVES /usr/local/mix

RUN mix local.hex --force \
 && mix local.rebar --force \
 && mix archive.install --force hex phx_new 1.4.1

ENV APP_HOME /app
RUN mkdir -p $APP_HOME
WORKDIR $APP_HOME

ENV MIX_ENV prod
ENV PORT 4000

COPY . .
RUN mix deps.get --only prod \
 && mix compile --force \
 && mix phx.digest


EXPOSE $PORT

CMD mix phx.server
