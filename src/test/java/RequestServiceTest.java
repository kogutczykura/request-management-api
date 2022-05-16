import com.application.data.Request;
import com.application.data.RequestDto;
import com.application.data.RequestState;
import com.application.exception.ServiceValidationException;
import com.application.repository.RequestRepository;
import com.application.service.RequestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class RequestServiceTest {

    private RequestService requestService;
    private RequestRepository requestRepository;

    @BeforeEach
    void setup() {
        requestRepository = mock(RequestRepository.class);
        requestService = new RequestService(requestRepository);
    }

    @Test
    public void createRequestWithoutName() {
        // Given
        RequestDto requestDto = prepare();
        requestDto.setName(null);
        BindingResult bindingResult = new BeanPropertyBindingResult(requestDto, "requestDto");

        // When, then
        ServiceValidationException ex = Assertions.assertThrows(ServiceValidationException.class, () -> {
            requestService.create(requestDto, bindingResult);
        });
        assertThat(ex.getErrors().get(0).getField()).isEqualTo("name");
    }

    @Test
    public void createRequestWithoutValue() {
        // Given
        RequestDto requestDto = prepare();
        requestDto.setValue(null);
        BindingResult bindingResult = new BeanPropertyBindingResult(requestDto, "requestDto");

        // When, then
        ServiceValidationException ex = Assertions.assertThrows(ServiceValidationException.class, () -> {
            requestService.create(requestDto, bindingResult);
        });
        assertThat(ex.getErrors().get(0).getField()).isEqualTo("value");
    }

    @Test
    public void createRequestWithoutNameAndValue() {
        // Given
        RequestDto requestDto = prepare();
        requestDto.setName(null);
        requestDto.setValue(null);
        BindingResult bindingResult = new BeanPropertyBindingResult(requestDto, "requestDto");

        // When, then
        ServiceValidationException ex = Assertions.assertThrows(ServiceValidationException.class, () -> {
            requestService.create(requestDto, bindingResult);
        });
        assertThat(ex.getErrors().get(0).getField()).isEqualTo("name");
        assertThat(ex.getErrors().get(1).getField()).isEqualTo("value");
    }

    @Test
    public void whenRequestIsPublished_thenValueCannotBeChanged() {
        // Given
        RequestDto requestDto = prepare();
        requestDto.setName(null);
        requestDto.setReasonRejection(null);
        requestDto.setState(null);
        BindingResult bindingResult = new BeanPropertyBindingResult(requestDto, "requestDto");
        Request requestReturnedFromRepo =  Request.builder()
                .name("dasdas")
                .value("rerereerrerer")
                .state(RequestState.PUBLISHED)
                .id(1L)
                .build();

        when(requestRepository.getById(requestDto.getId())).thenReturn(requestReturnedFromRepo);

        // When, then
        ServiceValidationException ex = Assertions.assertThrows(ServiceValidationException.class, () -> {
            requestService.update(requestDto, bindingResult);
        });

        assertThat(ex.getErrors().get(0).getField()).isEqualTo("value");
    }

    @Test
    public void updateRequestWithStatusVerifiedAndReasonRejected() {
        // Given
        RequestDto requestDto = prepare();
        requestDto.setState(RequestState.VERIFIED);
        BindingResult bindingResult = new BeanPropertyBindingResult(requestDto, "requestDto");
        Request requestReturnedFromRepo =  Request.builder()
                .name("dasdas")
                .value("rerereerrerer")
                .id(1L)
                .state(RequestState.CREATED)
                .build();

        when(requestRepository.getById(requestDto.getId())).thenReturn(requestReturnedFromRepo);

        // When, then
        ServiceValidationException ex = Assertions.assertThrows(ServiceValidationException.class, () -> {
            requestService.update(requestDto, bindingResult);
        });

        assertThat(ex.getErrors().get(0).getField()).isEqualTo("reasonRejection");
    }

    @Test
    public void removeRequestWithReason() {
        // Given
        RequestDto requestDto = prepare();
        requestDto.setState(RequestState.DELETED);
        requestDto.setValue(null);
        requestDto.setName(null);
        BindingResult bindingResult = new BeanPropertyBindingResult(requestDto, "requestDto");
        Request requestReturnedFromRepo =  Request.builder()
                .name("dasdas")
                .value("rerereerrerer")
                .id(1L)
                .state(RequestState.CREATED)
                .build();

        when(requestRepository.getById(requestDto.getId())).thenReturn(requestReturnedFromRepo);

        // When
        RequestDto updatedRequest = requestService.update(requestDto, bindingResult);

        // Then
        assertThat(updatedRequest.getState()).isEqualTo(RequestState.DELETED);
        assertThat(updatedRequest.getReasonRejection()).isNotEmpty();
        assertThat(updatedRequest.getValue()).isEqualTo("rerereerrerer");
    }



    private RequestDto prepare(){
        RequestDto requestDto = new RequestDto();
        requestDto.setReasonRejection("Because");
        requestDto.setState(RequestState.CREATED);
        requestDto.setId(1L);
        requestDto.setName("Request");
        requestDto.setValue("request one value");
        return requestDto;
    }
}
