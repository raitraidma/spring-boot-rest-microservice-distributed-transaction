package io.github.raitraidma.microservice.common.filter;


import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.atomikos.icatch.RollbackException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.remoting.support.ContainerInterceptorTemplate;
import com.atomikos.remoting.support.HeaderNames;

/**
 * Copy of com.atomikos.remoting.spring.rest.TransactionAwareRestContainerFilter
 * with some modifications.
 */
public class TransactionAwareRestContainerFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.createLogger(com.atomikos.remoting.spring.rest.TransactionAwareRestContainerFilter.class);

    private ContainerInterceptorTemplate template = new ContainerInterceptorTemplate();

    @Override
    protected void doFilterInternal(HttpServletRequest request, final HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String propagation = request.getHeader(HeaderNames.PROPAGATION_HEADER_NAME);

        try {
            template.onIncomingRequest(propagation);
            HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(response) {
                @Override
                public void setStatus(int sc) {
                    super.setStatus(sc);
                    String extent = terminateImportedTransaction(sc);
                    response.addHeader(HeaderNames.EXTENT_HEADER_NAME, extent);
                }

                @Override
                public int getStatus() {
                    int status = super.getStatus();
                    String extent = terminateImportedTransaction(status);
                    response.addHeader(HeaderNames.EXTENT_HEADER_NAME, extent);
                    return status;
                }
            };
            filterChain.doFilter(request, wrapper);
        } catch (IllegalStateException e) {
            LOGGER.logWarning("Detected invalid incoming transaction - aborting...");
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        }


    }

    private String terminateImportedTransaction(int httpCode)  {
        String extent = null;
        try {
            if(HttpStatus.valueOf(httpCode).is2xxSuccessful()) {
                extent = template.onOutgoingResponse(false);
            } else {
                extent = template.onOutgoingResponse(true);
            }

        } catch (RollbackException e) {
            String msg = "Transaction was rolled back - probably due to a timeout?";
            LOGGER.logWarning(msg, e);
        } catch (Exception e) {
            String msg = "Unexpected error while terminating transaction";
            LOGGER.logError(msg, e);
        }
        return extent;
    }
}