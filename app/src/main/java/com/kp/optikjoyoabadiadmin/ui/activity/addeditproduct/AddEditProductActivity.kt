package com.kp.optikjoyoabadiadmin.ui.activity.addeditproduct

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.kp.optikjoyoabadiadmin.GlideApp
import com.kp.optikjoyoabadiadmin.R
import com.kp.optikjoyoabadiadmin.databinding.ActivityAddEditProductBinding
import com.kp.optikjoyoabadiadmin.model.Product
import kotlin.random.Random

class AddEditProductActivity : AppCompatActivity() {

    private lateinit var imageUri: Uri
    private var _binding: ActivityAddEditProductBinding? = null
    private val fireDB = Firebase.firestore
    private lateinit var productId: String
    private lateinit var argument: String
    private lateinit var viewModel: AddEditProductViewModel
    private val binding get() = _binding!!

    //need to be declared here otherwise cant work
    //replaces the old onActivityResult
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                val selectedImage: Uri? = it.data!!.data
                if (selectedImage != null) {
                    imageUri = selectedImage
                    binding.productAddImage.setImageURI(imageUri)
                }
            }
        }

    companion object {
        const val EXTRA_ID = "idcon"
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
            val options = arrayOf<CharSequence>("Choose from Gallery", "Cancel")
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Choose product picture")
            builder.setItems(options) { dialog, item ->
                when {
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
            Log.d("TAG", "setOnClickListeners: PRESSED ADD")
            when (argument) {
                "a" -> {
                    Log.d("TAG", "setOnClickListeners: PRESSED ADD ARGUMENT A")
                    var uniqueIdentifier = ""
                    //randomizes number for UID
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
                    val productName = binding.addProductName.text.toString()
                    uniqueIdentifier =
                        uniqueIdentifier + randomized.toString() + productName.substring(0,3)
                    val picturePath = uniqueIdentifier + productName
                    Log.d("TAG", "setOnClickListeners: $uniqueIdentifier")
                    val product = Product(
                        uniqueIdentifier,
                        productName,
                        category,
                        shape,
                        binding.addHargaProduct.text.toString().toInt(),
                        binding.addStockProduct.text.toString().toInt(),
                        binding.addDetailProduct.text.toString(),
                        picturePath
                    )
                    viewModel.submitData(product, imageUri).observe(this, {
                        if (it == "Success!") {
                            Toast.makeText(
                                baseContext,
                                "Sukses menambahkan barang",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        } else if (it == "Failure!") {
                            Toast.makeText(baseContext, "An Error Occurred!", Toast.LENGTH_LONG)
                                .show()
                        }
                    })
                }
                "b" -> {
                    val reference = fireDB.collection("Products").document(productId)
                    val category = binding.addKategori.selectedItem.toString()
                    val shape = binding.addBentuk.selectedItem.toString()
                    reference
                        .update(
                            mapOf(
                                "details" to binding.addDetailProduct.text.toString(),
                                "price" to binding.addHargaProduct.text.toString().toInt(),
                                "stock" to binding.addStockProduct.text.toString().toInt(),
                                "category" to category,
                                "shape" to shape,
                                "productName" to binding.addProductName.text.toString()
                            )
                        )
                        .addOnSuccessListener {
                            Toast.makeText(baseContext, "Sukses mengubah barang", Toast.LENGTH_LONG)
                                .show()
                            finish()
                        }
                }
            }
        }
    }

    private fun setContentView() {
        when (argument) {
            "a" -> {
                binding.buttonAdd.text = getString(R.string.tambah_barang)
            }
            "b" -> {
                binding.buttonAdd.text = getString(R.string.update_barang)
                val query = fireDB.collection("Products").document(productId)
                binding.addKategori.isEnabled = false
                binding.addBentuk.isEnabled = false
                val reference = Firebase.storage.reference
                query.get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val product = it.result?.toObject<Product>()
                            binding.addProductName.setText(product?.productName)
                            binding.addHargaProduct.setText(product?.price.toString())
                            binding.addStockProduct.setText(product?.stock.toString())
                            binding.addDetailProduct.setText(product?.details)
                            when (product?.category){
                                "Kacamata" -> {
                                    binding.addKategori.setSelection(0)
                                }
                                "Sunglasses" -> {
                                    binding.addKategori.setSelection(1)
                                }
                                "Softlens" -> {
                                    binding.addKategori.setSelection(2)
                                }
                            }
                            when (product?.shape){
                                "Bulat" -> {
                                    binding.addBentuk.setSelection(0)
                                }
                                "Kotak" -> {
                                    binding.addBentuk.setSelection(1)
                                }
                                "Frameless" -> {
                                    binding.addBentuk.setSelection(2)
                                }
                                "Aksesoris" -> {
                                    binding.addBentuk.setSelection(3)
                                }
                            }
                            val image = reference.child("products/${product?.image_url}")
                            GlideApp.with(binding.root)
                                .load(image)
                                .into(binding.productAddImage)
                        }
                    }
                    .addOnFailureListener {

                    }
            }
        }
    }
}