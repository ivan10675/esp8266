package com.example.esp8266

import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import com.example.esp8266.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var request: Request
    private lateinit var binding: ActivityMainBinding
    private lateinit var pref: SharedPreferences
    private val client = OkHttpClient()
    private var this_mode = 0
    val mode_Array = arrayOf("Шкала громкости (градиент)","Шкала громкости (радуга)","Цветомузыка (5 полос)","Цветомузыка (3 полосы)","Цветомузыка (1 полоса)","Стробоскоп","Цветная подсветка","Бегущие частоты","Анализатор спектра");
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val arrayAdapter = ArrayAdapter(this@MainActivity,android.R.layout.simple_spinner_dropdown_item,mode_Array)
        binding.spinner.adapter = arrayAdapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                post("02"+p2+"00")
                this_mode=p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        binding.seekBarBrightN.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                if (p0 != null) {
                    if(p0.progress>=100)
                    post("10"+p0.progress)
                    else if(p0.progress>=10)post("100"+p0.progress)
                    else post("1000"+p0.progress)
                }
            }
        })
        binding.seekBarBright.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                if (p0 != null) {
                    if(p0.progress>100)
                        post("11"+p0.progress)
                    else if(p0.progress>=10)post("110"+p0.progress)
                    else post("1100"+p0.progress)
                }
            }
        })
        binding.seekBarOK.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onStartTrackingTouch(p0: SeekBar?) {
                if(this_mode in 0..4){
                    binding.seekBarOK.min=5
                    binding.seekBarOK.max=100
                }
                if(this_mode in 5..6){
                    binding.seekBarOK.min=0
                    binding.seekBarOK.max=255
                }
                if(this_mode in 7..8){
                    binding.seekBarOK.min=1
                    binding.seekBarOK.max=266
                }
            }


            override fun onStopTrackingTouch(p0: SeekBar?) {

                if (p0 != null) {
                    if(p0.progress>=100)
                        post("12"+p0.progress)
                    else if(p0.progress>=10)post("120"+p0.progress)
                    else post("1200"+p0.progress)
                }
            }
        })
        binding.seekBarOK2.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onStartTrackingTouch(p0: SeekBar?) {
                if(this_mode in 0..3){
                    binding.seekBarOK2.min=5
                    binding.seekBarOK2.max=20
                }
                if(this_mode ==4){
                    binding.seekBarOK2.min=0
                    binding.seekBarOK2.max=50
                }
                if(this_mode ==5 || this_mode ==8){
                    binding.seekBarOK2.min=0
                    binding.seekBarOK2.max=255

                }
                if(this_mode ==6){
                    binding.seekBarOK2.min=1
                    binding.seekBarOK2.max=999

                }
                if(this_mode ==7){
                    binding.seekBarOK2.min=0
                    binding.seekBarOK2.max=10

                }

            }


            override fun onStopTrackingTouch(p0: SeekBar?) {
                if (p0 != null) {

                    if(p0.progress>=100)
                        post("13"+p0.progress)
                    else if(p0.progress>=10)post("130"+p0.progress)
                    else post("1300"+p0.progress)
                }
            }
        })
        pref = getSharedPreferences("MyPref", MODE_PRIVATE)
        onClickSaveIp()
        getIp()
        binding.apply {
            //bLed1.setOnClickListener(onClickListener())
           // bLed2.setOnClickListener(onClickListener())
           // bLed3.setOnClickListener(onClickListener())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.sync) post("temperature")
        return true
    }

    private fun onClickListener(): View.OnClickListener{
        return View.OnClickListener {
            when(it.id){
              //  R.id.bLed1 -> { post("led1") }
               // R.id.bLed2 -> { post("led2") }
               // R.id.bLed3 -> { post("led3") }
            }
        }
    }

    private fun getIp() = with(binding){
        val ip = pref.getString("ip", "")
        if(ip != null){
            if(ip.isNotEmpty()) edIp.setText(ip)
        }
    }

    private fun onClickSaveIp() = with(binding){
        bSave.setOnClickListener {
            if(edIp.text.isNotEmpty())saveIp(edIp.text.toString())
        }
    }

    private fun saveIp(ip: String){
        val editor = pref.edit()
        editor.putString("ip", ip)
        editor.apply()
    }

    private fun post(post: String){
       Thread{

          request = Request.Builder().url("http://${binding.edIp.text}/$post").build()
          try {
              var response = client.newCall(request).execute()
              if(response.isSuccessful){
                  val resultText = response.body()?.string()
                  runOnUiThread {
                      val temp = resultText + "Cº"
                  }
              }
          } catch (i: IOException){

          }

       }.start()
    }


}