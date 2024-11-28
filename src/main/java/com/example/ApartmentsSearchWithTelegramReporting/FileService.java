package com.example.ApartmentsSearchWithTelegramReporting;

import java.util.Set;
import java.util.Map;

public interface FileService {

    Set<String> readChatIds();

    void saveChatId(String chatId);

    Map<Long, String> readFlats();

    void saveFlat(Long id, String flatPriceAndAddress, String flatUrl);

    void emptyFlats();

}
