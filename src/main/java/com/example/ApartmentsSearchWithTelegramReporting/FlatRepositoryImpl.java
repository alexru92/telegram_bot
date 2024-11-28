package com.example.ApartmentsSearchWithTelegramReporting;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlatRepositoryImpl implements FlatRepository {

    FileService fileService;

    private Map<Long, String> flatsWithDetails = new HashMap<>();

    public FlatRepositoryImpl(FileService fileService) {
        this.fileService = fileService;
        flatsWithDetails = fileService.readFlats();
    }

    @Override
    public Map<Long, String> getAllFlats() {
        return flatsWithDetails;
    }

    @Override
    public List<String> getLastFlats(int limit) {
        List<Long> lastIds = flatsWithDetails.keySet().stream()
                .sorted(Comparator.reverseOrder())
                .limit(limit)
                .toList();
        return lastIds.stream()
                .map(flatsWithDetails::get)
                .collect(Collectors.toList());
    }

    @Override
    public void replaceFlats(Map<Long, String> newFlatsWithDetails) {
        flatsWithDetails = newFlatsWithDetails;

        fileService.emptyFlats();
        for (Long flatId : newFlatsWithDetails.keySet()) {
            String[] flatDetails = newFlatsWithDetails.get(flatId).split("\n");
            fileService.saveFlat(flatId, flatDetails[0], flatDetails[1]);
        }
    }
}
