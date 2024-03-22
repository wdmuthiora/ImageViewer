package com.example.imageviewer

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.content.ContentUris
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import coil.compose.AsyncImage
import com.example.imageviewer.ui.theme.ImageViewerTheme

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<ImageViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            println("THE BUILD VERSION IS " + Build.VERSION.SDK_INT)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                ),
                0
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            println("THE BUILD VERSION IS " + Build.VERSION.SDK_INT)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(READ_MEDIA_IMAGES),
                1
            )
        } else {
            println("THE BUILD VERSION IS " + Build.VERSION.SDK_INT)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(READ_EXTERNAL_STORAGE),
                2
            )
        }

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        val selection = "${MediaStore.Images.Media.MIME_TYPE}= ?"

        val selectionArgs = arrayOf("image/jpeg")

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->

            val idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
            val imageType = cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)
            val imageItems = mutableListOf<ImageItem>()

            while (cursor.moveToNext()) {

                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val type = cursor.getString(imageType)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                imageItems.add(ImageItem(id, name, uri, type, generateRandomColor(), 175.dp))

            }

            viewModel.updateImages(imageItems)

        }

        setContent {
            ImageViewerTheme {
                Column() {

                    Text(
                        text = "Image Viewer App",
                        color = Color.Blue,
                        fontSize = 30.sp,
                        style = TextStyle(
                            brush = Brush.linearGradient(
                                colors = listOf(Color.Cyan, Color.LightGray, Color.Magenta)
                            )
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                            .padding(8.dp)
                    )

                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Adaptive(150.dp),
                        verticalItemSpacing = 4.dp,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        content = {
                            items(viewModel.gridImageItems) { item ->
                                AsyncImage(
                                    model = item.uri,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp)),
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(4.dp)
                    )

                }

            }
        }
    }
}


