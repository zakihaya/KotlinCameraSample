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

                            if (checkAndRequestPermissions()) {
                                takePhoto()
                            }
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
        }
    }


    private fun createImageFile(): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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
            "com.example.cautionplaterecognition.fileprovider",
            photoFile
        )
        takePictureLauncher.launch(photoURI)
    }
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
