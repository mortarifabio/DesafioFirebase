package com.mortarifabio.desafiofirebase.games.view

import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import androidx.exifinterface.media.ExifInterface.*
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mortarifabio.desafiofirebase.R
import com.mortarifabio.desafiofirebase.databinding.ActivityGamesRegisterBinding
import com.mortarifabio.desafiofirebase.games.viewModel.GamesViewModel
import com.mortarifabio.desafiofirebase.model.Game
import com.mortarifabio.desafiofirebase.utils.Constants
import com.mortarifabio.desafiofirebase.utils.Constants.Analytics.ANALYTICS_REGISTER_GAME_EVENT
import com.mortarifabio.desafiofirebase.utils.Constants.Intent.INTENT_GAME_KEY
import java.io.File


class GamesRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGamesRegisterBinding
    private val viewModel: GamesViewModel by lazy {
        ViewModelProvider(this).get(GamesViewModel::class.java)
    }
    private val analytics by lazy {
        Firebase.analytics
    }
    private val storageRef by lazy {
        Firebase.storage.reference
    }
    private var game: Game? = null
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGamesRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        game = intent.getParcelableExtra(INTENT_GAME_KEY)
        loadContent()
        setupObservables()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            var bitmap: Bitmap? = null
            when (requestCode) {
                0 -> (data.extras?.get("data") as? Bitmap).let {
                    bitmap = it
                }
                1 -> data.data?.let {
                    val image = BitmapFactory.decodeStream(contentResolver.openInputStream(it))
                    val imgStream = contentResolver.openInputStream(it)
                    imgStream?.let { _ ->
                        val exif = ExifInterface(imgStream)
                        val orientation = exif.getAttributeInt(TAG_ORIENTATION, 1);
                        val matrix = Matrix()
                        when (orientation) {
                            ORIENTATION_ROTATE_90 -> matrix.postRotate(90F);
                            ORIENTATION_ROTATE_180 -> matrix.postRotate(180F);
                            ORIENTATION_ROTATE_270 -> matrix.postRotate(270F);
                        }
                        val finalImage = Bitmap.createBitmap(
                            image,
                            0,
                            0,
                            image.width,
                            image.height,
                            matrix,
                            true
                        );
                        bitmap = finalImage
                    }
                }
            }
            bitmap?.let {
                Glide.with(this).clear(binding.ibGamesRegisterImage);
                binding.ibGamesRegisterImage.setImageBitmap(it)
                this.bitmap = it
            }
        }
    }

    private fun loadContent() = with(binding) {
        game?.let {
            val imgRef = it.image?.let { image -> storageRef.child(image) }
            Glide.with(this@GamesRegisterActivity)
                .load(imgRef)
                .placeholder(R.drawable.img_camera)
                .fitCenter()
                .into(ibGamesRegisterImage)
            tietGamesRegisterName.setText(it.name)
            tietGamesRegisterCreatedAt.setText(it.createdAt)
            tietGamesRegisterDescription.setText(it.description)
        }
    }

    private fun setupObservables() = with(binding) {
        ibGamesRegisterBack.setOnClickListener {
            finish()
        }
        btGamesRegisterSave.setOnClickListener {
            analytics.logEvent(ANALYTICS_REGISTER_GAME_EVENT, null)
            viewModel.saveGame(binding, game?.id, bitmap)
        }
        viewModel.saveLiveData.observe(this@GamesRegisterActivity) {
            if (it) {
                startActivity(Intent(this@GamesRegisterActivity, GamesListActivity::class.java))
                finish()
            } else {
                getString(R.string.error_saving_game)
            }
        }
        ibGamesRegisterImage.setOnClickListener {
            selectImage()
        }
    }

    private fun selectImage() {
        val items = arrayOf("Camera", "Gallery")
        AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.get_image_from))
            .setItems(items) { dialog, which ->
                when (items[which]) {
                    "Camera" -> {
                        val takePicture = Intent(ACTION_IMAGE_CAPTURE)
                        startActivityForResult(takePicture, 0)
                    }
                    "Gallery" -> {
                        val intent = Intent(ACTION_PICK, EXTERNAL_CONTENT_URI)
                        startActivityForResult(intent, 1)
                    }
                }
            }
            .show()
    }

}