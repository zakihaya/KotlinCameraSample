package net.zakihaya.kotlincamerasmaple

import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import net.zakihaya.kotlincamerasmaple.ui.theme.KotlinCameraSmapleTheme
import java.io.File

import coil.compose.AsyncImage
import coil.request.ImageRequest

import android.Manifest
import android.media.MediaScannerConnection
import android.util.Log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinCameraSmapleTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    imageUri?.let {
                        ImageDisplay(it)
                    }
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {

                            // パーミッションのリクエスト
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }) {
                            Text("Take Photo")
                        }
                    }
                }
            }
        }
    }

    private lateinit var photoFile: File
    private var imageUri by mutableStateOf<Uri?>(null)
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            imageUri = Uri.fromFile(photoFile)
            MediaScannerConnection.scanFile(
                this,
                arrayOf(photoFile.path),
                null
            ) { path, uri ->
                // スキャン完了後にログを記録
                Log.d("MediaScanner", "画像がスキャンされました: $path")
            }
        }
    }
    companion object {
        const val REQUEST_CAMERA_PERMISSION = 101

    }

    private fun createImageFile(): File {
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "JPEG_${System.currentTimeMillis()}_",
            ".jpg",
            storageDir
        )
    }

    private fun takePhoto() {
        photoFile = createImageFile()
        val photoURI: Uri = FileProvider.getUriForFile(
            this,
            "net.zakihaya.kotlincamerasmaple.fileprovider",
            photoFile
        )
        takePictureLauncher.launch(photoURI)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // パーミッションが許可された場合の処理
            // カメラを起動
            takePhoto()
        } else {
            // パーミッションが拒否された場合の処理
            Toast.makeText(
                this,
                "カメラのパーミッションが必要です。設定から許可してください。",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

//    private fun checkAndRequestPermissions(): Boolean {
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
//                // パーミッションが必要であることの説明を表示
//                Toast.makeText(this, "カメラのパーミッションが必要です。設定から許可してください。", Toast.LENGTH_LONG).show()
//            }
//            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
//            return false
//        }
//        return true
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CAMERA_PERMISSION) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // パーミッションが許可されたらカメラを起動
//                takePhoto()
//            } else {
//                // パーミッションが拒否された場合
//                Toast.makeText(
//                    this,
//                    "カメラのパーミッションが必要です。設定から許可してください。",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KotlinCameraSmapleTheme {
        Greeting("Android")
    }
}

@Composable
fun ImageDisplay(imageUri: Uri) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUri)
            .crossfade(true)
            .build(),
        contentDescription = "Captured Image",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}
