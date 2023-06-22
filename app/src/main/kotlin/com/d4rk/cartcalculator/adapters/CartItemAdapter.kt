package com.d4rk.cartcalculator.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.d4rk.cartcalculator.data.CartItem
import com.d4rk.cartcalculator.databinding.ItemListCartBinding
class CartItemAdapter(private var cartItems: List<CartItem>, private val listener: OnQuantityChangeListener? = null) : RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListCartBinding.inflate(inflater, parent, false)
        return CartItemViewHolder(binding)
    }
    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val cartItem = cartItems[position]
        if (cartItem.quantity > 0) {
            holder.binding.textViewListCartItemsName.text = cartItem.name
            holder.binding.textViewListCartItemsPrice.text = cartItem.totalPrice().toString()
            holder.binding.textViewListCartItemsQuantity.text = cartItem.quantity.toString()
            holder.binding.buttonListCartItemsPlusItem.setOnClickListener {
                cartItem.quantity++
                listener?.onQuantityChanged(cartItems)
                notifyItemChanged(position)
            }
            holder.binding.buttonListCartItemsMinusItem.setOnClickListener {
                cartItem.quantity--
                if (cartItem.quantity <= 0) {
                    cartItems = cartItems.filter { it != cartItem }
                    listener?.onQuantityChanged(cartItems)
                    notifyItemRemoved(position)
                } else {
                    listener?.onQuantityChanged(cartItems)
                    notifyItemChanged(position)
                }
            }
        } else {
            cartItems = cartItems.filter { it != cartItem }
            listener?.onQuantityChanged(cartItems)
            notifyItemRemoved(position)
        }
    }
    override fun getItemCount() = cartItems.size
    fun setItems(items: List<CartItem>) {
        cartItems = items.filter { it.quantity > 0 }
        notifyItemRangeChanged(0, cartItems.size)
    }
    interface OnQuantityChangeListener {
        fun onQuantityChanged(cartItems: List<CartItem>)
    }
    inner class CartItemViewHolder(val binding: ItemListCartBinding) : RecyclerView.ViewHolder(binding.root)
}