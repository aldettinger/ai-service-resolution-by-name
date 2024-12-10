package org.acme;

import java.util.Set;

import io.quarkus.arc.Arc;
import io.quarkus.arc.impl.ArcContainerImpl;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Attempt to reproduce the behavior debugged in camel.
 * I guess main questions are:
 *
 * 1) Shouldn't manager.getBeans("AiServiceResolvedByName") returns an instance of AiServiceResolvedByName$$QuarkusImpl ?
 * 2) The bean of type QuarkusAiServiceContext from ArcContainerImpl.beans should have no name ?
 * 3) The bean of type AiServiceResolvedByName$$QuarkusImpl should have name "AiServiceResolvedByName" instead ?
 * 4) Do we need to generate a synthetic injection point from camel-quarkus-langchain4j extension ?
 */
@Path("/hello")
public class GreetingResource {

    @Inject
    BeanManager manager;

    /*
     * In camel, there is currently no injection point.
     * We could probably tune the camel-quarkus-langchain4j extension to add a synthetic one.
     * So, uncommenting lines below could simulate this situation.
     *
     * With this injection, ArcContainerImpl.beans now contains the following QuarkusImpl:
     * CLASS bean [class=org.acme.AiServiceResolvedByName$$QuarkusImpl, id=Yh_MlBZKfzUclKc6psPcaqHnfZc]
     * However, the AiServiceResolvedByName.getName() returns null so the bean is not matched by name
     */
    //@Inject
    //AiServiceResolvedByName service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {

        // Returns same as in camel [SYNTHETIC bean [class=io.quarkiverse.langchain4j.runtime.aiservice.QuarkusAiServiceContext, id=TYpRs3KmsFjdSvyqkIN1qVZuAoI]]
        // Debugging this call, we can see ArcContainerImpl.beans by stopping in ArcContainerImpl.getMatchingBeans(String name)
        Set<Bean<?>> beans = manager.getBeans("aiServiceResolvedByName");

        // For the sake of illustration, camel then does something similar as below
        // Not sure it's relevant for this reproducer though
        Bean<?> bean = manager.resolve(beans);
        CreationalContext<?> ctx = manager.createCreationalContext(bean);

        Object reference = manager.getReference(bean, Object.class, ctx);

        if(reference instanceof AiServiceResolvedByName) {
            return ((AiServiceResolvedByName) reference).chat("dummy input");
        }

        return "The resolved bean was not of type AiServiceResolvedByName";
    }
}
