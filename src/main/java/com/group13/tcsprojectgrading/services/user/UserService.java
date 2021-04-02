package com.group13.tcsprojectgrading.services.user;

import com.group13.tcsprojectgrading.models.user.User;
import com.group13.tcsprojectgrading.repositories.user.UserRepository;
import com.group13.tcsprojectgrading.services.submissions.SubmissionService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UserService {
    private final SubmissionService submissionService;
    private final UserRepository userRepository;

    public UserService(SubmissionService submissionService, UserRepository userRepository) {
        this.submissionService = submissionService;
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveUser(User user) {
        this.userRepository.save(user);
    }

    @Transactional
    public User findById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return null;
        } else {
            return userOptional.get();
        }
    }
}
