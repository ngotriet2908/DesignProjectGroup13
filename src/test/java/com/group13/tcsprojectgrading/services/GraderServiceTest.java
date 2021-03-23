package com.group13.tcsprojectgrading.services;

import com.group13.tcsprojectgrading.models.Flag;
import com.group13.tcsprojectgrading.models.Grader;
import com.group13.tcsprojectgrading.models.GraderId;
import com.group13.tcsprojectgrading.repositories.FlagRepository;
import com.group13.tcsprojectgrading.repositories.GraderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GraderServiceTest {
    @Mock
    private GraderRepository repository;

    @Mock
    private FlagRepository flagRepository;

    private FlagService flagService;

    @InjectMocks
    private GraderService graderService;

    @BeforeEach
    void init() {
        flagService = new FlagService(flagRepository);
    }

    //Don't think it really works with mocking
    @Test
    public void addFlagToGraderNotInDb() {
        when(repository.findById(any(GraderId.class))).thenReturn(null);
        when(flagRepository.save(any(Flag.class))).then(returnsFirstArg());
        Grader grader = new Grader();
        graderService.addNewGrader(grader);
    }
}
