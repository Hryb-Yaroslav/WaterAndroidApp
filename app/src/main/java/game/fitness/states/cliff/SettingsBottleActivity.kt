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

class SettingsBottleActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_settings_bottle)
        val click = MediaPlayer.create(this, R.raw.button_sound)
        bindMusicService()
        val sharedPreferencesForMusic = getSharedPreferences("SoundState", Context.MODE_PRIVATE)
        val clickSound = sharedPreferencesForMusic.getFloat("buttonSound", 1f)
        click.setVolume(clickSound, clickSound)

        val buttonHistory: ImageButton = findViewById(R.id.backButton)
        val buttonAddNewSize: ImageButton = findViewById(R.id.addNewSizeButton)
        val textSetIntake: EditText = findViewById(R.id.setIntakeText)

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutParams = window.attributes
        //sbBrigthness.progress  = (layoutParams.screenBrightness * 100).toInt()


        val sharedPreferencesForCurrentUser = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userdata = sharedPreferencesForCurrentUser.getString("currentData", "")

        val sharedPreferences = getSharedPreferences("Intake$userdata", Context.MODE_PRIVATE)
        textSetIntake.setText(sharedPreferences.getInt("maxIntake", 0).toString())
        textSetIntake.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val textSetIntakeText = if ( textSetIntake.text.toString() != "") textSetIntake.text.toString().toInt()
                                        else 0
                val editor = sharedPreferences.edit()
                editor.putInt("maxIntake", textSetIntakeText)
                editor.apply()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        val sound = sharedPreferencesForMusic.getInt("musicSound", 100)
        setVolume(sound)

        val sharedPreferencesForBrigthness = getSharedPreferences("Brigthness", Context.MODE_PRIVATE)
        val Brigthness = sharedPreferencesForBrigthness.getFloat("brigthnessValue", 1f)
