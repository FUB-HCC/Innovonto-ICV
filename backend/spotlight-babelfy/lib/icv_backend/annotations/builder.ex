defmodule ICVBackend.Annotations.Builder do
  require Logger
  @wordsplit_regex ~r/[^\w\-]/

  @doc """
  Takes a list of tokens an sorted array of buckets and a text_list and inserts every item in
  token_list into the bucket

  Returns the new array of buckets
  """
  def insert_list_in_bucket(token_list, bucket, text, :candidates)
      when is_list(token_list) do
    Enum.reduce(token_list, bucket, fn token, bucket ->
      insert_in_bucket(token, bucket, text, :candidates)
    end)
  end

  @doc """
  Inserts a token into the array of buckets

  Returns new array of buckets including token
  """
  def insert_in_bucket(new_token, buckets, text, :candidates) do
    # check if the span overlaps with a existing span
    # overlap will hold the index of the overlapping bucket or nil

    {buckets, overlappings} =
      Enum.split_with(buckets, fn bucket ->
        token_range = new_token.offset..(new_token.offset + String.length(new_token.text))
        bucket_range = bucket.offset..(bucket.offset + String.length(bucket.text))
        Range.disjoint?(token_range, bucket_range)
      end)

    case overlappings do
      # if no overlap is found then a new bucket is created with only the 
      # new token in it. This b
      [] ->
        [
          %{
            text: new_token.text,
            offset: new_token.offset,
            token_span: new_token.token_span,
            resource_candidates: [new_token]
          }
          | buckets
        ]

      # if overlappings are found then those buckets need to be merged into one bucket
      overlappings ->
        Logger.debug(length(overlappings))

        bucket =
          merge_buckets(overlappings, text)
          |> add_to_bucket(new_token, text)

        # the extendend bucket is added to the rest of the list
        [bucket | buckets]
    end
  end

  defp merge_buckets(bucket, [], _text) do
    bucket
  end

  defp merge_buckets(bucket_a, [bucket_b | bucket_list], text) do
    new_bucket = merge_two_buckets(bucket_a, bucket_b, text)
    merge_buckets(new_bucket, bucket_list, text)
  end

  defp merge_buckets([bucket_a | bucket_list], text) do
    merge_buckets(bucket_a, bucket_list, text)
  end

  defp add_to_bucket(bucket, token, text) do
    offset = min(bucket.offset, token.offset)

    offset_end =
      max(
        bucket.offset + String.length(bucket.text) - 1,
        token.offset + String.length(token.text) - 1
      )

    token_span = %{
      start: min(bucket.token_span.start, token.token_span.start),
      end: max(bucket.token_span.end, token.token_span.end)
    }

    text = String.slice(text, offset..offset_end)

    %{
      text: text,
      offset: offset,
      token_span: token_span,
      resource_candidates: [token | bucket.resource_candidates]
    }
  end

  defp merge_two_buckets(a, b, text) do
    offset = min(a.offset, b.offset)

    offset_end =
      max(
        a.offset + String.length(a.text) - 1,
        b.offset + String.length(b.text) - 1
      )

    token_span = %{
      start: min(a.token_span.start, b.token_span.start),
      end: max(a.token_span.end, b.token_span.end)
    }

    text = String.slice(text, offset..offset_end)

    %{
      text: text,
      offset: offset,
      token_span: token_span,
      resource_candidates: a.resource_candidates ++ b.resource_candidates
    }
  end

  defp insert_in_specific_bucket(new_token, bucket, text_list) do
    token_range = Range.new(new_token.offset, new_token.offset + String.length(new_token.text))
    bucket_range = Range.new(bucket.offset, bucket.offset + String.length(bucket.text))

    # if the new token starts before the existing bucket the existing bucket
    # has to be expanded in that direction and the text, offset and tokenspan
    # need to be updated
    # (token), [bucket] with (1[2)3] -> [(12)3] no overlapping borders! 
    bucket =
      if bucket.token_span.start > new_token.token_span.start do
        put_in(bucket.token_span.start, new_token.token_span.start)
        |> put_in([:offset], new_token.offset)
        |> put_in(
          [:text],
          text_list
          |> Enum.slice(
            new_token.token_span.start,
            1 + bucket.token_span.end - new_token.token_span.start
          )
          |> Enum.join(" ")
        )
      else
        bucket
      end

    # if the new token ends after the existing bucket the existing bucket
    # has to be expanded in that direction and the text, offset and tokenspan
    # need to be updated
    # (token), [bucket] with [1(2]3)-> [1(23)] no overlapping borders! 
    bucket =
      if bucket.token_span.end < new_token.token_span.end do
        put_in(bucket.token_span.end, new_token.token_span.end)
        |> put_in(
          [:text],
          text_list
          |> Enum.slice(
            bucket.token_span.start,
            1 + new_token.token_span.end - bucket.token_span.start
          )
          |> Enum.join(" ")
        )
      else
        bucket
      end

    # check if the new bucket is now overlapping

    # check if the new_token is already present
    put_in(
      bucket.resource_candidates,
      solve_duplicate(new_token, bucket.resource_candidates)
    )
  end

  defp solve_duplicate(token, resource_candidates) do
    Enum.find_index(resource_candidates, fn cand ->
      String.equivalent?(cand.resource, token.resource)
    end)
    |> case do
      nil ->
        [token | resource_candidates]

      j ->
        {original_token, resource_candidates} = List.pop_at(resource_candidates, j)

        if original_token.confidence >= token.confidence do
          [original_token | resource_candidates]
        else
          [token | resource_candidates]
        end
    end
  end

  defp deduplicate(bucket) do
    update_in(
      bucket.resource_candidates,
      &Enum.uniq_by(&1, fn candidate ->
        candidate.resource
      end)
    )
  end

  def group_by_buckets(token_list, text, :candidates) do
    Enum.reduce(token_list, [], fn token, buckets ->
      insert_in_bucket(token, buckets, text, :candidates)
    end)
    |> Enum.map(&Task.async(fn -> deduplicate(&1) end))
    |> Enum.map(&Task.await/1)
  end

  def build_candidate(
        text,
        offset,
        token_start,
        token_end,
        resource,
        thumb,
        desc,
        confidence,
        source
      ) do
    %{
      text: text,
      offset: offset,
      token_span: %{
        start: token_start,
        end: token_end
      },
      resource: resource,
      thumbnail: thumb,
      description: desc,
      confidence: confidence,
      source: source
    }
  end
end
