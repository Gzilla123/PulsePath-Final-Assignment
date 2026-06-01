package com.pulsepath.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.pulsepath.models.WorkoutSession
import kotlinx.coroutines.tasks.await

object FirebaseManager {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    val currentUser: FirebaseUser? get() = auth.currentUser
    val isLoggedIn get() = auth.currentUser != null
    val userId get() = auth.currentUser?.uid ?: ""



    suspend fun register(email: String, password: String, name: String): Result<FirebaseUser> = try {
        val res = auth.createUserWithEmailAndPassword(email, password).await()
        db.collection("users").document(res.user!!.uid).set(
            mapOf("name" to name, "email" to email, "createdAt" to System.currentTimeMillis())
        ).await()
        Result.success(res.user!!)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun login(email: String, password: String): Result<FirebaseUser> = try {
        val res = auth.signInWithEmailAndPassword(email, password).await()
        Result.success(res.user!!)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun sendPasswordReset(email: String): Result<Unit> = try {
        auth.sendPasswordResetEmail(email).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    fun logout() = auth.signOut()



    suspend fun saveWorkout(session: WorkoutSession): Result<Unit> = try {
        db.collection("users").document(session.userId)
            .collection("workouts").document(session.id)
            .set(mapOf(
                "id" to session.id, "type" to session.type.name,
                "durationSeconds" to session.durationSeconds,
                "steps" to session.steps, "caloriesBurned" to session.caloriesBurned,
                "startTime" to session.startTime, "endTime" to session.endTime
            ), SetOptions.merge()).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun fetchWorkoutsFromCloud(userId: String): Result<List<WorkoutSession>> = try {
        val snap = db.collection("users").document(userId).collection("workouts").get().await()
        val sessions = snap.documents.mapNotNull { doc ->
            try {
                WorkoutSession(
                    id = doc.getString("id") ?: doc.id,
                    userId = userId,
                    type = enumValueOf(doc.getString("type") ?: "RUNNING"),
                    durationSeconds = doc.getLong("durationSeconds") ?: 0L,
                    steps = doc.getLong("steps")?.toInt() ?: 0,
                    caloriesBurned = doc.getLong("caloriesBurned")?.toInt() ?: 0,
                    startTime = doc.getLong("startTime") ?: 0L,
                    endTime = doc.getLong("endTime") ?: 0L,
                    syncedToCloud = true
                )
            } catch (e: Exception) { null }
        }
        Result.success(sessions)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun getUserName(userId: String): String = try {
        val doc = db.collection("users").document(userId).get().await()
        doc.getString("name") ?: "Athlete"
    } catch (e: Exception) { "Athlete" }
}
