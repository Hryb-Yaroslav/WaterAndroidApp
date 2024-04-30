package game.fitness.states.cliff

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible


class MainActivity : AppCompatActivity() {
    private var goToNewActivity = false
    private val waterMeterArray = listOf(
        R.drawable.ic_progress_0,
        R.drawable.ic_progress_1,
        R.drawable.ic_progress_2,
        R.drawable.ic_progress_3,
        R.drawable.ic_progress_4,
        R.drawable.ic_progress_5,
        R.drawable.ic_progress_6,
        R.drawable.ic_progress_7,
        R.drawable.ic_progress_8,
        R.drawable.ic_progress_9,
        R.drawable.ic_progress_10
    )
    private val waterMeterState = 0

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
    private fun onPlayButtonClick() {
        musicService?.play()
    }
    private fun onPauseButtonClick() {
        musicService?.pause()
    }
    fun setVolume(value: Int){
        musicService?.setMusicVolume(value)
    }

    var menuIsActive = false
    var menuAnimationIsEnd = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val click = MediaPlayer.create(this, R.raw.button_sound)
        val serviceIntent = Intent(this, MusicService::class.java)
        startService(serviceIntent)
        bindMusicService()
        val sharedPreferencesForMusic = getSharedPreferences("SoundState", Context.MODE_PRIVATE)
        val clickSound = sharedPreferencesForMusic.getFloat("buttonSound", 1f)
        click.setVolume(clickSound, clickSound)

        val buttonSettings: ImageButton = findViewById(R.id.settingsButton)
        val buttonHistory: ImageButton = findViewById(R.id.historyButton)
        val buttonMenu: ImageButton = findViewById(R.id.menuButton)
        val buttonAddWater: ImageButton = findViewById(R.id.addWaterButton)
        val buttonSettingsBottle: ImageButton = findViewById(R.id.settinsBottleButton)
        val buttonExit: ImageButton = findViewById(R.id.exitButton)
        val buttonSevenDaysHistory: ImageButton = findViewById(R.id.sevenDaysHistoryButton)
        val buttonAccount: ImageButton = findViewById(R.id.accountButton)

        checkWaterState()



        val sharedPreferencesForBrigthness = getSharedPreferences("Brigthness", Context.MODE_PRIVATE)
        val Brigthness = sharedPreferencesForBrigthness.getFloat("brigthnessValue", 1f)
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layoutParams = window.attributes
        layoutParams.screenBrightness = Brigthness
        window.attributes = layoutParams


        val sharedPreferencesForCurrentUser = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userdata = sharedPreferencesForCurrentUser.getString("currentData", "")


