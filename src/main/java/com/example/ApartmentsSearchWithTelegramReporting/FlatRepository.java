package com.example.ApartmentsSearchWithTelegramReporting;

import java.util.Map;
import java.util.List;

public interface FlatRepository {

    Map<Long, String> getAllFlats();

    List<String> getLastFlats(int limit);

    void replaceFlats(Map<Long, String> newFlatsWithDetails);

}
