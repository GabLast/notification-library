package org.myorg.application.services.notifications.impl;

import lombok.Getter;
import org.myorg.application.exceptions.InvalidDataFormat;
import org.myorg.application.models.notifications.Channel;
import org.myorg.application.models.notifications.ChannelProvider;
import org.myorg.application.models.notifications.Provider;
import org.myorg.application.services.notifications.ChannelProviderService;
import org.myorg.application.services.notifications.ChannelService;
import org.myorg.application.services.notifications.ProviderService;
import org.myorg.application.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ChannelProviderServiceImpl implements ChannelProviderService {

    private static final Logger                     LOGGER =
            Logger.getLogger(ChannelProviderServiceImpl.class.getName());
    private static       ChannelProviderServiceImpl instance;
    @Getter
    private              List<ChannelProvider>      configuredChannelProviders;
    private              ChannelService             channelService;
    private              ProviderService            providerService;

    private ChannelProviderServiceImpl() {
        this.configuredChannelProviders = new ArrayList<>();
        boostrap();
    }

    //synchronized to ensure thread safety
    public static synchronized ChannelProviderServiceImpl getInstance() {
        if (instance == null) {
            instance = new ChannelProviderServiceImpl();
        }
        return instance;
    }

    private void boostrap() {

        channelService = ChannelServiceImpl.getInstance();
        providerService = ProviderServiceImpl.getInstance();

        //configuring Twilio - has all channels (SMS, EMAIL, PUSH_NOTIFICATION, SLACK...)
        Provider twilio = providerService.findProviderByName(
                Provider.ProviderTypes.TWILIO.getName());

        Channel sms = channelService.findChannelByName(Channel.ChannelType.SMS.getName());
        Channel email =
                channelService.findChannelByName(Channel.ChannelType.EMAIL.getName());
        Channel push = channelService.findChannelByName(
                Channel.ChannelType.PUSH_NOTIFICATION.getName());
        Channel slack =
                channelService.findChannelByName(Channel.ChannelType.SLACK.getName());

        configureChannelProvider(sms, twilio, CommonUtils.generateApiKey(), null);
        configureChannelProvider(email, twilio, CommonUtils.generateApiKey(), null);
        configureChannelProvider(push, twilio, CommonUtils.generateApiKey(), null);
        configureChannelProvider(slack, twilio, CommonUtils.generateApiKey(), null);

        //configuring Mailgun - only email)
        Provider mailgun = providerService.findProviderByName(
                Provider.ProviderTypes.MAILGUN.getName());
        configureChannelProvider(email, mailgun, CommonUtils.generateApiKey(), null);
    }

    public ChannelProvider configureChannelProvider(Channel channel, Provider provider, String apiKey, String webhook) {

        if(webhook != null && !webhook.isBlank() && !CommonUtils.isValidUrl(webhook)) {
            throw new InvalidDataFormat("Configuration webhook is not a valid URL");
        }

        ChannelProvider config =
                ChannelProvider.builder().channel(channel).provider(provider)
                        .apiKey(apiKey)
                        .webhook(webhook)
                        .build();

        configuredChannelProviders.removeIf(
                it -> it.getChannel().getName().equalsIgnoreCase(channel.getName()) && it
                        .getProvider().getName().equalsIgnoreCase(provider.getName()));

        configuredChannelProviders.add(config);

        return config;
    }

}
