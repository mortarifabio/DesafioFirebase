package com.mortarifabio.desafiofirebase.games

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mortarifabio.desafiofirebase.R
import com.mortarifabio.desafiofirebase.extensions.toHashMap
import com.mortarifabio.desafiofirebase.model.Game
import com.mortarifabio.desafiofirebase.utils.Constants.Firestore.FIRESTORE_COLLECTION_USERS
import com.mortarifabio.desafiofirebase.utils.Constants.Firestore.FIRESTORE_COLLECTION_USER_GAMES
import com.mortarifabio.desafiofirebase.utils.Constants.Firestore.FIRESTORE_LOG_KEY
import com.mortarifabio.desafiofirebase.utils.Constants.Firestore.FIRESTORE_PAGE_SIZE
import com.mortarifabio.desafiofirebase.utils.Constants.Storage.STORAGE_LOG_KEY
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class GamesRepository (
    val context: Context
){
    private val auth by lazy {
        Firebase.auth
    }
    private val db by lazy {
        Firebase.firestore
    }
    private val storageRef by lazy {
        Firebase.storage.reference
    }

    suspend fun saveGame(game: Game, gameId: String?, bitmap: Bitmap?) : Boolean {
        return try {
            auth.currentUser?.uid?.let {
                game.image = saveImage(bitmap, it, game.image)
                val gameData = game.toHashMap()
                val collectionRef = db.collection(FIRESTORE_COLLECTION_USERS)
                    .document(it)
                    .collection(FIRESTORE_COLLECTION_USER_GAMES)
                val documentRef = gameId?.let { id ->
                    collectionRef.document(id)
                } ?: collectionRef.document()
                documentRef.set(gameData)
                    .await()
                true
            } ?: run {
                false
            }
        } catch(exception: Exception) {
            Log.w(FIRESTORE_LOG_KEY, "saveUser:failure", exception)
            false
        }
    }

    private suspend fun saveImage(bitmap: Bitmap?, uid: String, imgPath: String?): String? {
        return try {
            bitmap?.let {
                val file = bitmapToFile(bitmap)
                val fileUri = Uri.fromFile(file)
                val filePath = imgPath ?: "$uid/${System.currentTimeMillis()}.jpg"
                val fileRef = storageRef.child(filePath)
                fileRef.putFile(fileUri)
                    .await()
                filePath
            }
        } catch (exception: Exception) {
            Log.w(STORAGE_LOG_KEY, "saveImage:failure", exception)
            null
        }
    }

    suspend fun getGames(page: Int, lastGame: Game? = null) : GamesResponse {
        return try {
            auth.currentUser?.uid?.let {
                val games = db.collection(FIRESTORE_COLLECTION_USERS)
                    .document(it)
                    .collection(FIRESTORE_COLLECTION_USER_GAMES)
                    .orderBy("name")
                    .startAfter(lastGame)
                    .limit(FIRESTORE_PAGE_SIZE.toLong())
                    .get()
                    .await()
                GamesResponse.Success(games)
            } ?: run {
                GamesResponse.Error(context.getString(R.string.user_not_logged_in))
            }
        } catch(exception: Exception) {
            Log.w(FIRESTORE_LOG_KEY, "getGames:failure", exception)
            GamesResponse.Error(exception.localizedMessage ?: "")
        }
    }

    private fun bitmapToFile(bitmap: Bitmap): File? {
        val imageFile = File(context.filesDir, "tempImage.jpg")
        val outputStream: OutputStream
        return try {
            outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
            outputStream.flush()
            outputStream.close()
            imageFile
        } catch (exception: Exception) {
            Log.e(STORAGE_LOG_KEY, "bitmapToFile:failure", exception)
            null
        }
    }

}