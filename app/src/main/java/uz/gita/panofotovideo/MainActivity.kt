package uz.gita.panofotovideo

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
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

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            binding.mediaView.loadMedia(intent)
        } else {
            Toast.makeText(this, R.string.permission_warning, Toast.LENGTH_SHORT).show()
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = VideoActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.videoUiContainer.videoUiView.setVrIconClickListener { startVrActivity() }

        binding.vrButton.setOnClickListener { startVrActivity() }

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container_view)) { _, insets ->
//            val params = binding.vrFab.layoutParams as ConstraintLayout.LayoutParams
//            params.topMargin = insets.systemWindowInsetTop
//            params.bottomMargin = insets.systemWindowInsetBottom
//            params.leftMargin = insets.systemWindowInsetLeft
//            params.rightMargin = insets.systemWindowInsetRight
//            insets.consumeSystemWindowInsets()
//        }

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
        super.onDestroy()
    }
}
