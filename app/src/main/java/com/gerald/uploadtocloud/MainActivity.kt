package com.gerald.uploadtocloud

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       val btnUpload = findViewById<Button>(R.id.btnUpload)


        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 23000)
        }
        btnUpload.setOnClickListener {
            openGalleryForImages()
        }



    }

    val REQUEST_CODE = 200

    private fun openGalleryForImages() {

        if (Build.VERSION.SDK_INT < 19) {
            var intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Choose Pictures")
                , REQUEST_CODE
            )
        }
        else { // For latest versions API LEVEL 19+
            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE);
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){

            // if multiple images are selected
            var imagesArray = arrayListOf<Uri>()
            if (data?.getClipData() != null) {
                var count = data.clipData!!.itemCount

                for (i in 0..count - 1) {
                    var imageUri: Uri = data.clipData!!.getItemAt(i).uri
                    //     iv_image.setImageURI(imageUri) Here you can assign your Image URI to the ImageViews
                    imagesArray.add(imageUri)//storage/download
                }

            } else if (data?.getData() != null) {
                // if single image is selected

                var imageUri: Uri = data.data!!
                //   iv_image.setImageURI(imageUri) Here you can assign the picked image uri to your imageview
                imagesArray.add(imageUri)
            }
            //upload to firebase storage
            uploadImages(imagesArray)
        }
    }

    private fun uploadImages(imageArray: ArrayList<Uri>){
        for (imageUri in imageArray){
            val fileName = UUID.randomUUID().toString()+".jpg"//unique name in the firebase
            val ref = FirebaseStorage.getInstance().reference.child("Pics/$fileName")//path of image
            ref.putFile(imageUri).addOnSuccessListener {
                Toast.makeText(this,"Uploading File",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
            Toast.makeText(this,"Failed to Upload",Toast.LENGTH_LONG).show()
            Log.e("IMAGE_UPLOAD_FAILURE","uploadImages: ",it) }
        }

    }
}
//https://justpasteit/5d9sg

//telling firebase to allow upload from authenticated users