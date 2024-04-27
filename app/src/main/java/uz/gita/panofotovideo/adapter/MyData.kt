package uz.gita.panofotovideo.adapter

/**
 * Author: Zukhriddin Kamolov
 * Date: 27-Apr-24, 12:01 PM.
 * Project: PanoramaViewer
 */
data class MyData(
    val id: Int,
    val name: String,
    val url: String,
    val type: MediaType
)

enum class MediaType{
    Video, Photo
}