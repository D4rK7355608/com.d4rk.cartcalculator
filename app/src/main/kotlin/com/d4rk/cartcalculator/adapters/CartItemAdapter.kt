package com.d4rk.cartcalculator.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.d4rk.cartcalculator.data.CartItem
import com.d4rk.cartcalculator.databinding.ItemListCartBinding
class CartItemAdapter(private var cartItems: List<CartItem>) : RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListCartBinding.inflate(inflater, parent, false)
        return CartItemViewHolder(binding)
    }
    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.binding.textViewListCartItemsName.text = cartItem.name
        holder.binding.textViewListCartItemsPrice.text = cartItem.price.toString()
        holder.binding.textViewListCartItemsQuantity.text = cartItem.quantity.toString()
        holder.binding.buttonListCartItemsPlusItem.setOnClickListener {
            cartItem.quantity++
            notifyDataSetChanged()
        }
        holder.binding.buttonListCartItemsMinusItem.setOnClickListener {
            if (cartItem.quantity > 1) {
                cartItem.quantity--
                notifyDataSetChanged()
            }
        }
    }
    override fun getItemCount() = cartItems.size
    fun setItems(items: List<CartItem>) {
        cartItems = items
        notifyDataSetChanged()
    }
    inner class CartItemViewHolder(val binding: ItemListCartBinding) : RecyclerView.ViewHolder(binding.root)
}