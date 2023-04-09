package com.d4rk.cartcalculator.ui.home
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.d4rk.cartcalculator.MainActivity
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.adapters.CartItemAdapter
import com.d4rk.cartcalculator.data.CartItem
import com.d4rk.cartcalculator.databinding.FragmentHomeBinding
import com.d4rk.cartcalculator.ui.viewmodel.ViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
class HomeFragment : Fragment(), MainActivity.CartListener, CartItemAdapter.OnQuantityChangeListener {
    private lateinit var viewModel: ViewModel
    private lateinit var cartItemAdapter: CartItemAdapter
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var originalNavBarColor: Int? = null
    private var totalCost: Double = 0.00
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = ViewModelProvider(this)[ViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        MobileAds.initialize(requireContext())
        binding.adView.loadAd(AdRequest.Builder().build())
        val recyclerView = binding.recyclerViewCart
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        cartItemAdapter = CartItemAdapter(emptyList(), this)
        recyclerView.adapter = cartItemAdapter
        originalNavBarColor = activity?.window?.navigationBarColor
        binding.textViewTotal.text = getString(R.string.total_default_value)
        setNavigationBarColor()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val drawerLayout: DrawerLayout? = activity?.findViewById(R.id.drawer_layout)
        drawerLayout?.addDrawerListener(drawerListener)
        setNavigationBarColor()
    }
    override fun onDestroy() {
        super.onDestroy()
        val drawerLayout: DrawerLayout? = activity?.findViewById(R.id.drawer_layout)
        drawerLayout?.removeDrawerListener(drawerListener)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        activity?.window?.navigationBarColor = originalNavBarColor!!
    }
    override fun onPause() {
        super.onPause()
        activity?.window?.navigationBarColor = originalNavBarColor!!
        setNavigationBarColor()
    }
    override fun onResume() {
        super.onResume()
        activity?.window?.navigationBarColor = originalNavBarColor!!
        setNavigationBarColor()
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        @Suppress("DEPRECATION")
        super.onActivityCreated(savedInstanceState)
        (activity as? MainActivity)?.setCartListener(this)
    }
    override fun onCartUpdated(cartItems: List<CartItem>) {
        cartItemAdapter.setItems(cartItems)
        cartItemAdapter.notifyDataSetChanged()
        val textView = binding.textViewEmpty
        if (cartItems.isEmpty()) {
            textView.visibility = View.VISIBLE
        } else {
            textView.visibility = View.GONE
        }
        updateTotalCost(cartItems)
    }
    override fun onQuantityChanged(cartItems: List<CartItem>) {
        updateTotalCost(cartItems)
    }
    private fun updateTotalCost(cartItems: List<CartItem>) {
        totalCost = cartItems.sumOf { it.totalPrice() }
        binding.textViewTotal.text = String.format("%.2f $", totalCost).replace(",", ".")
    }
    private fun setNavigationBarColor() {
        when (findNavController().currentDestination?.id) {
            R.id.nav_home -> {
                val typedValue = TypedValue()
                val theme = requireContext().theme
                theme.resolveAttribute(R.attr.colorPrimaryContainer, typedValue, true)
                val color = typedValue.data
                activity?.window?.navigationBarColor = color
            }
            else -> activity?.window?.navigationBarColor = originalNavBarColor!!
        }
    }
    private val drawerListener = object : DrawerLayout.DrawerListener {
        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
        override fun onDrawerOpened(drawerView: View) {
            activity?.window?.navigationBarColor = originalNavBarColor!!
        }
        override fun onDrawerClosed(drawerView: View) {
            setNavigationBarColor()
        }
        override fun onDrawerStateChanged(newState: Int) {}
    }
}