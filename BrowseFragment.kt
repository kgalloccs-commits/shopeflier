package com.shopeflier.app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.shopeflier.app.ProductDetailActivity
import com.shopeflier.app.databinding.FragmentBrowseBinding
import com.shopeflier.app.utils.ProductManager

class BrowseFragment : Fragment() {
    
    private var _binding: FragmentBrowseBinding? = null
    private val binding get() = _binding!!
    private lateinit var productManager: ProductManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBrowseBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productManager = ProductManager(requireContext())
        setupUI()
    }
    
    private fun setupUI() {
        val products = productManager.getAllProducts()
        
        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                val results = productManager.searchProducts(query)
                
                // Show first result details if any
                if (results.isNotEmpty()) {
                    val firstProduct = results.first()
                    openProductDetail(firstProduct)
                }
            }
        }
        
        if (products.isEmpty()) {
            binding.tvNoProducts.visibility = View.VISIBLE
            binding.tvNoProducts.text = "No products available yet\n\nBe the first to list an item!"
            binding.tvNoProducts.setOnClickListener {
                (activity as? com.shopeflier.app.DashboardActivity)?.let { dashboardActivity ->
                    dashboardActivity.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                        com.shopeflier.app.R.id.bottom_navigation
                    )?.selectedItemId = com.shopeflier.app.R.id.nav_sell
                }
            }
        } else {
            binding.tvNoProducts.visibility = View.GONE
        }
    }
    
    private fun openProductDetail(product: com.shopeflier.app.models.Product) {
        val intent = Intent(requireContext(), ProductDetailActivity::class.java).apply {
            putExtra("PRODUCT_ID", product.id)
        }
        startActivity(intent)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
