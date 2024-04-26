package uz.gita.panofotovideo

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.vr.ndk.base.DaydreamApi
import uz.gita.panofotovideo.databinding.VideoActivityBinding
import uz.gita.panofotovideo.rendering.Mesh
import uz.gita.panofotovideo.util.FileUtils


/**
 * Basic Activity to hold [MonoscopicView] and render a 360 video in 2D.
 *
 * Most of this Activity's code is related to Android & VR permission handling. The real work is in
 * MonoscopicView.
 *
 * The default intent for this Activity will load a 360 placeholder panorama. For more options on
 * how to load other media using a custom Intent, see [MediaLoader].
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: VideoActivityBinding
    private var SELECT_PICTURE_CODE = 200

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
//            mMonoscopView.loadMedia(intent)
        } else {
            Toast.makeText(this, R.string.permission_warning, Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("Range")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = VideoActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clickListeners()


//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container_view)) { _, insets ->
//            val params = binding.vrFab.layoutParams as ConstraintLayout.LayoutParams
//            params.topMargin = insets.systemWindowInsetTop
//            params.bottomMargin = insets.systemWindowInsetBottom
//            params.leftMargin = insets.systemWindowInsetLeft
//            params.rightMargin = insets.systemWindowInsetRight
//            insets.consumeSystemWindowInsets()
//        }


    }

    private fun clickListeners() {
        binding.videoUiContainer.videoUiView.setVrIconClickListener { startVrActivity() }

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
        }


        binding.vrButton.setOnClickListener { startVrActivity() }

        binding.mediaView.onPositionTouched { position ->
            binding.bottomLinear.isVisible = !binding.bottomLinear.isVisible
        }

        binding.mediaView.initialize(binding.videoUiContainer.videoUiView)

        checkReadPermissionThenOpen()
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
                }
            }
        }
    }

    private fun checkReadPermissionThenOpen() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
            (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        ) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            binding.mediaView.loadMedia(intent)
        }
    }

    private fun startVrActivity() {
        // Convert the Intent used to launch the 2D Activity into one that can launch the VR
        // Activity. This flow preserves the extras and data in the Intent.
        val api = DaydreamApi.create(this)
        if (api != null) {
            // Launch the VR Activity with the proper intent.
            api.launchInVr(
                DaydreamApi.createVrIntent(ComponentName(this, VrActivity::class.java))
                    .setData(intent.data)
                    .putExtra(MediaLoader.MEDIA_FORMAT_KEY, intent.getIntExtra(MediaLoader.MEDIA_FORMAT_KEY, Mesh.MEDIA_MONOSCOPIC))
            )
            api.close()
        } else {
            // Fall back for devices that don't have Google VR Services. This flow should only
            // be used for older Cardboard devices.
            val intent = Intent(intent).setClass(this, VrActivity::class.java)
            intent.removeCategory(Intent.CATEGORY_LAUNCHER)
            intent.flags = 0 // Clear any flags from the previous intent.
            startActivity(intent)
        }

        // See VrVideoActivity's launch2dActivity() for more info about why this finish() call
        // may be required.
        finish()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        recreate()
    }

    override fun onResume() {
        super.onResume()
        binding.mediaView.onResume()
    }

    override fun onPause() {
        // MonoscopicView is a GLSurfaceView so it needs to pause & resume rendering. It's also
        // important to pause MonoscopicView's sensors & the video player.
        binding.mediaView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        binding.mediaView.destroy()

        // generates - crash with "No such files"
        cleanCache();
        super.onDestroy()
    }

    private fun cleanCache() {
        val directory = cacheDir.listFiles();
        if (directory != null) {
            for (file in directory) {
                file.delete();
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
