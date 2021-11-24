package com.kp.optikjoyoabadiadmin.ui.fragment.productlist

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kp.optikjoyoabadiadmin.R
import com.kp.optikjoyoabadiadmin.adapters.ProductAdapter
import com.kp.optikjoyoabadiadmin.databinding.FragmentProductListBinding
import com.kp.optikjoyoabadiadmin.ui.activity.addeditproduct.AddEditProductActivity

class ProductListFragment : Fragment() {

    private lateinit var productAdapter: ProductAdapter
    private val fireDB = Firebase.firestore
    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        productAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        productAdapter.stopListening()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //remove the line of code below after done developing
        FirebaseFirestore.setLoggingEnabled(true)
        //remove the line of code above after done developing
        val rv: RecyclerView = view.findViewById(R.id.rv_product_item_list)
        val query = fireDB.collection("Products")
        val reference = Firebase.storage.reference
        productAdapter = object : ProductAdapter(query, reference) {
            override fun onDataChanged() {
                super.onDataChanged()
                if (itemCount == 0){
                    binding.rvProductItemList.visibility = View.GONE
                    binding.noItemLayout.visibility = View.VISIBLE
                }else{
                    binding.rvProductItemList.visibility = View.VISIBLE
                    binding.noItemLayout.visibility = View.GONE
                }
            }

            override fun onError(e: FirebaseFirestoreException) {
                Snackbar.make(binding.root, "Error connecting to database", Snackbar.LENGTH_LONG).show()
            }
        }
        with (rv){
            layoutManager = LinearLayoutManager(context)
            adapter = productAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_produk, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_add_product) {
            val intent = Intent(context, AddEditProductActivity::class.java)
            intent.putExtra(AddEditProductActivity.EXTRA_ARGUMENT, "new product")
            intent.putExtra(AddEditProductActivity.EXTRA_ID, "a")
            context?.startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}