        buttonMenu.setOnClickListener {
            click.start()
            click.seekTo(0)
            if (menuAnimationIsEnd){
                menuIsActive = if (menuIsActive){
                    hideItems()
                    false
                }
                else{
                    showItems()
                    true
                }
                menuAnimationIsEnd = false
            }
        }
        buttonExit.setOnClickListener {
            click.start()
            click.seekTo(0)
            stopService(serviceIntent)
            finishAffinity()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
        buttonAccount.setOnClickListener {
            click.start()
            click.seekTo(0)
            goToNewActivity = true
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
        buttonAddWater.setOnClickListener{
            click.start()
            click.seekTo(0)

            val sharedPreferencesForBottleSize = getSharedPreferences("BottleSizePref", Context.MODE_PRIVATE)

            val firstBottleSize = sharedPreferencesForBottleSize.getInt("firstBottle", 0)
            val secondBottleSize = sharedPreferencesForBottleSize.getInt("secondBottle", 0)
            val thirdBottleSize = sharedPreferencesForBottleSize.getInt("thirdBottle", 0)
            val fourthBottleSize = sharedPreferencesForBottleSize.getInt("fourthBottle", 0)
            val fifthBottleSize = sharedPreferencesForBottleSize.getInt("fifthBottle", 0)
            val sixthBottleSize = sharedPreferencesForBottleSize.getInt("sixthBottle", 0)
            val seventhBottleSize = sharedPreferencesForBottleSize.getInt("seventhBottle", 0)

            val currentActiveBottle = sharedPreferencesForBottleSize.getInt("currentActive", 0)

            val countOfWaterToAdd = when (currentActiveBottle) {
                1 -> firstBottleSize
                2 -> secondBottleSize
                3 -> thirdBottleSize
                4 -> fourthBottleSize
                5 -> fifthBottleSize
                6 -> sixthBottleSize
                7 -> seventhBottleSize
                else -> -1
            }

            if (countOfWaterToAdd > 0){
                val textCurrentWaterCurrent: TextView = findViewById(R.id.curentWaterText)
                val res = (textCurrentWaterCurrent.text.toString().toInt() + countOfWaterToAdd)
//                val sharedPreferences = getSharedPreferences("Intake", Context.MODE_PRIVATE)


                val calendar = Calendar.getInstance()
                val month = calendar.get(Calendar.MONTH)
                val year = calendar.get(Calendar.YEAR)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val newSetForDey = mutableSetOf<String>()
                for (i in setForDey.indices){
                    val keyValue = setForDey.elementAt(i).split(":")
                    if(keyValue[0] == "$day/$month/$year"){
                        newSetForDey.add( keyValue[0] + ":" + res + ":" + keyValue[2] )
                    }
                    else newSetForDey.add(setForDey.elementAt(i))
                }
                setForDey.clear()
                setForDey.addAll(newSetForDey)

                val sharedPreferencesForCurrentUser2 = getSharedPreferences("UserData", Context.MODE_PRIVATE)
                val userdata2 = sharedPreferencesForCurrentUser2.getString("currentData", "")

                val sharedPreferences = getSharedPreferences("Calendar$userdata2", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putStringSet("dateList", setForDey)
                editor.apply()
                checkWaterState()
            }
            else Toast.makeText(this, "Peas select glass in settings", Toast.LENGTH_SHORT).show()
        }
        buttonSettings.setOnClickListener {
            click.start()
            click.seekTo(0)
            goToNewActivity = true
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
        buttonHistory.setOnClickListener {
            click.start()
            click.seekTo(0)
            goToNewActivity = true
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
        buttonSettingsBottle.setOnClickListener {
            click.start()
            click.seekTo(0)
            goToNewActivity = true
            val intent = Intent(this, SettingsBottleActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
        buttonSevenDaysHistory.setOnClickListener {
            click.start()
            click.seekTo(0)
            goToNewActivity = true
            val intent = Intent(this, HistorySevenDaysActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        checkWaterState()
        hideUi()
    }

    private lateinit var setForDey: MutableSet<String>
    fun checkWaterState(){
        val textCurrentWaterLimit: TextView = findViewById(R.id.textView3)
        val textCurrentWaterCurrent: TextView = findViewById(R.id.curentWaterText)
        val imageCurrentProgres: ImageView = findViewById(R.id.currentProgresImage)


        val sharedPreferencesForCurrentUser = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userdata = sharedPreferencesForCurrentUser.getString("currentData", "")



        val sharedPreferencesForBottleSize = getSharedPreferences("Calendar$userdata", Context.MODE_PRIVATE)
        val getSharedPreferencesForBottleSize = sharedPreferencesForBottleSize.getStringSet("dateList", null)

        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        var currentWaterCount = 0
        var keyIsFinded = false

        if (getSharedPreferencesForBottleSize != null) {
            setForDey = mutableSetOf()
            setForDey.addAll(getSharedPreferencesForBottleSize)
            keyIsFinded = false
            setForDey.forEach(){
                val keyValue = it.split(":")
                if(keyValue[0] == "$day/$month/$year"){
                    Log.d("TAG", "checkWaterState thi is at if: " + keyValue[0] + "  " + "$day/$month/$year" + "   " + keyValue[1])
                    keyIsFinded = true
                    currentWaterCount = keyValue[1].toInt()
                }
            }
            if(!keyIsFinded){
                setForDey.add("$day/$month/$year:0:0")
            }
        }
        else {
            setForDey = mutableSetOf("$day/$month/$year:0:0")
        }


        val editor = sharedPreferencesForBottleSize.edit()
        editor.putStringSet("dateList", setForDey)
        editor.apply()




        val sharedPreferences = getSharedPreferences("Intake$userdata", Context.MODE_PRIVATE)
        val waterMax: Float = sharedPreferences.getInt("maxIntake", 0).toString().toFloat()
        textCurrentWaterLimit.text = waterMax.toInt().toString()
//        var waterCurrent: Float = sharedPreferences.getInt("currentIntake", 0).toString().toFloat()
        var waterCurrent: Float = currentWaterCount.toFloat()
        textCurrentWaterCurrent.text = waterCurrent.toInt().toString()
        if (waterCurrent <= 0) waterCurrent = 1f
        var  waterMeterProcent = (waterCurrent / (waterMax / 100)).toInt()
        if (waterMeterProcent > 100) waterMeterProcent = 100
        val procent = waterMeterProcent / 10
        imageCurrentProgres.setImageResource(waterMeterArray[procent])


        val newSetForDey = mutableSetOf<String>()
        for (i in setForDey.indices){
            val keyValue = setForDey.elementAt(i).split(":")
            if(keyValue[0] == "$day/$month/$year"){
                newSetForDey.add( keyValue[0] + ":" + keyValue[1] + ":" + procent )
            }
            else newSetForDey.add(setForDey.elementAt(i))
        }
        setForDey.clear()
        setForDey.addAll(newSetForDey)

        setForDey.forEach {
            Log.d("TAG", "checkWaterState setForDey at the end off check: $it")
        }
    }

    private fun showItems(){
        val buttonSettingsBottle: ImageButton = findViewById(R.id.settinsBottleButton)
        val buttonSevenDaysHistory: ImageButton = findViewById(R.id.sevenDaysHistoryButton)
        val buttonHistory: ImageButton = findViewById(R.id.historyButton)
        val buttonSettings: ImageButton = findViewById(R.id.settingsButton)
        val buttonMenu: ImageButton = findViewById(R.id.menuButton)
        val buttonAccount: ImageButton = findViewById(R.id.accountButton)

        val distance = buttonHistory.height + (buttonHistory.height / 5)

        val menuAnim = RotateAnimation(0f,270f, RotateAnimation.RELATIVE_TO_SELF,
            0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        menuAnim.duration = 500
        menuAnim.fillAfter = true
        menuAnim.interpolator = DecelerateInterpolator()

        val historyAnim = ValueAnimator.ofFloat(buttonHistory.y, buttonHistory.y - distance * 3)
        historyAnim.duration = 500
        historyAnim.addUpdateListener {animation ->
            val value = animation.animatedValue as Float
            buttonHistory.y = value
        }

        val accountAnim = ValueAnimator.ofFloat(buttonAccount.y, buttonAccount.y - distance * 2)
        accountAnim.duration = 500
        accountAnim.addUpdateListener {animation ->
            val value = animation.animatedValue as Float
            buttonAccount.y = value
        }

        val settingsBottleAnim = ValueAnimator.ofFloat(buttonSettingsBottle.y, buttonSettingsBottle.y - distance * 5)
        settingsBottleAnim.duration = 500
        settingsBottleAnim.addUpdateListener {animation ->
            val value = animation.animatedValue as Float
            buttonSettingsBottle.y = value
        }

        val sevenDaysHistoryAnim = ValueAnimator.ofFloat(buttonSevenDaysHistory.y, buttonSevenDaysHistory.y - distance * 4)
        sevenDaysHistoryAnim.duration = 500
        sevenDaysHistoryAnim.addUpdateListener {animation ->
            val value = animation.animatedValue as Float
            buttonSevenDaysHistory.y = value
        }
        val settingsAnim = ValueAnimator.ofFloat(buttonSettings.y, buttonSettings.y - distance)
        settingsAnim.duration = 500
        settingsAnim.addUpdateListener {animation ->
            val value = animation.animatedValue as Float
            buttonSettings.y = value
        }
        settingsAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                menuAnimationIsEnd = true
            }
        })

        buttonMenu.startAnimation(menuAnim)

        historyAnim.start()
        settingsAnim.start()
        settingsBottleAnim.start()
        sevenDaysHistoryAnim.start()
        accountAnim.start()
    }

    private fun hideItems(){
        val buttonSettingsBottle: ImageButton = findViewById(R.id.settinsBottleButton)
        val buttonSevenDaysHistory: ImageButton = findViewById(R.id.sevenDaysHistoryButton)
        val buttonHistory: ImageButton = findViewById(R.id.historyButton)
        val buttonSettings: ImageButton = findViewById(R.id.settingsButton)
        val buttonMenu: ImageButton = findViewById(R.id.menuButton)
        val buttonAccount: ImageButton = findViewById(R.id.accountButton)

        val distance = buttonHistory.height + (buttonHistory.height / 5)

        val menuAnim = RotateAnimation(270f,0f, RotateAnimation.RELATIVE_TO_SELF,
            0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        menuAnim.duration = 500
        menuAnim.fillAfter = true
        menuAnim.interpolator = DecelerateInterpolator()


        val settingsBottleAnim = ValueAnimator.ofFloat(buttonSettingsBottle.y, buttonSettingsBottle.y + distance * 5)
        settingsBottleAnim.duration = 500
        settingsBottleAnim.addUpdateListener {animation ->
            val value = animation.animatedValue as Float
            buttonSettingsBottle.y = value
        }

        val sevenDaysHistoryAnim = ValueAnimator.ofFloat(buttonSevenDaysHistory.y, buttonSevenDaysHistory.y + distance * 4)
        sevenDaysHistoryAnim.duration = 500
        sevenDaysHistoryAnim.addUpdateListener {animation ->
            val value = animation.animatedValue as Float
            buttonSevenDaysHistory.y = value
        }

        val historyAnim = ValueAnimator.ofFloat(buttonHistory.y, buttonHistory.y + distance * 3)
        historyAnim.duration = 500
        historyAnim.addUpdateListener {animation ->
            val value = animation.animatedValue as Float
            buttonHistory.y = value
        }

        val accountAnim = ValueAnimator.ofFloat(buttonAccount.y, buttonAccount.y + distance * 2)
        accountAnim.duration = 500
        accountAnim.addUpdateListener {animation ->
            val value = animation.animatedValue as Float
            buttonAccount.y = value
        }

        val settingsAnim = ValueAnimator.ofFloat(buttonSettings.y, buttonSettings.y + distance)
        settingsAnim.duration = 500
        settingsAnim.addUpdateListener {animation ->
            val value = animation.animatedValue as Float
            buttonSettings.y = value
        }
        settingsAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                menuAnimationIsEnd = true
            }
        })

        buttonMenu.startAnimation(menuAnim)
        historyAnim.start()
        settingsAnim.start()
        settingsBottleAnim.start()
        sevenDaysHistoryAnim.start()
        accountAnim.start()

    }

    override fun onDestroy() {


        val sharedPreferencesForCurrentUser = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userdata = sharedPreferencesForCurrentUser.getString("currentData", "")

        val sharedPreferences = getSharedPreferences("Calendar$userdata", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putStringSet("dateList", setForDey)
        editor.apply()
        super.onDestroy()
    }
    override fun onPause() {
        super.onPause()
        hideUi()
        val sharedPreferencesForCurrentUser = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userdata = sharedPreferencesForCurrentUser.getString("currentData", "")

        val sharedPreferences = getSharedPreferences("Calendar$userdata", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putStringSet("dateList", setForDey)
        editor.apply()

        setForDey.forEach {
            Log.d("TAG", "checkWaterState at pause: $it")
        }

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
