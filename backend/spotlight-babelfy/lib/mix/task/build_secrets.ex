defmodule Mix.Tasks.BuildSecrets do
  use Mix.Task

  @impl Mix.Task
  def run(_args) do
    # Build prod.secret.exs
    Mix.Shell.cmd("mix phx.gen.secret", fn secret_key_base ->
      build_producion_secret_file(String.trim(secret_key_base))
    end)
  end

  defp build_producion_secret_file(secret_key_base) do
    IO.inspect(secret_key_base)
    prod_secret_file = Path.join(File.cwd!(), "/config/prod.secret.exs")

    if not File.exists?(prod_secret_file) do
      content = EEx.eval_file(prod_secret_file <> ".eex", secret_key_base: secret_key_base)

      File.open(prod_secret_file, [:write, :utf8], fn file ->
        IO.write(file, content)
      end)
    end
  end
end
