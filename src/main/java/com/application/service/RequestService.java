package com.application.service;

import com.application.data.*;
import com.application.exception.ServiceValidationException;
import com.application.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.application.service.RequestMapper.fromDto;
import static com.application.service.RequestMapper.toDto;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;

    public RequestDto getRequest(Long id) {
        Optional<Request> request = requestRepository.findById(id);
        return toDto(request.orElseGet(Request::new));
    }

    public PageAndDtoList findAll(String name, int page, int size) {
        PageAndDtoList pageAndDtoList = new PageAndDtoList();
        List<Request> requests;
        Pageable paging = PageRequest.of(page, size);

        Page<Request> pageRequest;
        if (name == null) {
            pageRequest = requestRepository.findAll(paging);
        } else {
            pageRequest = requestRepository.findAllByName(name, paging);
        }
        requests = pageRequest.getContent();
        List<RequestDto> requestDtos;
        requestDtos = requests.stream().map(RequestMapper::toDto).collect(Collectors.toList());
        pageAndDtoList.setRequestPage(pageRequest);
        pageAndDtoList.setRequestDtos(requestDtos);
        return pageAndDtoList;
    }

    public PageAndDtoList findAllWithState(RequestState requestState, int page, int size) {
        PageAndDtoList pageAndDtoList = new PageAndDtoList();
        List<Request> requests;
        Pageable paging = PageRequest.of(page, size);

        Page<Request> pageRequest;
        if (requestState == null) {
            pageRequest = requestRepository.findAll(paging);
        } else {
            pageRequest = requestRepository.findAllByState(requestState, paging);
        }
        requests = pageRequest.getContent();
        List<RequestDto> requestDtos;
        requestDtos = requests.stream().map(RequestMapper::toDto).collect(Collectors.toList());
        pageAndDtoList.setRequestPage(pageRequest);
        pageAndDtoList.setRequestDtos(requestDtos);
        return pageAndDtoList;
    }

    public RequestDto create(RequestDto requestDto, BindingResult bindingResult) {
        if (requestDto.getName() == null) {
            bindingResult.rejectValue("name", "NotNull", "Name of request cannot be empty");
        }
        if (requestDto.getValue() == null) {
            bindingResult.rejectValue("value", "NotNull", "Value of request cannot be empty");
        }
        if (requestDto.getReasonRejection() != null) {
            bindingResult.rejectValue("reasonRejection", "ShouldBeNull", "This value should be empty");
        }
        if (bindingResult.hasErrors()) {
            throw new ServiceValidationException(bindingResult);
        }

        requestDto.setState(RequestState.CREATED);
        Request request = fromDto(requestDto, new Request());

        RequestStateHistory requestStateHistory = new RequestStateHistory();
        requestStateHistory.setRequest(request);
        requestStateHistory.setRequestState(request.getState());
        request.getRequestStateHistories().add(requestStateHistory);

        requestRepository.save(request);
        return toDto(request);
    }

    @Transactional
    public RequestDto update(RequestDto requestDto, BindingResult bindingResult) {
        Request request = requestRepository.getById(requestDto.getId());

        if (!canValueChange(request, requestDto) && doesValueChanged(requestDto, request)) {
            bindingResult.rejectValue("value", "NotPossibleToChange", "Value could not be changed. Check if request has correct status");
        }

        if (requestDto.getId() != null) {
            request.setId(requestDto.getId());
        }

        if (statusCannotBeChanged(request, requestDto)) {
            bindingResult.rejectValue("state", "BadStatus", "Illegitimate status change");
        }

        if (requestCannotBeRejected(requestDto)) {
            bindingResult.rejectValue("reasonRejection", "NotNullOrBadStatus", "Check if the reason matches the states");
        }

        if (requestDto.getState() == RequestState.PUBLISHED) {
            PublishedRequest publishedRequest = new PublishedRequest();
            request.setPublishedNumber(publishedRequest);
        }

        if (bindingResult.hasErrors()) {
            throw new ServiceValidationException(bindingResult);
        }

        if(requestDto.getState() != null && requestDto.getState() != request.getState()) {
            RequestStateHistory requestStateHistory = new RequestStateHistory();
            requestStateHistory.setRequest(request);
            requestStateHistory.setRequestState(requestDto.getState());
            request.getRequestStateHistories().add(requestStateHistory);
        }

        fromDto(requestDto, request);
        return toDto(request);
    }

    private boolean canValueChange(Request request, RequestDto requestDto) {
        if (request.getState() == RequestState.CREATED && (requestDto.getState() == RequestState.CREATED
                || requestDto.getState() == RequestState.VERIFIED)) {
            return true;
        }
        return request.getState() == RequestState.VERIFIED && requestDto.getState() == RequestState.VERIFIED;
    }

    private boolean doesValueChanged(RequestDto requestDto, Request request) {
        if (requestDto.getValue() == null && request.getValue() != null) {
            requestDto.setValue(request.getValue());
        }
        return !StringUtils.equals(request.getValue(), requestDto.getValue());
    }

    private boolean statusCannotBeChanged(Request request, RequestDto requestDto) {
        boolean createdState = request.getState() == RequestState.CREATED;
        boolean verifiedState = request.getState() == RequestState.VERIFIED;
        boolean acceptedState = request.getState() == RequestState.ACCEPTED;
        boolean createdCanBeChangedTo = requestDto.getState() == RequestState.DELETED
                || requestDto.getState() == RequestState.VERIFIED;
        boolean verifiedCanBeChangedTo = requestDto.getState() == RequestState.REJECTED
                || requestDto.getState() == RequestState.ACCEPTED;
        boolean acceptedCanBeChangedTo = requestDto.getState() == RequestState.REJECTED
                || requestDto.getState() == RequestState.PUBLISHED;

        boolean stateCanBeChanged = (createdState && createdCanBeChangedTo)
                || (verifiedState && verifiedCanBeChangedTo)
                || (acceptedState && acceptedCanBeChangedTo);
        return !stateCanBeChanged && requestDto.getState() != null;
    }

    private boolean requestCannotBeRejected(RequestDto requestDto) {
        boolean hasRejectedReason = requestDto.getReasonRejection() != null;
        boolean canBeRejected = requestDto.getState() == RequestState.DELETED
                || requestDto.getState() == RequestState.REJECTED;
        return hasRejectedReason && !canBeRejected;
    }
}
