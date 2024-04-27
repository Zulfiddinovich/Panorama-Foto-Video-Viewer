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
            MyData(1, "Cagliari University", "https://github.com/Zulfiddinovich/Temp/blob/main/Cagliari%203.jpg?raw=true", MediaType.Photo),
            MyData(2, "Colloseum", "https://github.com/Zulfiddinovich/Temp/raw/main/360%20video-%20Inside%20Colosseo,%20Rome,%20Italy.mp4", MediaType.Video),
            MyData(3, "House", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/72/MK_30645-58_Stadtschloss_Wiesbaden.jpg/1280px-MK_30645-58_Stadtschloss_Wiesbaden.jpg", MediaType.Photo),
            MyData(4, "House", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/72/MK_30645-58_Stadtschloss_Wiesbaden.jpg/1280px-MK_30645-58_Stadtschloss_Wiesbaden.jpg", MediaType.Photo),
            MyData(5, "House", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/72/MK_30645-58_Stadtschloss_Wiesbaden.jpg/1280px-MK_30645-58_Stadtschloss_Wiesbaden.jpg", MediaType.Photo),
            MyData(6, "House", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/72/MK_30645-58_Stadtschloss_Wiesbaden.jpg/1280px-MK_30645-58_Stadtschloss_Wiesbaden.jpg", MediaType.Photo),
            MyData(7, "House", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/72/MK_30645-58_Stadtschloss_Wiesbaden.jpg/1280px-MK_30645-58_Stadtschloss_Wiesbaden.jpg", MediaType.Photo),
            MyData(8, "House", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/72/MK_30645-58_Stadtschloss_Wiesbaden.jpg/1280px-MK_30645-58_Stadtschloss_Wiesbaden.jpg", MediaType.Photo),
            MyData(8, "House", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/72/MK_30645-58_Stadtschloss_Wiesbaden.jpg/1280px-MK_30645-58_Stadtschloss_Wiesbaden.jpg", MediaType.Photo),
            MyData(8, "House", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/72/MK_30645-58_Stadtschloss_Wiesbaden.jpg/1280px-MK_30645-58_Stadtschloss_Wiesbaden.jpg", MediaType.Photo),
            MyData(8, "House", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/72/MK_30645-58_Stadtschloss_Wiesbaden.jpg/1280px-MK_30645-58_Stadtschloss_Wiesbaden.jpg", MediaType.Photo),
            MyData(8, "House", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/72/MK_30645-58_Stadtschloss_Wiesbaden.jpg/1280px-MK_30645-58_Stadtschloss_Wiesbaden.jpg", MediaType.Photo),
            MyData(8, "House", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/72/MK_30645-58_Stadtschloss_Wiesbaden.jpg/1280px-MK_30645-58_Stadtschloss_Wiesbaden.jpg", MediaType.Photo),
            MyData(8, "House", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/72/MK_30645-58_Stadtschloss_Wiesbaden.jpg/1280px-MK_30645-58_Stadtschloss_Wiesbaden.jpg", MediaType.Photo),
            MyData(9, "House", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/72/MK_30645-58_Stadtschloss_Wiesbaden.jpg/1280px-MK_30645-58_Stadtschloss_Wiesbaden.jpg", MediaType.Photo),
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