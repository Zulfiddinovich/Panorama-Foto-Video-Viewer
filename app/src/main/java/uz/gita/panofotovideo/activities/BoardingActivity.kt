package uz.gita.panofotovideo.activities;

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.gita.panofotovideo.App
import uz.gita.panofotovideo.adapter.MediaType
import uz.gita.panofotovideo.adapter.MyAdapter
import uz.gita.panofotovideo.adapter.MyData
import uz.gita.panofotovideo.databinding.ActivityBoardingBinding
import uz.gita.panofotovideo.databinding.BottomSheetBinding
import uz.gita.panofotovideo.util.FileUtils
/**
 * Author: Zukhriddin Kamolov
 * Date: 25-Apr-24, 10:57 AM.
 * Project: PanoramaViewer
 */

class BoardingActivity: AppCompatActivity() {
    private lateinit var binding: ActivityBoardingBinding
    private lateinit var bottomSheetBinding: BottomSheetBinding
    private var SELECT_PICTURE_CODE = 200
    private lateinit var mAdapter: MyAdapter
    private lateinit var mBottomSheetDialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomSheetDialog()
        initAdapter()
        clickListeners()

    }

    private fun initAdapter() {
        mAdapter = MyAdapter { url ->
            startActivityWithUrl(url)
        }
        bottomSheetBinding.recyclerView.adapter = mAdapter

        val list = listOf(
            MyData(1, "Cagliari city 1", "https://github.com/Zulfiddinovich/Panorama-Foto-Video-Viewer/blob/master/media/360videos/Cagliari%20city.mp4?raw=true", MediaType.Video),
            MyData(2, "Cagliari City 2", "https://github.com/Zulfiddinovich/Panorama-Foto-Video-Viewer/blob/master/media/360videos/Cagliari%20City%202.mp4?raw=true", MediaType.Video),
            MyData(3, "Cagliari city 3", "https://github.com/Zulfiddinovich/Panorama-Foto-Video-Viewer/blob/master/media/360photos/Cagliari%204.jpg?raw=true", MediaType.Photo),
            MyData(4, "Cagliari city 4", "https://github.com/Zulfiddinovich/Panorama-Foto-Video-Viewer/blob/master/media/360photos/Cagliari%205.jpg?raw=true", MediaType.Photo),
            MyData(5, "Cagliari University 1", "https://github.com/Zulfiddinovich/Panorama-Foto-Video-Viewer/blob/master/media/360photos/Universit%C3%A0%20di%20Cagliari%201.jpg?raw=true", MediaType.Photo),
            MyData(6, "Cagliari University 2", "https://github.com/Zulfiddinovich/Panorama-Foto-Video-Viewer/blob/master/media/360photos/Cagliari%202.jpg?raw=true", MediaType.Photo),
            MyData(7, "Cagliari University 3", "https://github.com/Zulfiddinovich/Panorama-Foto-Video-Viewer/blob/master/media/360photos/Cagliari%203.jpg?raw=true", MediaType.Photo),
            MyData(8, "Colosseum in Rome", "https://github.com/Zulfiddinovich/Panorama-Foto-Video-Viewer/blob/master/media/360videos/Colosseum%20in%20Rome.mp4?raw=true", MediaType.Video),
            MyData(9, "Colloseum, Rome", "https://github.com/Zulfiddinovich/Panorama-Foto-Video-Viewer/blob/master/media/360videos/Inside%20Colosseo%2C%20Rome%2C%20Italy.mp4?raw=true", MediaType.Video),
            MyData(10, "Tashkent 1", "https://github.com/Zulfiddinovich/Panorama-Foto-Video-Viewer/blob/master/media/360photos/Tashkent%201.jpg?raw=true", MediaType.Photo),
            MyData(11, "Tashkent 2", "?https://github.com/Zulfiddinovich/Panorama-Foto-Video-Viewer/blob/master/media/360photos/Tashkent%202.jpgraw=true", MediaType.Photo),
            MyData(12, "Tashkent 3", "https://github.com/Zulfiddinovich/Panorama-Foto-Video-Viewer/blob/master/media/360photos/Tashkent%203.jpg?raw=true", MediaType.Photo),
            MyData(13, "Tashkent 4", "https://github.com/Zulfiddinovich/Panorama-Foto-Video-Viewer/blob/master/media/360photos/Tashkent%204.jpg?raw=true", MediaType.Photo),
            MyData(14, "Tashkent 5", "https://github.com/Zulfiddinovich/Panorama-Foto-Video-Viewer/blob/master/media/360photos/Tashkent%205.jpg?raw=true", MediaType.Photo),
            MyData(15, "MINOR JOME MASJIDI", "https://github.com/Zulfiddinovich/Panorama-Foto-Video-Viewer/blob/master/media/360videos/MINOR%20JOME%20MASJIDI.mp4?raw=true", MediaType.Video),
            MyData(16, "ISLOM OTA JOME MASJIDI", "https://github.com/Zulfiddinovich/Panorama-Foto-Video-Viewer/blob/master/media/360videos/ISLOM%20OTA%20JOME%20MASJIDI.mp4?raw=true", MediaType.Video),
            MyData(17, "BUXORO ARK SAROYI", "https://github.com/Zulfiddinovich/Panorama-Foto-Video-Viewer/blob/master/media/360videos/BUXORO%20ARK%20SAROYI.mp4?raw=true", MediaType.Video),
            MyData(18, "KOKCHA JOME MASJIDI", "https://github.com/Zulfiddinovich/Panorama-Foto-Video-Viewer/blob/master/media/360videos/KOKCHA%20JOME%20MASJIDI.mp4?raw=true", MediaType.Video),
            MyData(19, "HAST-IMOM MAJMUASI", "https://github.com/Zulfiddinovich/Panorama-Foto-Video-Viewer/blob/master/media/360videos/HAST-IMOM%20MAJMUASI.mp4?raw=true", MediaType.Video),
            MyData(20, "Buxoro", "https://github.com/Zulfiddinovich/Panorama-Foto-Video-Viewer/blob/master/media/360videos/Buxoro.mp4?raw=true", MediaType.Video),
            MyData(21, "OQSAROY MAJMUASI", "https://github.com/Zulfiddinovich/Panorama-Foto-Video-Viewer/blob/master/media/360videos/OQSAROY%20MAJMUASI.mp4?raw=true", MediaType.Video),
            MyData(22, "KOKALDOSH MADRASASI", "https://github.com/Zulfiddinovich/Panorama-Foto-Video-Viewer/blob/master/media/360videos/KOKALDOSH%20MADRASASI.mp4?raw=true", MediaType.Video),
        )
        mAdapter.submitList(list)
    }

    private fun clickListeners() {
        binding.openVideoGalButton.setOnClickListener {
            openLocalMediaPicker()
        }

        binding.networkButton.setOnClickListener {
            mBottomSheetDialog.show()
        }

    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


    private fun openLocalMediaPicker() {

        // create an instance of the
        // intent of the type image
        val i = Intent()
        i.setType("*/*")
        i.setAction(Intent.ACTION_GET_CONTENT)

        // pass the constant to compare it
        // with the returned requestCode
        ActivityCompat.startActivityForResult(this, Intent.createChooser(i, "Select Picture"), SELECT_PICTURE_CODE, null)
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE_CODE) {
                // Get the url of the image from data
                val uri = data?.data

                if (uri != null){
                    startActivityWithUri(uri)
                }
            }
        }
    }

    private fun initBottomSheetDialog() {
        bottomSheetBinding = BottomSheetBinding.inflate(layoutInflater)
        mBottomSheetDialog = BottomSheetDialog(this)
        mBottomSheetDialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.showBt.setOnClickListener {

            val url: String = bottomSheetBinding.urlET.text.toString()
            bottomSheetBinding.urlET.setText("")

            startActivityWithUrl(url)
        }
    }

    private fun startActivityWithUrl(url: String) {
        mBottomSheetDialog.dismiss()
        App.PLAY_MEDIA_SOURCE = 2
        App.mMediaLink = url
        startMainActivity()
    }
    private fun startActivityWithUri(uri: Uri) {
        App.PLAY_MEDIA_SOURCE = 1
        App.mPickMediaUri = Uri.parse(FileUtils.getLocalPath(this, uri))
        startMainActivity()
    }
}