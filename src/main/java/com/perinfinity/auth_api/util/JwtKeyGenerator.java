package com.perinfinity.auth_api.util;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

/**
 * Utilitaire pour générer des clés secrètes JWT sécurisées
 * À utiliser uniquement pour générer des clés de production
 */
public class JwtKeyGenerator {
    
    public static void main(String[] args) {
        try {
            // Générer une clé secrète de 256 bits pour HS256
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();
            
            // Encoder en Base64
            String base64Key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            
            System.out.println("=== CLÉ SECRÈTE JWT GÉNÉRÉE ===");
            System.out.println("Clé Base64: " + base64Key);
            System.out.println("Longueur: " + base64Key.length() + " caractères");
            System.out.println("=================================");
            System.out.println();
            System.out.println("Pour utiliser cette clé, ajoutez-la à votre application.properties :");
            System.out.println("jwt.secret.key=" + base64Key);
            System.out.println();
            System.out.println("Ou définissez la variable d'environnement :");
            System.out.println("JWT_SECRET_KEY=" + base64Key);
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération de la clé : " + e.getMessage());
        }
    }
}
