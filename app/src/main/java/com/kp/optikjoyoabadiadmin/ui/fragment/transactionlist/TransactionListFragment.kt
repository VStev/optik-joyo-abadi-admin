package com.kp.optikjoyoabadiadmin.ui.fragment.transactionlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kp.optikjoyoabadiadmin.R
import com.kp.optikjoyoabadiadmin.adapters.TransactionAdapter
import com.kp.optikjoyoabadiadmin.databinding.FragmentTransactionListBinding

class TransactionListFragment : Fragment() {

    private lateinit var transactionAdapter: TransactionAdapter
    private val fireDB = Firebase.firestore
    private var _binding: FragmentTransactionListBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        transactionAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        transactionAdapter.stopListening()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //remove after development
        FirebaseFirestore.setLoggingEnabled(true)
        val rv: RecyclerView = view.findViewById(R.id.rv_transaction_item)
        val query = fireDB.collection("Transactions")
        transactionAdapter = object : TransactionAdapter(query) {
            override fun onDataChanged() {
                super.onDataChanged()
                if (itemCount == 0){
                    binding.rvTransactionItem.visibility = View.GONE
                    binding.noItemLayout.visibility = View.VISIBLE
                }else{
                    binding.rvTransactionItem.visibility = View.VISIBLE
                    binding.noItemLayout.visibility = View.GONE
                }
            }

            override fun onError(e: FirebaseFirestoreException) {
                Snackbar.make(binding.root, "Error connecting to database", Snackbar.LENGTH_LONG).show()
            }
        }
        with (rv){
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }
    }
}