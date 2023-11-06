package com.example.tp2_formulaire

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.telephony.SmsManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.tp2_formulaire.R.id
import java.io.File
import java.lang.Exception
import java.util.Calendar
import java.util.regex.Pattern

class FormActivity : AppCompatActivity() {
    private lateinit var nom:EditText
    private lateinit var  prenom: EditText
    private lateinit var avatar: ImageView
    private lateinit var ddn: EditText
    private lateinit var numTel: EditText
    private lateinit var email: EditText
    private lateinit var addresse: EditText
    private lateinit var codePostal: EditText
    private lateinit var genre: String
    private lateinit var radioGroup: RadioGroup
    private lateinit var rdbM: RadioButton
    private lateinit var rdbF: RadioButton
    private lateinit var btnSubmit: Button
    private lateinit var cameraBtn: ImageButton
    private lateinit var galleryBtn: ImageButton
    private lateinit var activityCapture: ActivityResultLauncher<Intent>
    private lateinit var  activityGallery: ActivityResultLauncher<Intent>
    companion object {
        const val KEY_FORM = "KEY_FORM"
    }
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        avatar = findViewById<ImageView>(id.avatar)
        nom = findViewById<EditText>(id.editTextNom)
        prenom = findViewById<EditText>(id.editTextPrenom)
        ddn = findViewById<EditText>(id.editTextDDN)
        numTel = findViewById<EditText>(id.editTextPhone)
        email = findViewById<EditText>(id.editTextEmailAddress)
        addresse = findViewById<EditText>(id.editTextAdresse)
        codePostal = findViewById<EditText>(id.editTextCodePostal)
        radioGroup = findViewById<RadioGroup>(id.radioGroup)
        rdbM = findViewById(id.rdbM)
        rdbF = findViewById(id.rdbF)
        //By default check the radio button of Male
        rdbM.isChecked = true
        genre = "M"
        activityGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                imageUri = result.data?.data
                avatar.setImageURI(imageUri)
            }
        }
        activityCapture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Glide.with(this).load(imageUri).into(avatar)
               /* val imageBitmap = result.data?.extras?.get("data")
                avatar.setImageBitmap(imageBitmap as Bitmap)*/
            }
        }

        cameraBtn = findViewById(id.cameraBtn)
        cameraBtn.setOnClickListener { capturePhoto() }

        galleryBtn = findViewById(id.galleryBtn)
        galleryBtn.setOnClickListener {
            //check permission at runtime
            val checkSelfPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if ( checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                //Requests permissions to be granted to this application at runtime
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            } else {
                openGallery()
            }
        }
        radioGroup.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { radioGroup, checkedId ->
                val rdb = findViewById<RadioButton>(checkedId)
                when (rdb.text) {
                    "M" -> genre = "M"
                    "F" -> genre = "F"
                }
            }
        )
        btnSubmit = findViewById<Button>(id.btnSubmit)
        btnSubmit.setOnClickListener {
            val scrollView = findViewById<ScrollView>(R.id.scrollView)
            val context = scrollView.context
            var valide = true
            if (nom.text.isNullOrBlank() || prenom.text.isNullOrBlank() || numTel.text.isNullOrBlank()) {
                Toast.makeText(applicationContext, "* Merci de saisir les champs obligatoires.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(applicationContext, "Submitted", Toast.LENGTH_LONG).show()
            }
            if (nom.text.isNullOrBlank()) {
                nom.error = "Saisie obligatoire"
                valide = false
            }
            if (prenom.text.isNullOrBlank()) {
                prenom.error = "Saisie obligatoire"
                valide = false
            }
            if (numTel.text.isNullOrBlank()) {
                numTel.error = "Saisie obligatoire"
                valide = false
            }else {
                val numPhone = numTel.text.toString()
                if (!Pattern.matches("^[+]?\\d+$", numPhone)) {
                    valide = false
                    numTel.error = "Number de téléphone invalid"
                }
            }

            val emailText = email.text.toString()
            if (!emailText.isNullOrBlank() && !Pattern.matches("^(.+)@(.+)\\.[a-zA-Z0-9]{2,6}$", emailText)) {
                valide = false
                email.error = "Email invalide"
            }
            if (valide) {
                if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_PHONE_STATE), 500)
                }
                if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    val restId = 0
                    val imgUriStr = if(this.imageUri != null) {
                        imageUri.toString()
                    } else {
                        null
                    }

                    val newContact = Contact(nom.text.toString(),prenom.text.toString(),ddn.text.toString(),numTel.text.toString(),
                                     email.text.toString(), addresse.text.toString(), codePostal.text.toString(), genre, restId, imgUriStr)
                    sendSMS()
                    val resultIntent = Intent()
                    resultIntent.putExtra(KEY_FORM, newContact)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS), 100)
                }
            }

        }
        email.setOnFocusChangeListener { view, b -> email.error = null }
        nom.addTextChangedListener(validateText(nom))
        prenom.addTextChangedListener(validateText(prenom))
        numTel.addTextChangedListener(validateText(numTel))

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    Toast.makeText(this,"Non permission à la galerie!", Toast.LENGTH_LONG)
                }
            100 ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSMS()
                } else {
                    Toast.makeText(this,"Non permission à envoyer un sms!", Toast.LENGTH_LONG)
                }
            500 ->
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Non permission à lire un status de téléphone!", Toast.LENGTH_LONG)
                }
        }
    }
    private fun sendSMS() {

        val smsIntent = Intent(Intent.ACTION_VIEW)
        smsIntent.data = Uri.parse("smsto:")
        smsIntent.type = "vnd.android-dir/mms-sms"
        smsIntent.putExtra("NUMBER", String(charArrayOf('5','5','5','4')))
        smsIntent.putExtra("SMS_BODY", "Votre contact est enregistré" )
        try {
            startActivity(smsIntent)
            finish()
            Log.d("sms send","Finished sending SMS...")
        } catch (ex: Exception){
            ex.message?.let { Log.d("send sms error", it) }
        }

    }

   /* private fun sendSMS() {
        var smsManager: SmsManager? = null
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                smsManager = this.getSystemService(SmsManager::class.java)
            } else {
                smsManager = SmsManager.getDefault()
            }
      //      val numPhone = numTel.text.toString()
            val numPhone = "5554";

            smsManager.sendTextMessage(numPhone, null, "Votre contact est enregistré",null, null)
        } catch (e:Exception) {
            e.message?.let { Log.d("send sms error", it) }
        }

    }*/

    private fun capturePhoto() {
        val capturedImage = File(externalCacheDir, "My_Captured_Photo.jpg")
        if(capturedImage.exists()) {
            capturedImage.delete()
        }
        capturedImage.createNewFile()
        imageUri = if(Build.VERSION.SDK_INT >= 24){
            FileProvider.getUriForFile(this, this.packageName +".provider",capturedImage)
        } else {
            Uri.fromFile(capturedImage)
        }
        val pictureIntent =  Intent("android.media.action.IMAGE_CAPTURE")
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        activityCapture.launch((pictureIntent))
    }

    private fun openGallery() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        activityGallery.launch(intent)
    }

    fun onClickDatePicker(view: View) {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(this,DatePickerDialog.OnDateSetListener {view, year, monthOfYear, dayOfMonth ->
      //      val ddn = findViewById<EditText>(id.editTextDDN)
            ddn.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
        }, year, month, day)
        dpd.show()
    }

    fun validateText(editText: EditText) : TextWatcher {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                // Code to be executed after the text has changed
                if (s.isNullOrBlank()) {
                    editText.error = "Saisie obligatoire"
                } else {
                    editText.error = null
                }
            }
        }
        return textWatcher
    }

}