//        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        val layoutParams = window.attributes
        layoutParams.screenBrightness = Brigthness
        window.attributes = layoutParams


        buttonAddNewSize.setOnClickListener {
            click.start()
            click.seekTo(0)
            addBottle()

            checkBottleSizeState()
        }

        buttonHistory.setOnClickListener {
            click.start()
            click.seekTo(0)
            goToNewActivity = true
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
        initializeAlleGlassSizeElements()
        listFLImageButtonDelete.forEach(){ imageButton ->
            imageButton.setOnClickListener {
                click.start()
                click.seekTo(0)
                val sharedPreferencesForBottleSize = getSharedPreferences("BottleSizePref", Context.MODE_PRIVATE)
                val editor = sharedPreferencesForBottleSize.edit()
                editor.putInt(it.contentDescription.toString() , 0)
                editor.apply()
                checkBottleSizeState()
            }
        }
        listFLImageButtonCheck.forEach {  imageButton ->
            imageButton.setOnClickListener {
                click.start()
                click.seekTo(0)
                val sharedPreferencesForBottleSize = getSharedPreferences("BottleSizePref", Context.MODE_PRIVATE)
                val editor = sharedPreferencesForBottleSize.edit()
                editor.putInt("currentActive", it.contentDescription.toString().toInt())
                editor.apply()
                checkBottleSizeState()
            }
        }
        listFLSizeBottleText.forEach { EditTextField ->
            EditTextField.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val textSetIntakeText = EditTextField.text.toString()
                    val finText = if (textSetIntakeText != "") textSetIntakeText.toInt()
                    else 0
                    val sharedPreferencesForBottleSize = getSharedPreferences("BottleSizePref", Context.MODE_PRIVATE)
                    val editor = sharedPreferencesForBottleSize.edit()
                    editor.putInt(EditTextField.contentDescription.toString(), finText)
                    editor.apply()
                    //checkBottleSizeState()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            })
        }


        checkBottleSizeState()
        hideUi()
    }
    private var countOfCurrentActiveBottle = 0
    private lateinit var listFL :List<FrameLayout>
    private lateinit var listFLImageButtonCheck :List<ImageButton>
    private lateinit var listFLImageButtonDelete :List<ImageButton>
    private lateinit var listFLSizeBottleText :List<EditText>
    private fun addBottle(){
        val allNumbers = listOf("firstBottle", "secondBottle", "thirdBottle",
                "fourthBottle", "fifthBottle", "sixthBottle", "seventhBottle")
        if (countOfCurrentActiveBottle < 7){
            val sharedPreferencesForBottleSize = getSharedPreferences("BottleSizePref", Context.MODE_PRIVATE)
            val editor = sharedPreferencesForBottleSize.edit()
            run breaking@ {
                allNumbers.forEach(){
                    if(sharedPreferencesForBottleSize.getInt(it, 0) <= 0){
                        editor.putInt(it, 100)
                        return@breaking
                    }
                }
            }
            editor.apply()
        }
        else Toast.makeText(this, "You already created maximum amount of glasses", Toast.LENGTH_SHORT).show()
    }
    private fun checkBottleSizeState(){
        val firstEditText: EditText = findViewById(R.id.firstEditTextText)
        val secondEditText: EditText = findViewById(R.id.secondEditTextText)
        val thirdEditText: EditText = findViewById(R.id.thirdEditTextText)
        val fourthEditText: EditText = findViewById(R.id.fourthEditTextText)
        val fifthEditText: EditText = findViewById(R.id.fifthEditTextText)
        val sixthEditText: EditText = findViewById(R.id.sixthEditTextText)
        val seventhEditText: EditText = findViewById(R.id.seventhEditTextText)

        val sharedPreferencesForBottleSize = getSharedPreferences("BottleSizePref", Context.MODE_PRIVATE)

        val firstBottleSize = sharedPreferencesForBottleSize.getInt("firstBottle", 0)
        val secondBottleSize = sharedPreferencesForBottleSize.getInt("secondBottle", 0)
        val thirdBottleSize = sharedPreferencesForBottleSize.getInt("thirdBottle", 0)
        val fourthBottleSize = sharedPreferencesForBottleSize.getInt("fourthBottle", 0)
        val fifthBottleSize = sharedPreferencesForBottleSize.getInt("fifthBottle", 0)
        val sixthBottleSize = sharedPreferencesForBottleSize.getInt("sixthBottle", 0)
        val seventhBottleSize = sharedPreferencesForBottleSize.getInt("seventhBottle", 0)

        countOfCurrentActiveBottle = 0
        if (firstBottleSize > 0){
            listFL[0].isVisible = true
            listFL[0].isEnabled = true
            countOfCurrentActiveBottle += 1
            firstEditText.setText(firstBottleSize.toString())
        }
        else{
            listFL[0].isVisible = false
            listFL[0].isEnabled = false
        }
        if (secondBottleSize > 0){
            listFL[1].isVisible = true
            listFL[1].isEnabled = true
            countOfCurrentActiveBottle += 1
            secondEditText.setText(secondBottleSize.toString())
        }
        else{
            listFL[1].isVisible = false
            listFL[1].isEnabled = false
        }
        if (thirdBottleSize > 0){
            listFL[2].isVisible = true
            listFL[2].isEnabled = true
            countOfCurrentActiveBottle += 1
            thirdEditText.setText(thirdBottleSize.toString())
        }
        else{
            listFL[2].isVisible = false
            listFL[2].isEnabled = false
        }
        if (fourthBottleSize > 0){
            listFL[3].isVisible = true
            listFL[3].isEnabled = true
            countOfCurrentActiveBottle += 1
            fourthEditText.setText(fourthBottleSize.toString())
        }
        else{
            listFL[3].isVisible = false
            listFL[3].isEnabled = false
        }
        if (fifthBottleSize > 0){
            listFL[4].isVisible = true
            listFL[4].isEnabled = true
            countOfCurrentActiveBottle += 1
            fifthEditText.setText(fifthBottleSize.toString())
        }
        else{
            listFL[4].isVisible = false
            listFL[4].isEnabled = false
        }
        if (sixthBottleSize > 0){
            listFL[5].isVisible = true
            listFL[5].isEnabled = true
            countOfCurrentActiveBottle += 1
            sixthEditText.setText(sixthBottleSize.toString())
        }
        else{
            listFL[5].isVisible = false
            listFL[5].isEnabled = false
        }
        if (seventhBottleSize > 0){
            listFL[6].isVisible = true
            listFL[6].isEnabled = true
            countOfCurrentActiveBottle += 1
            seventhEditText.setText(seventhBottleSize.toString())
        }
        else{
            listFL[6].isVisible = false
            listFL[6].isEnabled = false
        }
        checkCurrentSelectedBottle()
    }

    private fun checkCurrentSelectedBottle(){
        val buttonFirstButton: ImageButton = findViewById(R.id.firstButtonCheck)
        val buttonSecondButton: ImageButton = findViewById(R.id.secondButtonCheck)
        val buttonThirdButton: ImageButton = findViewById(R.id.thirdButtonCheck)
        val buttonFourthButton: ImageButton = findViewById(R.id.fourthButtonCheck)
        val buttonFifthButton: ImageButton = findViewById(R.id.fifthButtonCheck)
        val buttonSixthButton: ImageButton = findViewById(R.id.sixthButtonCheck)
        val buttonSeventhButton: ImageButton = findViewById(R.id.seventhButtonCheck)

        val sharedPreferencesForBottleSize = getSharedPreferences("BottleSizePref", Context.MODE_PRIVATE)
        val currentActiveBottle = sharedPreferencesForBottleSize.getInt("currentActive", 0)

        buttonFirstButton.setImageResource(R.drawable.ic_check_mark_clean)
        buttonSecondButton.setImageResource(R.drawable.ic_check_mark_clean)
        buttonThirdButton.setImageResource(R.drawable.ic_check_mark_clean)
        buttonFourthButton.setImageResource(R.drawable.ic_check_mark_clean)
        buttonFifthButton.setImageResource(R.drawable.ic_check_mark_clean)
        buttonSixthButton.setImageResource(R.drawable.ic_check_mark_clean)
        buttonSeventhButton.setImageResource(R.drawable.ic_check_mark_clean)
        when (currentActiveBottle) {
            1 -> {
                buttonFirstButton.setImageResource(R.drawable.ic_check_mark_fill)
            }
            2 -> {
                buttonSecondButton.setImageResource(R.drawable.ic_check_mark_fill)
            }
            3 -> {
                buttonThirdButton.setImageResource(R.drawable.ic_check_mark_fill)
            }
            4 -> {
                buttonFourthButton.setImageResource(R.drawable.ic_check_mark_fill)
            }
            5 -> {
                buttonFifthButton.setImageResource(R.drawable.ic_check_mark_fill)
            }
            6 -> {
                buttonSixthButton.setImageResource(R.drawable.ic_check_mark_fill)
            }
            7 -> {
                buttonSeventhButton.setImageResource(R.drawable.ic_check_mark_fill)
            }
        }

    }
    private fun initializeAlleGlassSizeElements(){
        listFL = listOf<FrameLayout>(
            findViewById(R.id.firstBotleElement),
            findViewById(R.id.secondBotleElement),
            findViewById(R.id.thirdBotleElement),
            findViewById(R.id.fourthBotleElement),
            findViewById(R.id.fifthBotleElement),
            findViewById(R.id.sixthBotleElement),
            findViewById(R.id.seventhBotleElement)
        )
        listFLImageButtonDelete = listOf<ImageButton>(
            findViewById(R.id.firstButtonDelete),
            findViewById(R.id.secondButtonDelete),
            findViewById(R.id.thirdButtonDelete),
            findViewById(R.id.fourthButtonDelete),
            findViewById(R.id.fifthButtonDelete),
            findViewById(R.id.sixthButtonDelete),
            findViewById(R.id.seventhButtonDelete)
        )
        listFLImageButtonCheck = listOf<ImageButton>(
            findViewById(R.id.firstButtonCheck),
            findViewById(R.id.secondButtonCheck),
            findViewById(R.id.thirdButtonCheck),
            findViewById(R.id.fourthButtonCheck),
            findViewById(R.id.fifthButtonCheck),
            findViewById(R.id.sixthButtonCheck),
            findViewById(R.id.seventhButtonCheck)
        )
        listFLSizeBottleText = listOf<EditText>(
            findViewById(R.id.firstEditTextText),
            findViewById(R.id.secondEditTextText),
            findViewById(R.id.thirdEditTextText),
            findViewById(R.id.fourthEditTextText),
            findViewById(R.id.fifthEditTextText),
            findViewById(R.id.sixthEditTextText),
            findViewById(R.id.seventhEditTextText)
        )
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