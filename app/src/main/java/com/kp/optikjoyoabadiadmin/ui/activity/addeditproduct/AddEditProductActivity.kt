package com.kp.optikjoyoabadiadmin.ui.activity.addeditproduct

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kp.optikjoyoabadiadmin.R
import com.kp.optikjoyoabadiadmin.databinding.ActivityAddEditProductBinding
import com.kp.optikjoyoabadiadmin.databinding.ActivityTransactionDetailBinding
import com.kp.optikjoyoabadiadmin.model.Product
import com.kp.optikjoyoabadiadmin.ui.activity.transactiondetail.TransactionDetailActivity
import kotlin.random.Random

class AddEditProductActivity : AppCompatActivity() {

    private lateinit var imageUri: Uri
    private var _binding: ActivityAddEditProductBinding? = null
    private lateinit var fireDB: FirebaseFirestore
    private lateinit var productId: String
    private lateinit var argument: String
    private lateinit var viewModel: AddEditProductViewModel
    private val binding get() = _binding!!

    companion object {
        const val  EXTRA_ID = "idcon"
        const val EXTRA_ARGUMENT = "a"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(AddEditProductViewModel::class.java)
        productId = intent.getStringExtra(EXTRA_ID).toString()
        argument = intent.getStringExtra(EXTRA_ARGUMENT).toString()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.productAddImage.setOnClickListener {
            val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                if (it.resultCode == Activity.RESULT_OK && it.data != null){
                    val selectedImage: Uri? = it.data!!.data
                    if (selectedImage != null) {
                        imageUri = selectedImage
                        binding.productAddImage.setImageURI(imageUri)
                    }
                }
            }

            val options = arrayOf<CharSequence>("Choose from Gallery", "Cancel")

            val builder: AlertDialog.Builder = AlertDialog.Builder(baseContext)
            builder.setTitle("Choose product picture")

            builder.setItems(options) { dialog, item ->
                when {
//                    options[item] == "Take Photo" -> {
//                        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                        //i cant think anything to circumvent this deprecated thing
//                        startActivityForResult(takePicture, 0)
//                    }
                    options[item] == "Choose from Gallery" -> {
                        val pickPhoto =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        resultLauncher.launch(pickPhoto)
                    }
                    options[item] == "Cancel" -> {
                        dialog.dismiss()
                    }
                }
            }
            builder.show()
        }

        binding.buttonAdd.setOnClickListener {
            var uniqueIdentifier = ""
            val randomized = Random.nextInt(0, 100)
            val cUID = resources.getStringArray(R.array.categories_uid)
            val sUID = resources.getStringArray(R.array.shape_uid)
            val category = binding.addKategori.selectedItem.toString()
            val shape = binding.addBentuk.selectedItem.toString()
            when (category) {
                "Kacamata" -> {
                    uniqueIdentifier += cUID[0]
                }
                "Sunglasses" -> {
                    uniqueIdentifier += cUID[1]
                }
                "Softlens" -> {
                    uniqueIdentifier += cUID[2]
                }
            }
            when (shape) {
                "Bulat" -> {
                    uniqueIdentifier += sUID[0]
                }
                "Kotak" -> {
                    uniqueIdentifier += sUID[1]
                }
                "Frameless" -> {
                    uniqueIdentifier += sUID[2]
                }
                "Aksesoris" -> {
                    uniqueIdentifier += sUID[3]
                }
            }
            uniqueIdentifier = uniqueIdentifier + randomized.toString() + binding.addProductName.toString()
            //"CAT+SHAPE+RAND100+3DIGITNAMESTART+3DIGITNAMEFINISH"
            val picturePath = uniqueIdentifier + binding.addProductName.toString()
            val product = Product(
                uniqueIdentifier,
                binding.addProductName.text.toString(),
                category,
                shape,
                binding.addHargaProduct.text.toString().toInt(),
                binding.addStockProduct.text.toString().toInt(),
                binding.addDetailProduct.text.toString(),
                picturePath
            )
            viewModel.submitData(product, imageUri).observe(this, {
                if(it){
                    Toast.makeText(baseContext, "Sukses menambahkan barang", Toast.LENGTH_LONG).show()
                    finish()
                }else{
                    Toast.makeText(baseContext, "An Error Occurred!", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun setContentView() {
        when (argument){
            "a" -> {
                binding.buttonAdd.text = getString(R.string.tambah_barang)
            }
            "b" -> {
                binding.buttonAdd.text = getString(R.string.update_barang)
                val query = fireDB.collection("Products").document(productId)
                val reference = Firebase.storage.reference
                query.get()
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            val product = it.result?.toObject<Product>()
                            binding.addProductName.setText(product?.productName)
                            binding.addHargaProduct.setText(product?.price.toString())
                            binding.addStockProduct.setText(product?.stock.toString())
                            binding.addDetailProduct.setText(product?.details)
                            val image = reference.child("Products/${product?.image_url}")
                            Glide.with(binding.root)
                                .load(image)
                                .override(256,256)
                                .into(binding.productAddImage)
                        }
                    }
                    .addOnFailureListener {

                    }
            }
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode != Activity.RESULT_CANCELED) {
//            when (requestCode) {
//                0 -> if (resultCode == Activity.RESULT_OK && data != null) {
//                    val imageBitmap = data.extras?.get("data") as Bitmap
//                }
//                1 -> if (resultCode == Activity.RESULT_OK && data != null) {
//                    val selectedImage: Uri? = data.data
//                    if (selectedImage != null) {
//                        imageUri = selectedImage
//                        binding.productAddImage.setImageURI(imageUri)
//                    }
//                }
//            }
//        }
//    }
}