# API d'Authentification - PerInfinity

## Vue d'ensemble

Cette API gère l'authentification en 2 étapes pour les organisations de l'application PerInfinity.

## Endpoints d'Authentification

### 1. Envoi de code de vérification

**POST** `/api/auth/send-code`

Envoie un code de vérification par email à l'utilisateur.

#### Request Body
```json
{
  "email": "organisation@example.com"
}
```

#### Response (200 OK)
```json
{
  "message": "Code de vérification envoyé avec succès",
  "success": true
}
```

#### Response (400 Bad Request)
```json
{
  "message": "Erreur lors de l'envoi du code: Utilisateur non trouvé avec cet email",
  "success": false
}
```

### 2. Vérification du code

**POST** `/api/auth/verify-code`

Vérifie le code de vérification et retourne un token JWT.

#### Request Body
```json
{
  "email": "organisation@example.com",
  "code": "123456"
}
```

#### Response (200 OK)
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Authentification réussie",
  "success": true
}
```

#### Response (401 Unauthorized)
```json
{
  "message": "Code de vérification invalide",
  "success": false
}
```

## Configuration

### Variables d'environnement requises

```properties
# Base de données
DB_URL=jdbc:mysql://localhost:3306/perinfinity
DB_USER=your_db_user
DB_PASS=your_db_password

# JWT
JWT_SECRET_KEY=your_jwt_secret_key

# Email (Gmail)
EMAIL_USERNAME=your_email@gmail.com
EMAIL_PASSWORD=your_app_password

# Serveur
PORT=8080
```

### Configuration Redis

L'API utilise Redis pour stocker les codes de vérification. Assurez-vous que Redis est installé et en cours d'exécution.

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.database=0
```

## Flux d'authentification

1. **Demande de code** : L'utilisateur saisit son email et clique sur "Envoyer le code"
2. **Vérification email** : Le système vérifie que l'email existe et correspond à une organisation
3. **Génération du code** : Un code à 6 chiffres est généré et stocké dans Redis (expire en 10 minutes)
4. **Envoi d'email** : Le code est envoyé par email à l'utilisateur
5. **Saisie du code** : L'utilisateur saisit le code reçu
6. **Vérification** : Le système vérifie le code et génère un token JWT
7. **Authentification** : L'utilisateur est authentifié et peut accéder au tableau de bord

## Sécurité

- Les codes de vérification expirent après 10 minutes
- Les codes sont supprimés après utilisation
- Seuls les utilisateurs avec le rôle `ORGANIZATION` peuvent utiliser cette API
- Les tokens JWT ont une durée de vie de 1 heure

## Gestion des erreurs

L'API retourne des codes HTTP appropriés :
- `200` : Succès
- `400` : Erreur de validation (email invalide, utilisateur non trouvé)
- `401` : Code de vérification invalide
- `500` : Erreur serveur interne

## Démarrage

1. Installer Redis
2. Configurer les variables d'environnement
3. Lancer l'application Spring Boot
4. L'API sera disponible sur `http://localhost:8080`



