package services.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.myorg.application.exceptions.ResourceNotFoundException;
import org.myorg.application.models.notifications.Provider;
import org.myorg.application.services.notifications.ProviderService;
import org.myorg.application.services.notifications.impl.ProviderServiceImpl;

public class ProviderServiceTest {

    private final ProviderService providerService = ProviderServiceImpl.getInstance();

    @Test
    void getEnabledProviders() {
        Assertions.assertTrue(!providerService.getEnabledProviders().isEmpty());
    }

    @Test
    void findProviderByName() {
        Assertions.assertNotNull(providerService.findProviderByName("Twilio"));
        Assertions.assertThrows(ResourceNotFoundException.class,() -> providerService.findProviderByName("aaaaaaaaaaaaa"));
    }

    @Test
    void addProvider() {
        Assertions.assertNotNull(
                providerService.addProvider(Provider.builder().name("Provider").build()));

        Assertions.assertThrows(ResourceNotFoundException.class, () ->
                providerService.addProvider(Provider.builder().build()));
        Assertions.assertThrows(ResourceNotFoundException.class, () ->
                providerService.addProvider(null));
    }

    @Test
    void removeProvider() {
        Assertions.assertTrue(providerService.removeProvider("Twilio"));
        Assertions.assertThrows(ResourceNotFoundException.class,() -> providerService.removeProvider("aaaaaaaaaaaaa"));
    }
}
