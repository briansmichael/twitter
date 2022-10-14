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

package com.starfireaviation.twitter.util;

import com.starfireaviation.model.Address;
import com.starfireaviation.model.Answer;
import com.starfireaviation.model.Event;
import com.starfireaviation.model.Question;
import com.starfireaviation.model.ReferenceMaterial;
import com.starfireaviation.model.User;
import com.starfireaviation.twitter.config.ApplicationProperties;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TemplateUtil.
 */
public class TemplateUtil {

    /**
     * DateTimeFormatter.
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("EEE MMM dd, yyyy hh:mm a");

    /**
     * ZoneId.
     */
    private static final ZoneId ET_ZONE_ID = ZoneId.of("America/New_York");

    /**
     * SimpleDateFormat - Day of Week.
     */
    private static final SimpleDateFormat DAY_OF_WEEK = new SimpleDateFormat("EEEE");

    /**
     * Builds model for use in templates.
     *
     * @param user                  User
     * @param event                 Event
     * @param question              Question
     * @param applicationProperties ApplicationProperties
     * @return model
     */
    public static Map<String, Object> getModel(
            final User user,
            final Event event,
            final Question question,
            final ApplicationProperties applicationProperties) {
        Map<String, Object> model = new HashMap<>();
        // TODO property file this value
        model.put("groundSchoolLink", "https://groundschool.starfireaviation.com");
        model.put("groundSchoolPasswordResetLink", "");
        buildUserModel(user, model);
        buildEventModel(event, model);
        buildQuestionModel(question, model);
        return model;
    }

    /**
     * Builds Question portions of model for use in templates.
     *
     * @param question Question
     * @param model    Map
     */
    private static void buildQuestionModel(final Question question, final Map<String, Object> model) {
        if (question != null) {
            model.put("questionUnit", question.getUnit());
            model.put("questionSubUnit", question.getSubUnit());
            model.put("questionLearningStatementCode", question.getLearningStatementCode());
            model.put("questionText", question.getText());
            model.put("callbackId", "question");
            final List<ReferenceMaterial> referenceMaterials = question.getReferenceMaterials();
            if (referenceMaterials != null && referenceMaterials.size() > 0) {
                StringBuilder sb = new StringBuilder("Reference Material: ");
                for (ReferenceMaterial referenceMaterial : referenceMaterials) {
                    sb.append("<").append(referenceMaterial.getResourceLocation()).append(">\n");
                }
                model.put("referenceMaterial", sb.toString());
            }
            int count = 1;
            if (question.getAnswers() != null) {
                for (Answer answer : question.getAnswers()) {
                    model.put("answerChoice" + count, answer.getChoice());
                    model.put("answerText" + count, answer.getText());
                    count++;
                }
            }
        }
    }

    /**
     * Builds Event portions of model for use in templates.
     *
     * @param event Event
     * @param model Map
     */
    private static void buildEventModel(final Event event, final Map<String, Object> model) {
        if (event != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(event.getTitle());
            sb.append("\n\n");
            sb.append("Time: ");
            sb.append(DATE_TIME_FORMATTER.format(event.getStartTime().atZone(ET_ZONE_ID)));
            sb.append("\n");
            Address address = event.getAddress();
            if (address != null) {
                sb.append("Address: \n");
                if (address.getAddressLine1() != null) {
                    sb.append("\t");
                    sb.append(address.getAddressLine1());
                    sb.append("\n");
                }
                if (address.getAddressLine2() != null) {
                    sb.append("\t");
                    sb.append(address.getAddressLine2());
                    sb.append("\n");
                }
                sb.append("\t");
                sb.append(address.getCity());
                sb.append(", ");
                sb.append(address.getState());
                sb.append(" ");
                sb.append(address.getZipCode());
            }
            model.put("event", sb.toString());
            model.put("eventtitle", event.getTitle());
            model.put(
                    "dayofweek",
                    DAY_OF_WEEK.format(
                            Date.from(
                                    event.getStartTime().toInstant(
                                            OffsetDateTime.now(ZoneId.of("America/New_York")).getOffset()))));
        } else {
            model.put("event", "");
            model.put("eventtitle", "");
            model.put("dayofweek", "");
        }
    }

    /**
     * Builds User portions of model for use in templates.
     *
     * @param user  User
     * @param model Map
     */
    private static void buildUserModel(final User user, final Map<String, Object> model) {
        if (user != null) {
            model.put("firstName", user.getFirstName());
            model.put("lastName", user.getLastName());
            model.put("userId", user.getId());
            model.put("code", user.getCode());
            model.put("certificateNumber", user.getCertificateNumber());
        } else {
            model.put("firstName", "");
            model.put("lastName", "");
            model.put("userId", "");
            model.put("code", "");
            model.put("certificateNumber", "");
        }
    }

}
