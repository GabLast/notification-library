package org.myorg.application.services.notifications;

import org.myorg.application.models.notifications.Provider;

import java.util.List;

public interface ProviderService {
    public List<Provider> getEnabledProviders();
    public Provider findProviderByName(String name);
    public Provider addProvider(Provider provider);
    public boolean removeProvider(String name);
}
