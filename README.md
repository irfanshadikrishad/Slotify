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
    match /users/{userId} {
      allow read: if true; // Anyone can read user data
      allow update, delete: if request.auth != null && request.auth.uid == userId;
      allow create: if request.auth != null;
    }
  }
}
```