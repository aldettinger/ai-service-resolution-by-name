/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.acme;

import java.util.function.Supplier;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@ApplicationScoped
@Named("aiServiceResolvedByName")
@RegisterAiService(chatLanguageModelSupplier = AiServiceResolvedByName.AiServiceResolvedByNameModelSupplier.class)
public interface AiServiceResolvedByName {

    public static class AiServiceResolvedByNameModelSupplier implements Supplier<ChatLanguageModel> {
        @Override
        public ChatLanguageModel get() {
            System.out.println("!!!!!!!!!!!! model get has been called");
            return (messages) -> {
                System.out.println("!!!!!!!!!!!! model chat has been called");
                return new Response<>(new AiMessage("AiServiceResolvedByName has been resolved"));
            };
        }
    }

    @UserMessage("Any prompt")
    //@Handler annotation from camel removed in this reproducer
    String chat(String input);
}
