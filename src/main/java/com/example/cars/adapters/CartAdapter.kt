package com.example.cars.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cars.entity.Car
import com.example.cars.R

class CartAdapter(
    private val cart: List<Car>,
    private val onRemoveFromCart: (Car) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val carMakeModel: TextView = itemView.findViewById(R.id.carMakeModel)
        val carPrice: TextView = itemView.findViewById(R.id.carPrice)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val car = cart[position]
        holder.carMakeModel.text = "${car.make} ${car.model}"
        holder.carPrice.text = "Цена: ${car.price} руб."
        holder.deleteButton.setOnClickListener {
            onRemoveFromCart(car)
        }
    }

    override fun getItemCount(): Int {
        return cart.size
    }
}
