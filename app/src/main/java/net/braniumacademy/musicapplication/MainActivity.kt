package net.braniumacademy.musicapplication

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.DisplayMetrics.DENSITY_DEFAULT
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import net.braniumacademy.musicapplication.data.repository.song.SongRepositoryImpl
import net.braniumacademy.musicapplication.databinding.ActivityMainBinding
import net.braniumacademy.musicapplication.utils.MusicAppUtils
import net.braniumacademy.musicapplication.utils.PermissionUtils
import net.braniumacademy.musicapplication.utils.SharedObjectUtils
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var currentSongLoaded = false

    @Inject
    lateinit var songRepository: SongRepositoryImpl

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                val snackBar = Snackbar.make(
                    binding.root.rootView,
                    getString(R.string.permission_denied),
                    Snackbar.LENGTH_LONG
                )
                snackBar.setAnchorView(R.id.nav_view)
                snackBar.show()
            } else {
                PermissionUtils.grantPermission()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNav()
        setupComponents()
        observeData()
    }

    override fun onResume() {
        super.onResume()
        SharedObjectUtils.playingSong.observe(this) {
            if (it.song != null) {
                binding.fcvMiniPlayer.visibility = VISIBLE
            } else {
                binding.fcvMiniPlayer.visibility = GONE
            }
        }
    }

    override fun onStop() {
        super.onStop()
        saveCurrentSong()
    }

    private fun setupBottomNav() {
        val navView: BottomNavigationView = binding.navView
        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment)
        val navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_library, R.id.navigation_discovery
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun setupComponents() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                MusicAppUtils.DENSITY = windowMetrics.density
            } else {
                MusicAppUtils.DENSITY = 1f * windowMetrics.bounds.width() / DENSITY_DEFAULT
            }
        } else {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            MusicAppUtils.DENSITY = displayMetrics.density
        }
        if (!MusicAppUtils.isConfigChanged) {
            SharedObjectUtils.initPlaylist()
        }
        sharedPreferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE)
    }

    private fun observeData() {
        SharedObjectUtils.isReady.observe(this) { ready ->
            if (ready && !currentSongLoaded) {
                loadPreviousSessionSong()
                currentSongLoaded = true
            }
        }
        PermissionUtils
            .permissionAsked
            .observe(this) {
                if (it) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        checkPostNotificationPermission()
                    }
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPostNotificationPermission() {
        val permission = android.Manifest.permission.POST_NOTIFICATIONS
        val isPermissionGranted = ActivityCompat
            .checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        if (isPermissionGranted) {
            PermissionUtils.grantPermission()
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            val snackBar = Snackbar.make(
                binding.root.rootView,
                getString(R.string.permission_denied),
                Snackbar.LENGTH_LONG
            )
            snackBar.setAction(R.string.action_agree) {
                permissionLauncher.launch(permission)
            }
            snackBar.setAnchorView(R.id.nav_view)
            snackBar.show()
        } else {
            permissionLauncher.launch(permission)
        }
    }

    private fun saveCurrentSong() {
        val playingSong = SharedObjectUtils.playingSong.value
        playingSong?.let {
            val song = it.song
            song?.let { currentSong ->
                sharedPreferences.edit()
                    .putString(PREF_SONG_ID, currentSong.id)
                    .putString(PREF_PLAYLIST_NAME, it.playlist?.name)
                    .apply()
            }
        }
    }

    private fun loadPreviousSessionSong() {
        val songId = sharedPreferences.getString(PREF_SONG_ID, null)
        val playlistName = sharedPreferences.getString(PREF_PLAYLIST_NAME, null)
        SharedObjectUtils.loadPreviousSessionSong(songId, playlistName, songRepository)
    }

    companion object {
        const val PREF_SONG_ID = "PREF_SONG_ID"
        const val PREF_PLAYLIST_NAME = "PREF_PLAYLIST_NAME"
        const val PREF_FILE_NAME = "net.braniumacademy.musicapplication.preff_file"
    }
}