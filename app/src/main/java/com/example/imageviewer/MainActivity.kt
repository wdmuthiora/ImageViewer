package com.example.imageviewer

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Cyan
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import coil.compose.AsyncImage
import com.example.imageviewer.ui.theme.ImageViewerTheme

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<ImageViewModel> ()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
           println("THE BUILD VERSION IS "+Build.VERSION.SDK_INT)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                ),
                0
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            println("THE BUILD VERSION IS "+Build.VERSION.SDK_INT)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(READ_MEDIA_IMAGES),
                1
            )
        } else {
            println("THE BUILD VERSION IS "+Build.VERSION.SDK_INT)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(READ_EXTERNAL_STORAGE),
                2
            )
        }

//        val millisYesterday = Calendar.getInstance().apply {
//            add(Calendar.DAY_OF_YEAR, -1)
//        }.timeInMillis

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        val selection = "${MediaStore.Images.Media.MIME_TYPE}= ?"
        //val selection = "${MediaStore.Images.Media.DATE_TAKEN} >=?"

        val selectionArgs = arrayOf("image/jpeg")
        //val selectionArgs= arrayOf(millisYesterday.toString())

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            println("**************here")
            val idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID)

            val nameColumn = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)

            val imageType = cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)

            val images = mutableListOf<Image>()

            while(cursor.moveToNext()){

                println("-------------FOUND SOMETHING")

                val id = cursor.getLong(idColumn)

                val name = cursor.getString(nameColumn)

                val type = cursor.getString(imageType)

                val uri = ContentUris.withAppendedId(
                   MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                   id
                )

                images.add(Image(id, name, uri, type))

            }

            println()

            viewModel.updateImages(images)

        }

        setContent {
            ImageViewerTheme{

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(
                        modifier = Modifier
                            .padding(16.dp)
                            .background(Color.Red)
                    )

                    Text(
                        text = "Image Viewer App",
                        color = Blue,
                        fontSize = 30.sp,
                        style = TextStyle(
                            brush = Brush.linearGradient(
                                colors = listOf(Cyan, Color.LightGray, Color.Magenta)
                            )
                        )
                    )

                    Spacer(
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color.Red)
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        println(viewModel.images)
                        items(viewModel.images) { image ->

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = image.name,
                                    color = Blue,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Bold,
                                    style = TextStyle(
                                        brush = Brush.linearGradient(listOf(Cyan, Blue, Color.Gray))
                                    ),
                                    modifier = Modifier.padding(7.dp)

                                )

                                Text(
                                    text = image.type,
                                    color = Blue,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Bold,
                                    style = TextStyle(
                                        brush = Brush.linearGradient(listOf(Cyan, Blue, Color.Gray))
                                    ),
                                    modifier = Modifier.padding(7.dp)

                                )

                                AsyncImage(
                                   model = image.uri,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp)),
                                   contentDescription = null
                                )

                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(0.dp, 8.dp)
                                        .height(1.dp),
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
data class Image(
    val  id: Long,
    val name: String,
    val uri: Uri,
    val type: String
)

