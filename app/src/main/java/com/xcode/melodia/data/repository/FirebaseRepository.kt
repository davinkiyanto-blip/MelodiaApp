package com.xcode.melodia.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    val currentUser = auth.currentUser

    // This is a simplified example. Real app would use Credential Manager for Google Sign In
    // But since that requires more boilerplate, we'll setup the structure here.
    
    suspend fun saveSongToHistory(song: SongEntity) {
        val uid = auth.currentUser?.uid ?: return
        try {
            firestore.collection("users").document(uid)
                .collection("songs").document(song.id)
                .set(song)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getUserSongs(): List<SongEntity> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        return try {
            val snapshot = firestore.collection("users").document(uid)
                .collection("songs")
                .orderBy("createdAt") // Descending usually
                .get()
                .await()
            snapshot.toObjects(SongEntity::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
    suspend fun signInWithGoogle(idToken: String): Boolean {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun logout() {
        auth.signOut()
    }
}

// Simple Entity for Firestore
data class SongEntity(
    val id: String = "",
    val title: String = "",
    val audioUrl: String = "",
    val imageUrl: String = "",
    val status: String = "",
    val type: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
