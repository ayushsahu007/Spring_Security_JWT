package com._workfoxtech.controller;

import com._workfoxtech.model.Candidate;
import com._workfoxtech.repository.CandidateRepository;
import com._workfoxtech.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/candidate")
@RequiredArgsConstructor
public class CandidateController {
    private final CandidateRepository candidateRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

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

    // ---------- SIGNIN WITH JWT ----------
    @PostMapping("/signin")
    public ResponseEntity<Map<String, String>> signin(
            @RequestBody Candidate candidate) {

        // 1️⃣ Authenticate username + password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        candidate.getUsername(),
                        candidate.getPassword()
                )
        );

        // 2️⃣ Generate JWT token
        String token = jwtUtil.generateToken(candidate.getUsername());

        // 3️⃣ Return token in response
        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<String> profile() {
        return ResponseEntity.ok("This is a protected profile API");
    }

}
