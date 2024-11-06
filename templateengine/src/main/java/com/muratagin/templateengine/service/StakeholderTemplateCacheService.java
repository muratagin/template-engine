package com.muratagin.templateengine.service;

import com.muratagin.templateengine.entity.StakeholderTemplate;
import com.muratagin.templateengine.repository.StakeholderTemplateRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class StakeholderTemplateCacheService {

    private final StakeholderTemplateRepository stakeholderTemplateRepository;

    @Cacheable(value = "templates", key = "#id")
    public StakeholderTemplate getStakeholderTemplateById(UUID id) throws Exception {

        Optional<StakeholderTemplate> stakeholderTemplateOptional = stakeholderTemplateRepository.findById(id);

        if (stakeholderTemplateOptional.isEmpty()) {
            throw new Exception("Stakeholder Template not found for ID: " + id);
        }

        return stakeholderTemplateOptional.get();
    }
}
