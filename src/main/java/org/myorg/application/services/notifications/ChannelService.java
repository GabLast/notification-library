package org.myorg.application.services.notifications;

import org.myorg.application.models.notifications.Channel;

import java.util.List;

public interface ChannelService {
    public List<Channel> getEnabledChannels();
    public Channel findChannelByName(String channel);
    public Channel addChannel(Channel channel);
    public boolean removeChannel(String channel);

}
