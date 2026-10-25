package chemilabi.trade.api_gateway.exception;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;

import java.time.OffsetDateTime;
import java.util.Map;

@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {

    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                          WebProperties webProperties,
                                          ServerProperties serverProperties,
                                          ApplicationContext applicationContext,
                                          ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes,
                webProperties.getResources(),
                serverProperties.getError(),
                applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    @NonNull
    protected Map<String, Object> getErrorAttributes(@NonNull ServerRequest request,
                                                     @NonNull ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);

        Throwable error = getError(request);
        HttpStatus status = determineHttpStatus(errorAttributes);

        String message = (error != null && error.getMessage() != null)
                ? error.getMessage()
                : "Resource Not Found";

        if (status == HttpStatus.NOT_FOUND) {
            message = "Resource Not Found";
        }

        return Map.of(
                "status", status.value(),
                "message", message,
                "timestamp", OffsetDateTime.now().toString(),
                "path", request.path()
        );
    }

    @Override
    @NonNull
    protected RouterFunction<ServerResponse> getRoutingFunction(@NonNull ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private HttpStatus determineHttpStatus(Map<String, Object> errorAttributes) {
        Integer status = (Integer) errorAttributes.get("status");
        return status != null ? HttpStatus.valueOf(status) : HttpStatus.INTERNAL_SERVER_ERROR;
    }
}