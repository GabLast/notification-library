package org.myorg.application.services.notifications;

import org.myorg.application.models.notifications.Channel;
import org.myorg.application.models.notifications.ChannelProvider;
import org.myorg.application.models.notifications.Provider;

import java.util.List;

public interface ChannelProviderService {
    public ChannelProvider configureChannelProvider(Channel channel, Provider provider, String apiKey, String webhook);
    public List<ChannelProvider> getConfiguredChannelProviders();
}
