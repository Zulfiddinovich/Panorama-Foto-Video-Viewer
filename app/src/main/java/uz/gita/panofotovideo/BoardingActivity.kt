package uz.gita.panofotovideo;

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.gita.panofotovideo.databinding.ActivityBoardingBinding
import uz.gita.panofotovideo.util.FileUtils

class BoardingActivity: AppCompatActivity() {
    private lateinit var binding: ActivityBoardingBinding
    private var SELECT_PICTURE_CODE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clickListeners()

    }

    private fun clickListeners() {
        binding.openVideoGalButton.setOnClickListener {
            App.PLAY_MEDIA_SOURCE = 1
            openLocalMediaPicker()
        }

        binding.networkButton.setOnClickListener {
//            bottomSheetDialogOpen().show()
            App.PLAY_MEDIA_SOURCE = 2

            App.mMediaLink = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/72/MK_30645-58_Stadtschloss_Wiesbaden.jpg/1280px-MK_30645-58_Stadtschloss_Wiesbaden.jpg"
//            App.mMediaLink = "https://github.com/Zulfiddinovich/Temp/blob/main/Cagliari%203.jpg?raw=true"
//            App.mMediaLink = "https://github.com/Zulfiddinovich/Temp/raw/main/360%20video-%20Inside%20Colosseo,%20Rome,%20Italy.mp4"

            startMainActivity()
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
                    App.mPickMediaUri = Uri.parse(FileUtils.getLocalPath(this, uri))
                    startMainActivity()
                }
            }
        }
    }

    private fun bottomSheetDialogOpen(): BottomSheetDialog {

        val dialog = BottomSheetDialog(this)
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet, null)

//        bottomSheet.buttonSubmit.setOnClickListener { dialog.dismiss() }

        dialog.setContentView(bottomSheet)
        return dialog
    }
}