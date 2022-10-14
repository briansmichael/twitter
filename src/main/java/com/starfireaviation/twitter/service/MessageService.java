/*
 *  Copyright (C) 2022 Starfire Aviation, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.starfireaviation.twitter.service;

import com.starfireaviation.model.Event;
import com.starfireaviation.model.EventType;
import com.starfireaviation.model.Message;
import com.starfireaviation.twitter.config.ApplicationProperties;
import com.starfireaviation.twitter.util.TemplateUtil;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.time.Instant;

/**
 * MessageService.
 */
@Slf4j
public class MessageService {

    /**
     * FreeMarker Configuration.
     */
    private final Configuration freemarkerConfig;

    /**
     * ApplicationProperties.
     */
    private final ApplicationProperties applicationProperties;

    /**
     * TwitterService.
     *
     * @param aProps ApplicationProperties
     * @param config Configuration
     */
    public MessageService(final ApplicationProperties aProps,
                          final Configuration config) {
        freemarkerConfig = config;
        applicationProperties = aProps;
    }

    /**
     * Sends a last minute message to register/RSVP for an upcoming event.
     *
     * @param message Message
     */
    public void sendEventLastMinRegistrationMsg(final Message message) {
        if (!applicationProperties.isEnabled()) {
            return;
        }
        final Event event = getEvent(message);
        try {
            freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/templates/twitter");

            final String msg = FreeMarkerTemplateUtils.processTemplateIntoString(
                    freemarkerConfig.getTemplate("event_last_min_registration.ftl"),
                    TemplateUtil.getModel(null, event, null, applicationProperties));
            tweet(msg);
        } catch (IOException | TemplateException e) {
            log.warn(e.getMessage());
        }
    }

    /**
     * Sends a message for an upcoming event.
     *
     * @param event Event
     */
    public void sendEventUpcomingMsg(final Event event) {
        if (!applicationProperties.isEnabled()) {
            return;
        }
        try {
            freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/templates/twitter");
            if (event.getEventType() == EventType.GROUNDSCHOOL) {
                final String message = FreeMarkerTemplateUtils.processTemplateIntoString(
                        freemarkerConfig.getTemplate("gs_event_upcoming.ftl"),
                        TemplateUtil.getModel(null, event, null, applicationProperties));
                tweet(message);
            }
        } catch (IOException | TemplateException e) {
            log.warn(e.getMessage());
        }
    }

    /**
     * Sends Tweet.
     *
     * @param message to be sent
     */
    private void tweet(final String message) {
        final Instant start = Instant.now();
        try {
            final TwitterTemplate twitter = new TwitterTemplate(
                    applicationProperties.getConsumerKey(),
                    applicationProperties.getConsumerSecret(),
                    applicationProperties.getAccessToken(),
                    applicationProperties.getAccessTokenSecret());
            twitter.timelineOperations().updateStatus(message);
        } catch (RuntimeException ex) {
            log.error("Unable to tweet " + message, ex);
        }
    }

    private Event getEvent(final Message message) {
        return null;
    }

}
