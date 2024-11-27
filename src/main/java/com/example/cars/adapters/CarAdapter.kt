package com.example.cars.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cars.R
import com.example.cars.entity.Car
import com.example.cars.entity.CarItem

class CarAdapter(
    private val carItems: List<CarItem>,
    private val carDetails: Map<Int, Car>,
    private val onCarItemLongPress: (CarItem) -> Unit,
    private val onCarItemSelected: (CarItem) -> Unit
) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val carInfo: TextView = itemView.findViewById(R.id.carInfo)
        val carItemStatus: TextView = itemView.findViewById(R.id.carItemStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CarViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val carItem = carItems[position]
        val car = carDetails[carItem.carId]
        holder.carInfo.text = "${car?.make} ${car?.model}"
        holder.carItemStatus.text = "Статус: \${carItem.status}"
        holder.itemView.setOnLongClickListener {
            onCarItemLongPress(carItem)
            onCarItemSelected(carItem)
            true
        }
    }

    override fun getItemCount(): Int {
        return carItems.size
    }
}
