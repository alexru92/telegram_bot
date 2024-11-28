package com.example.ApartmentsSearchWithTelegramReporting;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.ApartmentsSearchWithTelegramReporting.FileUtils.*;

@Service
public class FileServiceImpl implements FileService {

    private static final String CHAT_IDS_FILE = "chatIds.txt";
    private static final String FLATS_FILE = "apartments.txt";

    @Override
    public Set<String> readChatIds() {
        return readFromFile(CHAT_IDS_FILE, reader -> reader.lines().collect(Collectors.toSet()));
    }

    @Override
    public void saveChatId(String chatId) {
        writeToFile(CHAT_IDS_FILE, chatId, true);
    }

    @Override
    public Map<Long, String> readFlats() {
        Map<Long, String> flatsWithDetails = new HashMap<>();
        Set<String> lines = readFromFile(FLATS_FILE, reader -> reader.lines().collect(Collectors.toSet()));
        if (lines != null && !lines.isEmpty()) {
            for (String line : lines) {
                String[] stringToMap = line.split(";");
                flatsWithDetails.put(Long.valueOf(stringToMap[0]), stringToMap[1] + "\n" + stringToMap[2]);
            }
        }
        return flatsWithDetails;
    }

    @Override
    public void saveFlat(Long id, String flatPriceAndAddress, String flatUrl) {
        writeToFile(FLATS_FILE, id + ";" + flatPriceAndAddress + ";" + flatUrl, true);
    }

    public void emptyFlats() {
        emptyFile(FLATS_FILE);
    }
}
