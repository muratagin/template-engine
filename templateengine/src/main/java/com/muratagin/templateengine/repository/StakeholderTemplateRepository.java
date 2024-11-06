package com.muratagin.templateengine.repository;

import com.muratagin.templateengine.entity.StakeholderTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StakeholderTemplateRepository extends JpaRepository<StakeholderTemplate, UUID> {
}
