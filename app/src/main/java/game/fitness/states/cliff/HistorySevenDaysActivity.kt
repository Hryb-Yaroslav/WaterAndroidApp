package game.fitness.states.cliff

import android.annotation.SuppressLint
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
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible

class HistorySevenDaysActivity : AppCompatActivity() {
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

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_seven_days)
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

        val imageProgres: ImageView = findViewById(R.id.progresImage)


        val start: ImageView = findViewById(R.id.startView)
        val end: ImageView = findViewById(R.id.endView)

        val iv0X0: ImageButton = findViewById(R.id.imageView0X0)
        val iv0X1: ImageButton = findViewById(R.id.imageView0X1)
        val iv0X2: ImageButton = findViewById(R.id.imageView0X2)
        val iv0X3: ImageButton = findViewById(R.id.imageView0X3)
        val iv0X4: ImageButton = findViewById(R.id.imageView0X4)
        val iv0X5: ImageButton = findViewById(R.id.imageView0X5)
        val iv0X6: ImageButton = findViewById(R.id.imageView0X6)
//        val lineViewFirstColum = findViewById<LineView>(R.id.lineViewForFirstColum)
//        val lineViewSecondColum = findViewById<LineView>(R.id.lineViewForSecondColum)
        listDateButtons = listOf<ImageButton>(
        iv0X0,
        iv0X1,
        iv0X2,
        iv0X3,
        iv0X4,
        iv0X5,
        iv0X6
        )
        val mar = 20

        getDataForCalendar()

        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
