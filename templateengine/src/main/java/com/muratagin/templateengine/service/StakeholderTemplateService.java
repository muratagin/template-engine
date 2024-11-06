package com.muratagin.templateengine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muratagin.templateengine.configuration.StakeholderTemplateConfig;
import com.muratagin.templateengine.entity.StakeholderTemplate;
import com.muratagin.templateengine.repository.StakeholderTemplateRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StakeholderTemplateService {

    private final StakeholderTemplateRepository stakeholderTemplateRepository;

    public void processRequest(HttpServletRequest request, UUID stakeholderId) throws Exception {

        Optional<StakeholderTemplate> stakeholderTemplateOptional = stakeholderTemplateRepository.findById(stakeholderId);

        if (stakeholderTemplateOptional.isEmpty()) {
            throw new Exception("Template not found for stakeholder: " + stakeholderId);
        }

        StakeholderTemplate stakeholderTemplate = stakeholderTemplateOptional.get();

        // Parse the template JSON
        ObjectMapper mapper = new ObjectMapper();
        StakeholderTemplateConfig stakeholderTemplateConfig = mapper.readValue(stakeholderTemplate.getTemplate(), StakeholderTemplateConfig.class);

        // Extract the source data from the incoming request
        Map<String, Object> sourceData = extractDataFromRequest(request);

        // Map the source data to target data using SpEL
        Map<String, Object> targetBody = mapData(stakeholderTemplateConfig.getBodyMapping(), sourceData);
        Map<String, Object> targetHeaders = mapData(stakeholderTemplateConfig.getHeaderMapping(), sourceData);

        // Build the target request
        String targetEndpoint = stakeholderTemplateConfig.getTargetEndpoint();
        String targetRequestType = stakeholderTemplateConfig.getTargetRequestType();

        sendHttpRequest(targetEndpoint, targetRequestType, targetHeaders, targetBody);
    }

    private Map<String, Object> extractDataFromRequest(HttpServletRequest request) throws IOException {

        Map<String, Object> data = new HashMap<>();

        // Extract headers
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            data.put(headerName, request.getHeader(headerName));
        }

        // Extract body
        String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        if (!body.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> bodyData = mapper.readValue(body, Map.class);
            data.putAll(bodyData);
        }

        // Also add parameters (if any)
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String key : parameterMap.keySet()) {
            String[] values = parameterMap.get(key);
            data.put(key, values.length == 1 ? values[0] : Arrays.asList(values));
        }

        return data;
    }

    private Map<String, Object> mapData(Map<String, String> mappingRules, Map<String, Object> sourceData) {

        Map<String, Object> targetData = new HashMap<>();

        ExpressionParser parser = new SpelExpressionParser();

        StandardEvaluationContext context = new StandardEvaluationContext();

        // Set variables in context
        for (Map.Entry<String, Object> entry : sourceData.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, String> entry : mappingRules.entrySet()) {
            String targetField = entry.getKey();
            String expressionString = entry.getValue();

            Expression expression = parser.parseExpression(expressionString);

            Object value = expression.getValue(context);

            targetData.put(targetField, value);
        }

        return targetData;
    }

    private void sendHttpRequest(String endpoint, String method, Map<String, Object> headers, Map<String, Object> body) throws Exception {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                httpHeaders.add(entry.getKey(), entry.getValue() == null ? null : entry.getValue().toString());
            }
        }

        // Set content type to JSON
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> requestEntity = null;
        if (body != null && !body.isEmpty()) {
            requestEntity = new HttpEntity<>(body, httpHeaders);
        } else {
            requestEntity = new HttpEntity<>(httpHeaders);
        }

        ResponseEntity<String> responseEntity;
        try {
            if ("POST".equalsIgnoreCase(method)) {
                responseEntity = restTemplate.exchange(endpoint, HttpMethod.POST, requestEntity, String.class);
            } else if ("GET".equalsIgnoreCase(method)) {
                responseEntity = restTemplate.exchange(endpoint, HttpMethod.GET, requestEntity, String.class);
            } else {
                throw new Exception("Unsupported method: " + method);
            }
        } catch (Exception e) {
            throw new Exception("Cannot call target endpoint");
        }

        // Handle the response as needed
        System.out.println("Response from target endpoint: " + responseEntity.getBody());
    }
}
