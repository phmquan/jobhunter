package vn.uit.jobhunter.service.rag_service;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import vn.uit.jobhunter.domain.response.ResEmbeddingJobDes;

@Component
public class PythonBackendService {
    public void sendJobToPythonBackend(ResEmbeddingJobDes job)  {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        
        
        HttpEntity<ResEmbeddingJobDes> request = new HttpEntity<>(job, headers);
        String pythonBackendUrl = "http://localhost:8000/embed-job-requirement/";
        ResponseEntity<String> response = restTemplate.postForEntity(pythonBackendUrl, request, String.class);
        System.out.println("Response from Python backend: " + response.getBody());
       
    }
}
