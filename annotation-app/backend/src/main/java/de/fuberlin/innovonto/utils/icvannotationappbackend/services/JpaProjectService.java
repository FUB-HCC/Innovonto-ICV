package de.fuberlin.innovonto.utils.icvannotationappbackend.services;

import de.fuberlin.innovonto.utils.batchmanager.api.Project;
import de.fuberlin.innovonto.utils.batchmanager.api.ProjectService;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.AnnotationProject;
import de.fuberlin.innovonto.utils.icvannotationappbackend.model.AnnotationProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaProjectService implements ProjectService<AnnotationProject> {
    private final AnnotationProjectRepository projectRepository;

    @Autowired
    public JpaProjectService(AnnotationProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public Optional<AnnotationProject> findById(String projectId) {
        return projectRepository.findById(projectId);
    }

    @Override
    public AnnotationProject save(AnnotationProject project) {
        return projectRepository.save(project);
    }
}
