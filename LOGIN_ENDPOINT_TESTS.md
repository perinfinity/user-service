# Tests de l'endpoint /auth/login avec gestion d'erreurs

## Test 1: Connexion réussie
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ntepp_marcus@yahoo.fr",
    "password": "password"
  }'
```

**Réponse attendue (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600000
}
```

## Test 2: Email manquant
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "password": "password"
  }'
```

**Réponse attendue (400 Bad Request):**
```
L'email est requis
```

## Test 3: Mot de passe manquant
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ntepp_marcus@yahoo.fr"
  }'
```

**Réponse attendue (400 Bad Request):**
```
Le mot de passe est requis
```

## Test 4: Mauvais email/mot de passe
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "wrong@example.com",
    "password": "wrongpassword"
  }'
```

**Réponse attendue (401 Unauthorized):**
```
Email ou mot de passe incorrect
```

## Test 5: Compte désactivé (si implémenté)
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "disabled@example.com",
    "password": "password"
  }'
```

**Réponse attendue (403 Forbidden):**
```
Votre compte est désactivé. Contactez l'administrateur.
```

## Test 6: Erreur interne du serveur
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password"
  }'
```

**Réponse attendue (500 Internal Server Error):**
```
Erreur lors de l'authentification: [message d'erreur spécifique]
```
