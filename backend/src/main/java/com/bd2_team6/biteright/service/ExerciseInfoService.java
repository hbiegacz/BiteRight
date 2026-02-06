package com.bd2_team6.biteright.service;

import com.bd2_team6.biteright.controllers.DTO.ExerciseInfoDTO;
import com.bd2_team6.biteright.controllers.requests.create_requests.ExerciseInfoCreateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.ExerciseInfoUpdateRequest;
import com.bd2_team6.biteright.entities.exercise_info.ExerciseInfo;
import com.bd2_team6.biteright.entities.exercise_info.ExerciseInfoRepository;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class ExerciseInfoService {

    private final ExerciseInfoRepository exerciseInfoRepository;

    public ExerciseInfoService(ExerciseInfoRepository exerciseInfoRepository) {
        this.exerciseInfoRepository = exerciseInfoRepository;
    }

    public Set<ExerciseInfoDTO> findExerciseInfoByName(String name) {
        Set<ExerciseInfo> info = exerciseInfoRepository.findByNameContainingIgnoreCase(name);
        return info.stream() 
                .map(ExerciseInfoDTO::new)
                .collect(Collectors.toSet());
    }

    public ExerciseInfoDTO createExerciseInfo(ExerciseInfoCreateRequest request) {
        ExerciseInfo newInfo = new ExerciseInfo();
        newInfo.setMetabolicEquivalent(request.getMetabolicEquivalent());
        newInfo.setName(request.getName());
        return new ExerciseInfoDTO(exerciseInfoRepository.save(newInfo));
    }

    public ExerciseInfoDTO updateExerciseInfo(String name, ExerciseInfoUpdateRequest request) {
        ExerciseInfo info = exerciseInfoRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Exercise with name '" + name + "' not found"));
        info.setMetabolicEquivalent(request.getMetabolicEquivalent());
        info.setName(request.getName());
        return new ExerciseInfoDTO(exerciseInfoRepository.save(info));
    }

    public void deleteExerciseInfo(String name) {
        ExerciseInfo info = exerciseInfoRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Exercise with name '" + name + "' not found"));
        exerciseInfoRepository.delete(info);
    }
}
