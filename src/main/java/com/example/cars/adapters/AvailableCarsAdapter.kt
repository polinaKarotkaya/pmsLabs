package com.example.cars.adapters

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cars.R
import com.example.cars.entity.Car

class AvailableCarsAdapter(
    private val availableCars: List<Car>,
    private val onAddToCart: (Car) -> Unit,
    private val onItemClick: (Car) -> Unit
) : RecyclerView.Adapter<AvailableCarsAdapter.AvailableCarsViewHolder>() {

    companion object {
        private const val DOUBLE_CLICK_TIME_DELTA = 300
        private var lastClickTime: Long = 0
    }

    class AvailableCarsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val makeModelTextView: TextView = itemView.findViewById(R.id.makeModelTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        val addToCartButton: Button = itemView.findViewById(R.id.addToCartButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableCarsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_available_car, parent, false)
        return AvailableCarsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AvailableCarsViewHolder, position: Int) {
        val car = availableCars[position]
        val context = holder.itemView.context
        holder.makeModelTextView.text = context.getString(R.string.make_model, car.make, car.model)
        holder.priceTextView.text = context.getString(R.string.price, car.price)

        // Добавление обработчика двойного нажатия
        holder.itemView.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                // Двойное нажатие
                onAddToCart(car)
            }
            lastClickTime = SystemClock.elapsedRealtime()
        }

        holder.addToCartButton.setOnClickListener {
            onItemClick(car)
        }
    }

    override fun getItemCount(): Int {
        return availableCars.size
    }
}