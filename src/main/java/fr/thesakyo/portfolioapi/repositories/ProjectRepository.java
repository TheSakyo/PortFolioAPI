package fr.thesakyo.portfolioapi.repositories;

import fr.thesakyo.portfolioapi.models.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {}
