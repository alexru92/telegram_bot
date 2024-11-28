package com.example.ApartmentsSearchWithTelegramReporting;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Component
@Log4j2
public class MyTelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private Set<String> chatIds = new HashSet<>();

    private final String botToken;

    private final TelegramClient telegramClient;

    private final FlatRepositoryImpl flatRepositoryImpl;

    private final FileService fileService;

    public MyTelegramBot(@Value("${bot.token}") String botToken, FileService fileService, FlatRepositoryImpl flatRepositoryImpl) {
        this.fileService = fileService;
        this.botToken = botToken;
        this.telegramClient = new OkHttpTelegramClient(botToken);
        this.chatIds = fileService.readChatIds();
        this.flatRepositoryImpl = flatRepositoryImpl;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals("/start")) {
            String chatId = String.valueOf(update.getMessage().getChatId());
            User user = update.getMessage().getFrom();

            chatIds.add(chatId);
            fileService.saveChatId(chatId);


            List<String> lastFlats = new ArrayList<>();
            lastFlats.add("You were subscribed to updates. There are 5 last ads:");
            lastFlats.addAll(flatRepositoryImpl.getLastFlats(5));

            lastFlats.forEach(flat -> sendMessage(chatId, flat));
            log.info("User {}, {} {}, has subscribed with chat_id: {}", user.getUserName(), user.getFirstName(), user.getLastName(), chatId);
        }
    }

    public void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage(chatId, text);
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageToAll(String text) {
        chatIds.forEach(chatId ->
                CompletableFuture.runAsync(() -> sendMessage(chatId, text)));
    }
}