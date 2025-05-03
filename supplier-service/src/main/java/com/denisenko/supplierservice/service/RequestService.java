package com.denisenko.supplierservice.service;

import com.denisenko.supplierservice.model.RequestLog;
import com.denisenko.supplierservice.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;

    public void saveAll(List<RequestLog> requests) {
        requestRepository.saveAll(requests);
    }

    public Optional<RequestLog> getRequest(String requestId) {
        return requestRepository.findByRequestId(requestId);
    }
}
