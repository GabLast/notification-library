package services.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.myorg.application.exceptions.ResourceNotFoundException;
import org.myorg.application.models.notifications.Channel;
import org.myorg.application.services.notifications.ChannelService;
import org.myorg.application.services.notifications.impl.ChannelServiceImpl;

public class ChannelServiceTest {

    private final ChannelService channelService = ChannelServiceImpl.getInstance();

    @Test
    void getEnabledChannels() {
        Assertions.assertTrue(!channelService.getEnabledChannels().isEmpty());
    }

    @Test
    void findChannelByName() {
        Assertions.assertNotNull(channelService.findChannelByName("SMS"));
        Assertions.assertThrows(ResourceNotFoundException.class,() -> channelService.findChannelByName("aaaaaaaaaaaaa"));
    }

    @Test
    void addChannel() {
        Assertions.assertNotNull(channelService.addChannel(Channel.builder().name("Channel").build()));

        //to restore channel list state
        channelService.removeChannel("Channel");

        Assertions.assertThrows(ResourceNotFoundException.class, () ->
                channelService.addChannel(Channel.builder().build()));
        Assertions.assertThrows(ResourceNotFoundException.class, () ->
                channelService.addChannel(null));
    }

    @Test
    void removeChannel() {
        Assertions.assertNotNull(channelService.addChannel(Channel.builder().name("Channel").build()));
        Assertions.assertTrue(channelService.removeChannel("Channel"));
        Assertions.assertThrows(ResourceNotFoundException.class,() -> channelService.removeChannel("aaaaaaaaaaaaa"));
    }
}
