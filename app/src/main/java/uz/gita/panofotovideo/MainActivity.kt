package uz.gita.panofotovideo

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.vr.ndk.base.DaydreamApi
import uz.gita.panofotovideo.databinding.VideoActivityBinding
import uz.gita.panofotovideo.rendering.Mesh


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
    private lateinit var photoPicker: ActivityResultLauncher<PickVisualMediaRequest>

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
        photoPicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            var filePath = ""
            if (uri != null && uri.scheme.equals("content")) {
                val cursor = contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                    cursor.close()

                    // this is PhotoPicker Uri like this: "/sdcard/.transforms/synthetic/picker/0/com.android.providers.media.photopicker/media/1000065314.jpg"
                    // but it won't be available after recreation of Activity when navigation to VRActivity, that's why I generate cached copy image or video
                    App.mPickMediaUri = Uri.parse(filePath)
                }
            }
        }


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

        binding.openImageGalButton.setOnClickListener {
            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.openVideoGalButton.setOnClickListener {
            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
        }

        binding.networkButton.setOnClickListener { bottomSheetDialogOpen().show() }


        binding.vrButton.setOnClickListener { startVrActivity() }

        binding.mediaView.onPositionTouched { position ->
            binding.bottomLinear.isVisible = !binding.bottomLinear.isVisible
        }

        binding.mediaView.initialize(binding.videoUiContainer.videoUiView)

        checkReadPermissionThenOpen()
    }

    private fun checkReadPermissionThenOpen() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
            (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
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
            api.launchInVr(DaydreamApi.createVrIntent(ComponentName(this, VrActivity::class.java))
                .setData(intent.data)
                .putExtra(MediaLoader.MEDIA_FORMAT_KEY, intent.getIntExtra(MediaLoader.MEDIA_FORMAT_KEY, Mesh.MEDIA_MONOSCOPIC)))
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
        if(directory != null){
            for (file in directory){
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
