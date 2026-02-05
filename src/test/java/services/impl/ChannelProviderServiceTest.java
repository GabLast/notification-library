package services.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.myorg.application.exceptions.InvalidDataFormat;
import org.myorg.application.services.notifications.ChannelProviderService;
import org.myorg.application.services.notifications.ChannelService;
import org.myorg.application.services.notifications.ProviderService;
import org.myorg.application.services.notifications.impl.ChannelProviderServiceImpl;
import org.myorg.application.services.notifications.impl.ChannelServiceImpl;
import org.myorg.application.services.notifications.impl.ProviderServiceImpl;
import org.myorg.application.utils.CommonUtils;

public class ChannelProviderServiceTest {

    private final ChannelProviderService channelProviderService =
            ChannelProviderServiceImpl.getInstance();
    private final ChannelService         channelService         =
            ChannelServiceImpl.getInstance();
    private final ProviderService        providerService        =
            ProviderServiceImpl.getInstance();

    @Test
    void configureChannelProvider() {
        Assertions.assertNotNull(channelProviderService.configureChannelProvider(
                channelService.findChannelByName("SMS"),
                providerService.findProviderByName("Twilio"),
                CommonUtils.generateApiKey(), null));

        Assertions.assertThrows(InvalidDataFormat.class,
                () -> channelProviderService.configureChannelProvider(
                        channelService.findChannelByName("SMS"),
                        providerService.findProviderByName("Twilio"),
                        CommonUtils.generateApiKey(), "invalid url"));
    }
}