//        textMonths.text = monthsList[month] + " $year"

        var firstDay = 0
        var secondDay = 0
        var thirdDay = 0
        var fourthDay = 0
        var fifthDay = 0
        var sixthDay = 0
        var seventhDay = 0


        setForDey.forEach {
            val keyValue = it.split(":")
            if (keyValue[0] ==  "$day/$month/$year"){
                seventhDay = keyValue[1].toInt()
            }



            if (day - 1 > 0){
                if (keyValue[0] ==  "${day-1}/$month/$year"){
                    sixthDay = keyValue[1].toInt()
                }
            }
            else if (day - 1 <= 0 && month > 1){
                val newMonthIndex = month - 1
                Log.d("TAG", "onCreate: ELSE $newMonthIndex\t$month")
                if (newMonthIndex == 0 || newMonthIndex == 2 || newMonthIndex == 4 ||
                    newMonthIndex == 6 || newMonthIndex == 7 || newMonthIndex == 9 || newMonthIndex == 11){
                    if (keyValue[0] ==  "31/${month-1}/$year"){
                        sixthDay = keyValue[1].toInt()
                    }
                }
                else if (newMonthIndex == 1 && (year % 4) != 0){
                    if (keyValue[0] ==  "28/${month-1}/$year"){
                        sixthDay = keyValue[1].toInt()
                    }
                }
                else if (newMonthIndex == 1 && (year % 4) == 0){
                    if (keyValue[0] ==  "29/${month-1}/$year"){
                        sixthDay = keyValue[1].toInt()
                    }
                }
                else{
                    if (keyValue[0] ==  "30/${month-1}/$year"){
                        sixthDay = keyValue[1].toInt()
                    }
                }
            }


            if (day - 2 > 0){
                if (keyValue[0] ==  "${day-2}/$month/$year"){
                    fifthDay = keyValue[1].toInt()
                }
            }
            else if (day - 2 <= 0 && month > 1){
                val newMonthIndex = month - 1
                if (newMonthIndex == 0 || newMonthIndex == 2 || newMonthIndex == 4 ||
                    newMonthIndex == 6 || newMonthIndex == 7 || newMonthIndex == 9 || newMonthIndex == 11){
                    if (keyValue[0] ==  "30/${month-1}/$year"){
                        fifthDay = keyValue[1].toInt()
                    }
                }
                else if (newMonthIndex == 1 && (year % 4) != 0){
                    if (keyValue[0] ==  "27/${month-1}/$year"){
                        fifthDay = keyValue[1].toInt()
                    }
                }
                else if (newMonthIndex == 1 && (year % 4) == 0){
                    if (keyValue[0] ==  "28/${month-1}/$year"){
                        fifthDay = keyValue[1].toInt()
                    }
                }
                else{
                    if (keyValue[0] ==  "29/${month-1}/$year"){
                        fifthDay = keyValue[1].toInt()
                    }
                }
            }


            if (day - 3 > 0){
                if (keyValue[0] ==  "${day-3}/$month/$year"){
                    fourthDay = keyValue[1].toInt()
                }
            }
            else if (day - 3 <= 0 && month > 1){
                val newMonthIndex = month - 1
                if (newMonthIndex == 0 || newMonthIndex == 2 || newMonthIndex == 4 ||
                    newMonthIndex == 6 || newMonthIndex == 7 || newMonthIndex == 9 || newMonthIndex == 11){
                    if (keyValue[0] ==  "29/${month-1}/$year"){
                        fourthDay = keyValue[1].toInt()
                    }
                }
                else if (newMonthIndex == 1 && (year % 4) != 0){
                    if (keyValue[0] ==  "26/${month-1}/$year"){
                        fourthDay = keyValue[1].toInt()
                    }
                }
                else if (newMonthIndex == 1 && (year % 4) == 0){
                    if (keyValue[0] ==  "27/${month-1}/$year"){
                        fourthDay = keyValue[1].toInt()
                    }
                }
                else{
                    if (keyValue[0] ==  "28/${month-1}/$year"){
                        fourthDay = keyValue[1].toInt()
                    }
                }
            }

            if (day - 4 > 0){
                if (keyValue[0] ==  "${day-4}/$month/$year"){
                    thirdDay = keyValue[1].toInt()
                }
            }
            else if (day - 4 <= 0 && month > 1){
                val newMonthIndex = month - 1
                if (newMonthIndex == 0 || newMonthIndex == 2 || newMonthIndex == 4 ||
                    newMonthIndex == 6 || newMonthIndex == 7 || newMonthIndex == 9 || newMonthIndex == 11){
                    if (keyValue[0] ==  "28/${month-1}/$year"){
                        thirdDay = keyValue[1].toInt()
                    }
                }
                else if (newMonthIndex == 1 && (year % 4) != 0){
                    if (keyValue[0] ==  "25/${month-1}/$year"){
                        thirdDay = keyValue[1].toInt()
                    }
                }
                else if (newMonthIndex == 1 && (year % 4) == 0){
                    if (keyValue[0] ==  "26/${month-1}/$year"){
                        thirdDay = keyValue[1].toInt()
                    }
                }
                else{
                    if (keyValue[0] ==  "27/${month-1}/$year"){
                        thirdDay = keyValue[1].toInt()
                    }
                }
            }

            if (day - 5 > 0){
                if (keyValue[0] ==  "${day-5}/$month/$year"){
                    secondDay = keyValue[1].toInt()
                }
            }
            else if (day - 5 <= 0 && month > 1){
                val newMonthIndex = month - 1
                if (newMonthIndex == 0 || newMonthIndex == 2 || newMonthIndex == 4 ||
                    newMonthIndex == 6 || newMonthIndex == 7 || newMonthIndex == 9 || newMonthIndex == 11){
                    if (keyValue[0] ==  "27/${month-1}/$year"){
                        secondDay = keyValue[1].toInt()
                    }
                }
                else if (newMonthIndex == 1 && (year % 4) != 0){
                    if (keyValue[0] ==  "24/${month-1}/$year"){
                        secondDay = keyValue[1].toInt()
                    }
                }
                else if (newMonthIndex == 1 && (year % 4) == 0){
                    if (keyValue[0] ==  "25/${month-1}/$year"){
                        secondDay = keyValue[1].toInt()
                    }
                }
                else{
                    if (keyValue[0] ==  "26/${month-1}/$year"){
                        secondDay = keyValue[1].toInt()
                    }
                }
            }

            if (day - 6 > 0){
                if (keyValue[0] ==  "${day-6}/$month/$year"){
                    firstDay = keyValue[1].toInt()
                }
            }
            else if (day - 6 <= 0 && month > 1){
                val newMonthIndex = month - 1
                if (newMonthIndex == 0 || newMonthIndex == 2 || newMonthIndex == 4 ||
                    newMonthIndex == 6 || newMonthIndex == 7 || newMonthIndex == 9 || newMonthIndex == 11){
                    if (keyValue[0] ==  "26/${month-1}/$year"){
                        firstDay = keyValue[1].toInt()
                    }
                }
                else if (newMonthIndex == 1 && (year % 4) != 0){
                    if (keyValue[0] ==  "23/${month-1}/$year"){
                        firstDay = keyValue[1].toInt()
                    }
                }
                else if (newMonthIndex == 1 && (year % 4) == 0){
                    if (keyValue[0] ==  "24/${month-1}/$year"){
                        firstDay = keyValue[1].toInt()
                    }
                }
                else{
                    if (keyValue[0] ==  "25/${month-1}/$year"){
                        firstDay = keyValue[1].toInt()
                    }
                }
            }
        }

        end.post{
            //lineViewFirstColum.setPoints(end.x, end.y, end.x, end.y - iv0X0.height )

            //val startFor2 = end.x + lineViewFirstColum.contentDescription.toString().toInt() + mar
            //lineViewSecondColum.setPoints(startFor2, end.y, startFor2 + 1, end.y - iv0X0.height )
        }

        end.post {
            val numbers = intArrayOf(firstDay, secondDay, thirdDay, fourthDay, fifthDay, sixthDay, seventhDay)
            val maxNumber = numbers.max()

            val layoutParams1 = iv0X0.layoutParams
            val lp1Procent = (maxNumber.toDouble() - firstDay.toDouble())/ maxNumber.toDouble() *  100
            layoutParams1.height = (iv0X0.height - (iv0X0.height / 100) * lp1Procent).toInt()
            iv0X0.layoutParams = layoutParams1




            val layoutParams2 = iv0X1.layoutParams
            val lp2Procent = (maxNumber.toDouble() - secondDay.toDouble())/ maxNumber.toDouble() *  100
            layoutParams2.height = (iv0X1.height - (iv0X1.height / 100) * lp2Procent).toInt()
            iv0X1.layoutParams = layoutParams2

            val layoutParams3 = iv0X2.layoutParams
            val lp3Procent = (maxNumber.toDouble() - thirdDay.toDouble())/ maxNumber.toDouble() *  100
            layoutParams3.height = (iv0X2.height - (iv0X2.height / 100) * lp3Procent).toInt()
            iv0X2.layoutParams = layoutParams3


            val layoutParams4 = iv0X3.layoutParams
            val lp4Procent = (maxNumber.toDouble() - fourthDay.toDouble())/ maxNumber.toDouble() *  100
            layoutParams4.height = (iv0X3.height - (iv0X3.height / 100) * lp4Procent).toInt()
            iv0X3.layoutParams = layoutParams4


            val layoutParams5 = iv0X4.layoutParams
            val lp5Procent = (maxNumber.toDouble() - fifthDay.toDouble())/ maxNumber.toDouble() *  100
            layoutParams5.height = (iv0X4.height - (iv0X4.height / 100) * lp5Procent).toInt()
            iv0X4.layoutParams = layoutParams5

            val layoutParams6 = iv0X5.layoutParams
            val lp6Procent = (maxNumber.toDouble() - sixthDay.toDouble())/ maxNumber.toDouble() *  100
            layoutParams6.height = (iv0X5.height  - (iv0X5.height / 100) * lp6Procent).toInt()
            iv0X5.layoutParams = layoutParams6

            val layoutParams7 = iv0X6.layoutParams
            val lp7Procent = (maxNumber.toDouble() - seventhDay.toDouble())/ maxNumber.toDouble() *  100
            layoutParams7.height = (iv0X6.height - (iv0X6.height.toDouble() / 100.toDouble()) * lp7Procent).toInt()
            iv0X6.layoutParams = layoutParams7




        }


        //lineViewSecondWord.setStartPoint(100f, 100f)
        //lineViewSecondWord.returnToStarPosition = true
        //lineViewSecondWord.isActive = false

        val textData: TextView = findViewById(R.id.dataTextView)
        val textValue: TextView = findViewById(R.id.valueTextView)



        listDateButtons.forEach { button ->
            button.setOnClickListener {
                click.start()
                click.seekTo(0)
                listDateButtons.forEach { burr ->
                    burr.setImageResource(R.drawable.glass_size_area)
                }
                button.setImageResource(R.drawable.ic_scele)
                //listDateNumbers[button.contentDescription.toString().toInt()  -1].setTextColor(-0x1000000)

                imageProgres.setImageResource(waterMeterArray[0])
                setForDey.forEach {
                    val keyValue = it.split(":")

                    if (keyValue[0] ==  "${day - button.contentDescription.toString().toInt()}/$month/$year"){
                        imageProgres.setImageResource(waterMeterArray[keyValue[2].toInt()])
                        textData.text = keyValue[0]
                        textValue.text = keyValue[1]
                    }

                    if (day - button.contentDescription.toString().toInt() > 0){
                        if (keyValue[0] ==  "${day-button.contentDescription.toString().toInt()}/$month/$year"){
                            imageProgres.setImageResource(waterMeterArray[keyValue[2].toInt()])
                            textData.text = keyValue[0]
                            textValue.text = keyValue[1]
                        }
                    }
                    else if (day - button.contentDescription.toString().toInt() <= 0 && month > 1){
                        val newMonthIndex = month - 1
                        if (newMonthIndex == 0 || newMonthIndex == 2 || newMonthIndex == 4 ||
                            newMonthIndex == 6 || newMonthIndex == 7 || newMonthIndex == 9 || newMonthIndex == 11){
                            if (keyValue[0] ==  "${31 - button.contentDescription.toString().toInt() + 1 }/${month-1}/$year"){
                                imageProgres.setImageResource(waterMeterArray[keyValue[2].toInt()])
                                textData.text = keyValue[0]
                                textValue.text = keyValue[1]
                            }
                        }
                        else if (newMonthIndex == 1 && (year % 4) != 0){
                            if (keyValue[0] ==  "${28 - button.contentDescription.toString().toInt() + 1 }/${month-1}/$year"){
                                imageProgres.setImageResource(waterMeterArray[keyValue[2].toInt()])
                                textData.text = keyValue[0]
                                textValue.text = keyValue[1]
                            }
                        }
                        else if (newMonthIndex == 1 && (year % 4) == 0){
                            if (keyValue[0] ==  "${29 - button.contentDescription.toString().toInt() + 1 }/${month-1}/$year"){
                                imageProgres.setImageResource(waterMeterArray[keyValue[2].toInt()])
                                textData.text = keyValue[0]
                                textValue.text = keyValue[1]
                            }
                        }
                        else{
                            if (keyValue[0] ==  "${30 - button.contentDescription.toString().toInt() + 1 }/${month-1}/$year"){
                                imageProgres.setImageResource(waterMeterArray[keyValue[2].toInt()])
                                textData.text = keyValue[0]
                                textValue.text = keyValue[1]
                            }
                        }
                    }


//                    val bc = textMonths.text.toString().split(" ")
//                    val day1 = button.contentDescription.toString()
//                    val month1 = monthsList.indexOf(bc[0])
//                    val year1 = bc[1]
//                    Log.d("TAG", "onCreate: button $day1/$month1/$year1")
//                    Log.d("TAG", "onCreate: in list " + keyValue[0])
//                    if(keyValue[0] == "$day1/$month1/$year1"){
//                        Log.d("TAG", "onCreate: if WORK    " + keyValue[2].toInt())
//                        imageProgres.setImageResource(waterMeterArray[keyValue[2].toInt()])
                //}
                }

                val dataValue = textData.text.toString().split("/")
                textData.text = dataValue[0] + " " + (dataValue[1].toInt() + 1) + " " + dataValue[2]
                textValue.text = textValue.text.toString() + "ml"

            }
        }

        buttonBack.setOnClickListener {
            click.start()
            click.seekTo(0)
            goToNewActivity = true
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        setValueForColum()
        hideUi()
    }

    private fun setValueForColum(){


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
        val sharedPreferencesForBottleSize = getSharedPreferences("Calendar", Context.MODE_PRIVATE)
        val editor = sharedPreferencesForBottleSize.edit()
        editor.putStringSet("dateList", setForDey)
        editor.apply()
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