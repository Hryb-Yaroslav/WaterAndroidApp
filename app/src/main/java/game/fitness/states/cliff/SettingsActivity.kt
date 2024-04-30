package game.fitness.states.cliff

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible

class SettingsActivity : AppCompatActivity() {
    private var goToNewActivity = false

    private var musicService: MusicService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            isBound = true
            val sharedPreferencesForMusic = getSharedPreferences("SoundState", Context.MODE_PRIVATE)
            val sound = sharedPreferencesForMusic.getInt("musicSound", 100)
            setVolume(sound)
            val sharedPreferences = getSharedPreferences("SoundState", Context.MODE_PRIVATE)
            if(sharedPreferences.getBoolean("boolKeyForSound", true)){
                onPlayButtonClick()
            }
            else{onPauseButtonClick()}
        }
        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }
    private fun bindMusicService() {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }
    fun onPlayButtonClick() {
        musicService?.play()
    }
    fun onPauseButtonClick() {
        musicService?.pause()
    }
    fun setVolume(value: Int){
        musicService?.setMusicVolume(value)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val click = MediaPlayer.create(this, R.raw.button_sound)
        val sharedPreferencesForMusic = getSharedPreferences("SoundState", Context.MODE_PRIVATE)
        val clickSound = sharedPreferencesForMusic.getFloat("buttonSound", 1f)
        click.setVolume(clickSound, clickSound)
        bindMusicService()

        val buttonHistory: ImageButton = findViewById(R.id.backButton)
        val sbBrigthness: SeekBar = findViewById(R.id.brigthnessSeekBar)
        val seekBarMusic: SeekBar = findViewById(R.id.musicSeekBar)
        val sbSound: SeekBar = findViewById(R.id.soundSeekBar)

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutParams = window.attributes
        sbBrigthness.progress  = (layoutParams.screenBrightness * 100).toInt()
        Log.d("TAG", "onCreate: sb progress: " + sbBrigthness.progress + " lp: " + layoutParams.screenBrightness)

        click.setVolume(clickSound, clickSound)
        sbSound.progress = (clickSound * 100).toInt()
        sbSound.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val soundValue = progress / 100f
                click.setVolume(soundValue, soundValue)
                val editor = sharedPreferencesForMusic.edit()
                editor.putFloat("buttonSound", soundValue)
                editor.apply()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                click.start()
                click.seekTo(0)
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        val sound = sharedPreferencesForMusic.getInt("musicSound", 100)
        setVolume(sound)
        seekBarMusic.progress = sound
        seekBarMusic.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                setVolume(progress)
                val editor = sharedPreferencesForMusic.edit()
                editor.putInt("musicSound", progress)
                editor.apply()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        val sharedPreferencesForBrigthness = getSharedPreferences("Brigthness", Context.MODE_PRIVATE)
        val Brigthness = sharedPreferencesForBrigthness.getFloat("brigthnessValue", 1f)
//        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        val layoutParams = window.attributes
        layoutParams.screenBrightness = Brigthness
        window.attributes = layoutParams
        sbBrigthness.progress = (Brigthness * 255f).toInt()
        sbBrigthness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val brightnessValue =
                    progress / 255f // перетворення значення в діапазоні 0.0 до 1.0
                val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val layoutParams = window.attributes
                layoutParams.screenBrightness = brightnessValue
                window.attributes = layoutParams
                val editor = sharedPreferencesForBrigthness.edit()
                editor.putFloat("brigthnessValue", brightnessValue)
                editor.apply()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        buttonHistory.setOnClickListener {
            click.start()
            click.seekTo(0)
            goToNewActivity = true
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }


        hideUi()
    }


    override fun onPause() {
        super.onPause()
        hideUi()
        if(goToNewActivity) goToNewActivity = false
        else onPauseButtonClick()
    }

    override fun onResume() {
        super.onResume()
        hideUi()
        val sharedPreferences = getSharedPreferences("SoundState", Context.MODE_PRIVATE)
        if(sharedPreferences.getBoolean("boolKeyForSound", true))
            onPlayButtonClick()
    }
    private fun hideUi() {
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LOW_PROFILE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val insetsController = ViewCompat.getWindowInsetsController(window.decorView)
            insetsController?.let {
                it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                it.hide(WindowInsetsCompat.Type.systemBars())
            }
        } else {
            val decorView = window.decorView
            decorView.systemUiVisibility = uiOptions
        }
    }
}