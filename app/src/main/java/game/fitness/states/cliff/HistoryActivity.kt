package game.fitness.states.cliff

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.icu.util.Calendar
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible

class HistoryActivity : AppCompatActivity() {
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


    private lateinit var listDateButtons :List<ImageButton>
    private lateinit var listDateNumbers :List<TextView>
    private lateinit var setForDey: MutableSet<String>
    private val monthsList = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        val click = MediaPlayer.create(this, R.raw.button_sound)
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

        val buttonBack: ImageButton = findViewById(R.id.backButton)
        val buttonNext: ImageButton = findViewById(R.id.nextMounthButton)
        val buttonPrevious: ImageButton = findViewById(R.id.previousMonthButton)

        val textMonths: TextView = findViewById(R.id.monthsText)

        val imageProgres: ImageView = findViewById(R.id.progresImage)

        initializeCalendarElements()
        getDataForCalendar()

        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        textMonths.text = monthsList[month] + " $year"
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        listDateButtons[day - 1].setImageResource(R.drawable.ic_calendar_bloc_active)
        listDateNumbers[day - 1].setTextColor(-0x1000000)
        setForDey.forEach(){
            val keyValue = it.split(":")
            Log.d("TAG", "onCreate: keys value: " + keyValue[0] + " " + keyValue[1] + " " + keyValue[2])
            if(keyValue[0] == "$day/$month/$year"){
                Log.d("TAG", "onCreate: If is worked progress: " + keyValue[2])
                imageProgres.setImageResource(waterMeterArray[keyValue[2].toInt()])
            }
        }
        buttonNext.setOnClickListener {
            click.start()
            click.seekTo(0)
            listDateButtons.forEach { burr ->
                burr.setImageResource(R.drawable.ic_calendar_bloc_inactive)
            }
            listDateNumbers.forEach { text ->
                text.setTextColor(Color.parseColor("#63AEEF"))
            }
            imageProgres.setImageResource(waterMeterArray[0])
            val bc = textMonths.text.toString().split(" ")
            var newYear = bc[1].toInt()
            var newMonthIndex = (monthsList.indexOf(bc[0]) + 1)
            if (newMonthIndex >= 12){
                newYear += 1
                newMonthIndex = 0
            }
            val newMounth = monthsList[newMonthIndex]
            textMonths.text = "$newMounth $newYear"
            if (newMonthIndex == 0 || newMonthIndex == 2 || newMonthIndex == 4 ||
                newMonthIndex == 6 || newMonthIndex == 7 || newMonthIndex == 9 || newMonthIndex == 11){
                listDateButtons[30].isVisible = true
                listDateButtons[29].isVisible = true
                listDateButtons[28].isVisible = true
            }
            else if (newMonthIndex == 1 && (newYear % 4) != 0){
                listDateButtons[30].isVisible = false
                listDateButtons[29].isVisible = false
                listDateButtons[28].isVisible = false
            }
            else if (newMonthIndex == 1 && (newYear % 4) == 0){
                listDateButtons[30].isVisible = false
                listDateButtons[29].isVisible = false
                listDateButtons[28].isVisible = true
            }
            else{
                listDateButtons[30].isVisible = false
                listDateButtons[29].isVisible = true
                listDateButtons[28].isVisible = true
            }
        }
        buttonPrevious.setOnClickListener {
            click.start()
            click.seekTo(0)
            listDateButtons.forEach { burr ->
                burr.setImageResource(R.drawable.ic_calendar_bloc_inactive)
            }
            listDateNumbers.forEach { text ->
                text.setTextColor(Color.parseColor("#63AEEF"))
            }
            imageProgres.setImageResource(waterMeterArray[0])
            val bc = textMonths.text.toString().split(" ")
            var newYear = bc[1].toInt()
            var newMonthIndex = (monthsList.indexOf(bc[0]) - 1)
            if (newMonthIndex <= -1){
                newYear -= 1
                newMonthIndex = 11
            }
            val newMounth = monthsList[newMonthIndex]
            textMonths.text = "$newMounth $newYear"
            if (newMonthIndex == 0 || newMonthIndex == 2 || newMonthIndex == 4 ||
                newMonthIndex == 6 || newMonthIndex == 7 || newMonthIndex == 9 || newMonthIndex == 11){
                listDateButtons[30].isVisible = true
                listDateButtons[29].isVisible = true
                listDateButtons[28].isVisible = true
            }
            else if (newMonthIndex == 1 && (newYear % 4) != 0){
                listDateButtons[30].isVisible = false
                listDateButtons[29].isVisible = false
                listDateButtons[28].isVisible = false
            }
            else if (newMonthIndex == 1 && (newYear % 4) == 0){
                listDateButtons[30].isVisible = false
                listDateButtons[29].isVisible = false
                listDateButtons[28].isVisible = true
            }
            else{
                listDateButtons[30].isVisible = false
                listDateButtons[29].isVisible = true
                listDateButtons[28].isVisible = true
            }
        }

