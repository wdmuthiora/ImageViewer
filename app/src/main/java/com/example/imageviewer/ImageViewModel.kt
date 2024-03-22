package com.example.imageviewer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ImageViewModel:ViewModel() {
    var gridImageItems by mutableStateOf(emptyList<ImageItem>())
        private set

    fun updateImages(gridImageItems: List<ImageItem>){
        this.gridImageItems = gridImageItems
    }
}