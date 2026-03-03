package com.perinfinity.auth_api.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilitaire pour tester les mots de passe et vérifier la configuration BCrypt
 */
public class PasswordTestUtil {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Hash du mot de passe utilisé dans les données de test
        String testPassword = "password";
        String hashedPassword = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi";
        
        System.out.println("=== TEST DE MOT DE PASSE ===");
        System.out.println("Mot de passe test: " + testPassword);
        System.out.println("Hash stocké: " + hashedPassword);
        System.out.println("Correspondance: " + encoder.matches(testPassword, hashedPassword));
        
        // Générer un nouveau hash pour comparaison
        String newHash = encoder.encode(testPassword);
        System.out.println("Nouveau hash généré: " + newHash);
        System.out.println("Nouveau hash correspond: " + encoder.matches(testPassword, newHash));
        
        // Test avec d'autres mots de passe
        System.out.println("\n=== TESTS ADDITIONNELS ===");
        String[] testPasswords = {"password", "Password", "PASSWORD", "password123", "wrong"};
        
        for (String pwd : testPasswords) {
            boolean matches = encoder.matches(pwd, hashedPassword);
            System.out.println("'" + pwd + "' correspond: " + matches);
        }
    }
}
