#### Slotify: Efficient Schedule Management

#### Tech Stack

- Firebase (Authentication & Database)
- Kotlin

#### Firebase rules

```
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    match /slots/{slotId} {
      allow read, write: if request.auth != null;
    }
  }
}
```