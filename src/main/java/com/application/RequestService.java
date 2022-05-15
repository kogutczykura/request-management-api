package com.application;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.application.RequestMapper.fromDto;
import static com.application.RequestMapper.toDto;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;

    public RequestDto getRequest(Long id) {
        Optional<Request> request = requestRepository.findById(id);
        return toDto(request.orElseGet(Request::new));
    }

    public List<RequestDto> findAll() {
        List<RequestDto> requestDtos;
        List<Request> requests = requestRepository.findAll();
        requestDtos = requests.stream().map(RequestMapper::toDto).collect(Collectors.toList());
        return requestDtos;
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

        requestRepository.save(request);
        return toDto(request);
    }

    @Transactional
    public RequestDto update(RequestDto requestDto, BindingResult bindingResult) {
        Request request = requestRepository.getById(requestDto.getId());

        boolean canNameAndValueChange = request.getState() == RequestState.CREATED || request.getState() == RequestState.VERIFIED;
        boolean doesNameOrValueChanged = !StringUtils.equals(request.getValue(), requestDto.getValue()) || !StringUtils.equals(request.getName(), requestDto.getName());
        if (!canNameAndValueChange && doesNameOrValueChanged) {
            bindingResult.rejectValue("value", "NotPossibleToChange", "Value could not be changed. Check if request has correct status");
        }

        if (requestDto.getId() != null) {
            request.setId(requestDto.getId());
        }

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
        if (!stateCanBeChanged && requestDto.getState() != null) {
            bindingResult.rejectValue("state", "BadStatus", "Illegitimate status change");
        }

        boolean hasRejectedReason = requestDto.getReasonRejection() != null;
        boolean canBeRejected = requestDto.getState() == RequestState.DELETED
                || requestDto.getState() == RequestState.REJECTED;
        if (hasRejectedReason && !canBeRejected) {
            bindingResult.rejectValue("reasonRejection", "NotNullOrBadStatus", "Check if the reason matches the states");
        }

        if (bindingResult.hasErrors()) {
            throw new ServiceValidationException(bindingResult);
        }

        fromDto(requestDto, request);
        return toDto(request);
    }
}
