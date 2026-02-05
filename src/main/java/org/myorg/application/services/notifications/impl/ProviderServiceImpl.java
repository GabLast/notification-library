package org.myorg.application.services.notifications.impl;

import org.myorg.application.exceptions.ResourceFoundException;
import org.myorg.application.exceptions.ResourceNotFoundException;
import org.myorg.application.models.notifications.Provider;
import org.myorg.application.services.notifications.ProviderService;
import org.myorg.application.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class ProviderServiceImpl implements ProviderService {

    private static final Logger              LOGGER =
            Logger.getLogger(ProviderServiceImpl.class.getName());
    private static       ProviderServiceImpl instance;

    private List<Provider> providers;
    private Long           id = 1L;

    private ProviderServiceImpl() {
        this.providers = new ArrayList<>();

        boostrap();
    }

    public static synchronized ProviderServiceImpl getInstance() {
        if (instance == null) {
            instance = new ProviderServiceImpl();
        }
        return instance;
    }

    private void boostrap() {
        try {
            for (Provider.ProviderTypes value : Provider.ProviderTypes.values()) {
                addProvider(Provider.builder().id(id).name(value.getName()).build());
            }
        } catch (Exception e) {
            LOGGER.severe("Error on provider boostrap: " + e.getMessage());
        }
    }

    public List<Provider> getEnabledProviders() {
        return providers.stream().toList();
    }

    public Provider findProviderByName(String name) {

        Optional<Provider> optional = getEnabledProviders().stream()
                .filter(it -> it.getName().equalsIgnoreCase(name)).findAny();

        if (optional.isEmpty()) {
            throw new ResourceNotFoundException("Provider " + name + " does not exist");
        }

        return optional.get();
    }

    public Provider addProvider(Provider provider) {

        if (provider == null) {
            throw new ResourceNotFoundException("Provider can not be null");
        }

        if (provider.getName() == null || provider.getName().isBlank()) {
            throw new ResourceNotFoundException("Provider name is empty");
        }

        if (getEnabledProviders().stream()
                .noneMatch(it -> it.getName().equalsIgnoreCase(provider.getName()))) {

            provider.setId(id);
            provider.setName(CommonUtils.capitalizeEachWord(provider.getName().trim()));
            providers.add(provider);
            id++;

            return provider;
        } else {
            throw new ResourceFoundException(
                    "Provider " + provider.getName() + " already exists");
        }
    }

    public boolean removeProvider(String name) {

        findProviderByName(name);
        return providers.removeIf(it -> it.getName().equalsIgnoreCase(name));
    }

}
