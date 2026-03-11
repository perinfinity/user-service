# Test de l'authentification

## Utilisateurs de test disponibles :

### Utilisateurs VOLUNTEER :

- **Email:** sarah.volunteer@example.com  
- **Mot de passe:** password
- **Username:** sarah_volunteer

- **Email:** alex.volunteer@example.com
- **Mot de passe:** password
- **Username:** alex_volunteer

### Utilisateurs ORGANIZATION :

- **Email:** redcross@example.com
- **Mot de passe:** password
- **Username:** redcross_org

## Test avec curl (PowerShell) :

```powershell
# Test de connexion réussie
$body = @{
    email = "alex.volunteer@example.com"
    password = "password"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/auth/login" -Method POST -Body $body -ContentType "application/json"
```

## Test avec Postman :
- **URL:** POST http://localhost:8080/auth/login
- **Headers:** Content-Type: application/json
- **Body (JSON):**
```json
{
    "email": "alex.volunteer@example.com",
    "password": "password"
}
```

## Réponse attendue :
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600000
}
```
