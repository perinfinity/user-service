package com.perinfinity.auth_api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class VerificationCodeService {
    
    private final Map<String, CodeData> codeStorage = new ConcurrentHashMap<>();
    private static final int CODE_EXPIRATION_MINUTES = 10;
    
    public String generateAndStoreCode(String email) {
        String code = generateRandomCode();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES);
        
        codeStorage.put(email, new CodeData(code, expirationTime));
        log.info("Verification code generated for: {}", email);

        return code;
    }

    public boolean verifyCode(String email, String code) {
        CodeData storedData = codeStorage.get(email);

        if (storedData != null &&
            storedData.code.equals(code) &&
            LocalDateTime.now().isBefore(storedData.expirationTime)) {

            codeStorage.remove(email);
            log.info("Verification code verified for: {}", email);
            return true;
        }

        log.warn("Invalid or expired verification code for: {}", email);
        return false;
    }
    
    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        
        return code.toString();
    }
    
    public void cleanupExpiredCodes() {
        LocalDateTime now = LocalDateTime.now();
        codeStorage.entrySet().removeIf(entry -> 
            entry.getValue().expirationTime.isBefore(now));
    }
    
    private static class CodeData {
        final String code;
        final LocalDateTime expirationTime;
        
        CodeData(String code, LocalDateTime expirationTime) {
            this.code = code;
            this.expirationTime = expirationTime;
        }
    }
}
