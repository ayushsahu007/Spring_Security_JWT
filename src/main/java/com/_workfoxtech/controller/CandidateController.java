package com._workfoxtech.controller;

import com._workfoxtech.model.Candidate;
import com._workfoxtech.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/candidate")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateRepository candidateRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    // ---------- SIGNUP ----------
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody Candidate candidate) {

        candidate.setPassword(
                passwordEncoder.encode(candidate.getPassword())
        );

        candidate.setRole("USER"); // IMPORTANT

        candidateRepository.save(candidate);

        return ResponseEntity.ok("Signup successful");
    }

    // ---------- SIGNIN ----------
    @PostMapping("/signin")
    public ResponseEntity<String> signin(@RequestBody Candidate candidate) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        candidate.getUsername(),
                        candidate.getPassword()
                )
        );

        return ResponseEntity.ok("Login successful");
    }
}
