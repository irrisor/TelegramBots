package org.telegram.telegrambots.starter;

import org.springframework.beans.factory.InitializingBean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.meta.generics.WebhookBot;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Receives all beand which are #LongPollingBot and #WebhookBot and register them in #TelegramBotsApi.
 */
public class TelegramBotInitializer implements InitializingBean {

    private final TelegramBotsApi telegramBotsApi;
    private final List<LongPollingBot> longPollingBots;
    private final List<WebhookBot> webHookBots;
    private List<BotSession> botSessions = new ArrayList<>();

    public TelegramBotInitializer(TelegramBotsApi telegramBotsApi,
                                  List<LongPollingBot> longPollingBots,
                                  List<WebhookBot> webHookBots) {
        Objects.requireNonNull(telegramBotsApi);
        Objects.requireNonNull(longPollingBots);
        Objects.requireNonNull(webHookBots);
        this.telegramBotsApi = telegramBotsApi;
        this.longPollingBots = longPollingBots;
        this.webHookBots = webHookBots;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            for (LongPollingBot bot : longPollingBots) {
                addSession(telegramBotsApi.registerBot(bot));
            }
            for (WebhookBot bot : webHookBots) {
                telegramBotsApi.registerBot(bot);
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void addSession(BotSession botSession) {

        botSessions.add(botSession);
    }

    @PreDestroy
    public void shutdown() {
        for (BotSession session : botSessions) {
            session.stop();
        }
    }
}
