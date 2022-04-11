package io.github.raitraidma.microservice.product;

import com.atomikos.remoting.jaxrs.TransactionAwareRestContainerFilter;
import com.atomikos.remoting.taas.TransactionProvider;
import com.atomikos.remoting.twopc.AtomikosRestPort;
import com.atomikos.remoting.twopc.ParticipantsProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

@Component
@ApplicationPath("atomikos")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(TransactionAwareRestContainerFilter.class);
        register(ParticipantsProvider.class);
        register(TransactionProvider.class);
        register(AtomikosRestPort.class);
    }
}
