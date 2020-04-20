package de.fuberlin.innovonto.utils.icvannotationappbackend.model;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface IdeaRepository  extends CrudRepository<Idea, UUID> {
}