        listDateButtons.forEach { button ->
            button.setOnClickListener {
                click.start()
                click.seekTo(0)
                listDateButtons.forEach { burr ->
                    burr.setImageResource(R.drawable.ic_calendar_bloc_inactive)
                }
                listDateNumbers.forEach { text ->
                    text.setTextColor(Color.parseColor("#63AEEF"))
                }
                button.setImageResource(R.drawable.ic_calendar_bloc_active)
                listDateNumbers[button.contentDescription.toString().toInt()  -1].setTextColor(-0x1000000)

                imageProgres.setImageResource(waterMeterArray[0])
                setForDey.forEach {
                    val keyValue = it.split(":")
                    val bc = textMonths.text.toString().split(" ")
                    val day1 = button.contentDescription.toString()
                    val month1 = monthsList.indexOf(bc[0])
                    val year1 = bc[1]
                    Log.d("TAG", "onCreate: button $day1/$month1/$year1")
                    Log.d("TAG", "onCreate: in list " + keyValue[0])
                    if(keyValue[0] == "$day1/$month1/$year1"){
                        Log.d("TAG", "onCreate: if WORK    " + keyValue[2].toInt())
                        imageProgres.setImageResource(waterMeterArray[keyValue[2].toInt()])

                    }
                }

            }

        }
        buttonBack.setOnClickListener {
            click.start()
            click.seekTo(0)
            goToNewActivity = true
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }


        hideUi()
    }
    private fun getDataForCalendar(){

        val sharedPreferencesForCurrentUser = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userdata = sharedPreferencesForCurrentUser.getString("currentData", "")

        val sharedPreferencesForBottleSize = getSharedPreferences("Calendar$userdata", Context.MODE_PRIVATE)
        val getSharedPreferencesForBottleSize = sharedPreferencesForBottleSize.getStringSet("dateList", null)

        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        setForDey = mutableSetOf()
        if (getSharedPreferencesForBottleSize != null) {
            setForDey.addAll(getSharedPreferencesForBottleSize)
        }
    }
    private fun setDataForCalendar(){

        val sharedPreferencesForCurrentUser = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userdata = sharedPreferencesForCurrentUser.getString("currentData", "")

        val sharedPreferencesForBottleSize = getSharedPreferences("Calendar$userdata", Context.MODE_PRIVATE)
        val editor = sharedPreferencesForBottleSize.edit()
        editor.putStringSet("dateList", setForDey)
        editor.apply()
    }
    private fun initializeCalendarElements(){
        listDateButtons = listOf<ImageButton>(
            findViewById(R.id.day1Button),
            findViewById(R.id.day2Button),
            findViewById(R.id.day3Button),
            findViewById(R.id.day4Button),
            findViewById(R.id.day5Button),
            findViewById(R.id.day6Button),
            findViewById(R.id.day7Button),
            findViewById(R.id.day8Button),
            findViewById(R.id.day9Button),
            findViewById(R.id.day10Button),
            findViewById(R.id.day11Button),
            findViewById(R.id.day12Button),
            findViewById(R.id.day13Button),
            findViewById(R.id.day14Button),
            findViewById(R.id.day15Button),
            findViewById(R.id.day16Button),
            findViewById(R.id.day17Button),
            findViewById(R.id.day18Button),
            findViewById(R.id.day19Button),
            findViewById(R.id.day20Button),
            findViewById(R.id.day21Button),
            findViewById(R.id.day22Button),
            findViewById(R.id.day23Button),
            findViewById(R.id.day24Button),
            findViewById(R.id.day25Button),
            findViewById(R.id.day26Button),
            findViewById(R.id.day27Button),
            findViewById(R.id.day28Button),
            findViewById(R.id.day29Button),
            findViewById(R.id.day30Button),
            findViewById(R.id.day31Button)
        )
        listDateNumbers = listOf<TextView>(
            findViewById(R.id.day1Text),
            findViewById(R.id.day2Text),
            findViewById(R.id.day3Text),
            findViewById(R.id.day4Text),
            findViewById(R.id.day5Text),
            findViewById(R.id.day6Text),
            findViewById(R.id.day7Text),
            findViewById(R.id.day8Text),
            findViewById(R.id.day9Text),
            findViewById(R.id.day10Text),
            findViewById(R.id.day11Text),
            findViewById(R.id.day12Text),
            findViewById(R.id.day13Text),
            findViewById(R.id.day14Text),
            findViewById(R.id.day15Text),
            findViewById(R.id.day16Text),
            findViewById(R.id.day17Text),
            findViewById(R.id.day18Text),
            findViewById(R.id.day19Text),
            findViewById(R.id.day20Text),
            findViewById(R.id.day21Text),
            findViewById(R.id.day22Text),
            findViewById(R.id.day23Text),
            findViewById(R.id.day24Text),
            findViewById(R.id.day25Text),
            findViewById(R.id.day26Text),
            findViewById(R.id.day27Text),
            findViewById(R.id.day28Text),
            findViewById(R.id.day29Text),
            findViewById(R.id.day30Text),
            findViewById(R.id.day31Text)
        )
    }
    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Місяці починаються з 0, тому додаємо 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return "$year-$month-$day"
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