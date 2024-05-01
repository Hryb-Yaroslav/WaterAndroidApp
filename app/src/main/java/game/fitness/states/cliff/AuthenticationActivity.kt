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
import android.text.InputType
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible

class AuthenticationActivity : AppCompatActivity() {
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


    var eyeState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        val click = MediaPlayer.create(this, R.raw.button_sound)
        val serviceIntent = Intent(this, MusicService::class.java)
        startService(serviceIntent)
        bindMusicService()
        val sharedPreferencesForMusic = getSharedPreferences("SoundState", Context.MODE_PRIVATE)
        val clickSound = sharedPreferencesForMusic.getFloat("buttonSound", 1f)
        click.setVolume(clickSound, clickSound)


        val sharedPreferencesForBrigthness = getSharedPreferences("Brigthness", Context.MODE_PRIVATE)
        val Brigthness = sharedPreferencesForBrigthness.getFloat("brigthnessValue", 1f)
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutParams = window.attributes
        layoutParams.screenBrightness = Brigthness
        window.attributes = layoutParams

        val passField: EditText = findViewById(R.id.setPasswordText)
        val loginField: EditText = findViewById(R.id.setLoginText)

        val textLogin: TextView = findViewById(R.id.textView9)
        val textCreateAccount: TextView = findViewById(R.id.textView90)
        val textCreate: TextView = findViewById(R.id.textView901)
        val textBeck: TextView = findViewById(R.id.textView902)

        val buttonEye: ImageButton = findViewById(R.id.eyeButton)
        val buttonLogin: ImageButton = findViewById(R.id.loginButton)
        val buttonCreateAccount: ImageButton = findViewById(R.id.createAccountButton)
        val buttonCreate: ImageButton = findViewById(R.id.logOutButton)
        val buttonBack: ImageButton = findViewById(R.id.backButtonlog)

        val buttonExit: ImageButton = findViewById(R.id.exitButton)


        val sharedPreferencesForCurrentUser = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val getSharedPreferencesForCurrentUsers = sharedPreferencesForCurrentUser.getString("currentData", "")
        if (getSharedPreferencesForCurrentUsers != ""){
            val name = getSharedPreferencesForCurrentUsers?.split(":")?.get(0)
            Toast.makeText(this, "Welcome Back $name", Toast.LENGTH_SHORT).show()
            goToNewActivity = true
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }


        buttonEye.setOnClickListener {
            eyeState = if (eyeState) {
                buttonEye.setImageResource(R.drawable.ic_eye)
                passField.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passField.setSelection(passField.text.length)
                false
            } else {
                buttonEye.setImageResource(R.drawable.ic_eye2)
                passField.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                passField.setSelection(passField.text.length)
                true
            }
        }
        buttonLogin.setOnClickListener {
            click.start()
            click.seekTo(0)
            if (passField.text.toString() != "" && loginField.text.toString() != "") {
                val sharedPreferencesForUsers = getSharedPreferences("UsersData", Context.MODE_PRIVATE)
                val getSharedPreferencesForUsersData = sharedPreferencesForUsers.getStringSet("data", emptySet())

                var check = true
                getSharedPreferencesForUsersData?.forEach {
                    val data = it.split(":")
                    if (data[0] == loginField.text.toString() && data[1] == passField.text.toString()){
                        check = false
                        Toast.makeText(this, "Uou Enter As " + data[0], Toast.LENGTH_SHORT).show()

                        val editor = sharedPreferencesForCurrentUser.edit()
                        editor.putString("currentData", it)
                        editor.apply()

                        goToNewActivity = true
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        finish()
                    }
                }
                if (check) Toast.makeText(this, "Wrong Login Or Password", Toast.LENGTH_SHORT).show()
            }
            else if (loginField.text.toString() == "") Toast.makeText(this, "Enter Login", Toast.LENGTH_SHORT).show()
            else if (passField.text.toString() == "") Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
            else Toast.makeText(this, "Incorrect data", Toast.LENGTH_SHORT).show()
        }
        buttonCreate.setOnClickListener {
            if (passField.text.toString() != "" && loginField.text.toString() != "") {
                val sharedPreferencesForUsers = getSharedPreferences("UsersData", Context.MODE_PRIVATE)
                val getSharedPreferencesForUsersData = sharedPreferencesForUsers.getStringSet("data", emptySet())

                val users = getSharedPreferencesForUsersData?.toMutableSet()

                val userData = loginField.text.toString() + ":" + passField.text.toString()

                users?.add(userData)

                val editor = sharedPreferencesForUsers.edit()
                editor.putStringSet("data", users?.toSet())
                editor.apply()

                Toast.makeText(this, "You Create Account With Name " + loginField.text.toString(), Toast.LENGTH_SHORT).show()

                loginField.text.clear()
                passField.text.clear()
            }
            else if (loginField.text.toString() == "") Toast.makeText(this, "Enter Login", Toast.LENGTH_SHORT).show()
            else if (passField.text.toString() == "") Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
            else Toast.makeText(this, "Incorrect data", Toast.LENGTH_SHORT).show()
        }
        buttonCreateAccount.setOnClickListener {
            textLogin.isVisible = false
            buttonLogin.isVisible = false

            textCreateAccount.isVisible = false
            buttonCreateAccount.isVisible = false

            textCreate.isVisible = true
            buttonCreate.isVisible = true

            textBeck.isVisible = true
            buttonBack.isVisible = true

            loginField.hint = "Create Login"
            passField.hint = "Create Password"
        }
        buttonBack.setOnClickListener {

            textLogin.isVisible = true
            buttonLogin.isVisible = true

            textCreateAccount.isVisible = true
            buttonCreateAccount.isVisible = true

            textCreate.isVisible = false
            buttonCreate.isVisible = false

            textBeck.isVisible = false
            buttonBack.isVisible = false

            loginField.hint = "Enter Login"
            passField.hint = "Enter Password"
        }

        buttonExit.setOnClickListener {
            click.start()
            click.seekTo(0)
            stopService(serviceIntent)
            finishAffinity()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }


        hideUi()
    }




    override fun onPause() {
        super.onPause()
        if(goToNewActivity) goToNewActivity = false
        else onPauseButtonClick()
    }
    override fun onResume() {
        super.onResume()
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