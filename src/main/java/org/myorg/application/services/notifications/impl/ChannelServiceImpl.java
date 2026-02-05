package org.myorg.application.services.notifications.impl;

import org.myorg.application.exceptions.ResourceFoundException;
import org.myorg.application.exceptions.ResourceNotFoundException;
import org.myorg.application.models.notifications.Channel;
import org.myorg.application.services.notifications.ChannelService;
import org.myorg.application.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class ChannelServiceImpl implements ChannelService {

    private static final Logger             LOGGER =
            Logger.getLogger(ChannelServiceImpl.class.getName());
    private static       ChannelServiceImpl instance;

    private List<Channel> channels;
    private Long          id = 1L;

    private ChannelServiceImpl() {
        this.channels = new ArrayList<>();

        boostrap();
    }

    public static synchronized ChannelServiceImpl getInstance() {
        if (instance == null) {
            instance = new ChannelServiceImpl();
        }
        return instance;
    }

    private void boostrap() {
        try {
            for (Channel.ChannelType value : Channel.ChannelType.values()) {
                addChannel(Channel.builder().id(id).name(value.getName()).build());
            }
        } catch (Exception e) {
            LOGGER.severe("Error on channel boostrap: " + e.getMessage());
        }
    }

    public List<Channel> getEnabledChannels() {
        return channels.stream().toList();
    }

    public Channel findChannelByName(String channel) {

        Optional<Channel> optional = getEnabledChannels().stream()
                .filter(it -> it.getName().equalsIgnoreCase(channel)).findAny();

        if (optional.isEmpty()) {
            throw new ResourceNotFoundException("Channel " + channel + " does not exist");
        }

        return optional.get();
    }

    public Channel addChannel(Channel channel) {

        if (channel == null) {
            throw new ResourceNotFoundException("Channel can not be null");
        }

        if (channel.getName() == null || channel.getName().isBlank()) {
            throw new ResourceNotFoundException("Channel name is empty");
        }

        if (getEnabledChannels().stream()
                .noneMatch(it -> it.getName().equalsIgnoreCase(channel.getName()))) {

            channel.setId(id);
            channel.setName(CommonUtils.capitalizeEachWord(channel.getName().trim()));
            channels.add(channel);
            id++;

            return channel;
        } else {
            throw new ResourceFoundException(
                    "Channel " + channel.getName() + " already exists");
        }
    }

    public boolean removeChannel(String channel) {

        findChannelByName(channel);
        return channels.removeIf(it -> it.getName().equalsIgnoreCase(channel));
    }
}
