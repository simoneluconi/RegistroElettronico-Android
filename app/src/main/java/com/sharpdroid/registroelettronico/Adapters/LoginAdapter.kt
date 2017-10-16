package com.sharpdroid.registroelettronico.Adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.sharpdroid.registroelettronico.Adapters.Holders.Holder
import com.sharpdroid.registroelettronico.Databases.Entities.Choice
import com.sharpdroid.registroelettronico.Views.Cells.ValueDetailsCheckboxCell
import java.util.*

class LoginAdapter(private val mPassword: String, private val mEmail: String, choiceList: List<Choice>, val c: Context, private val listener: (checked: List<String>) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val choices = ArrayList<Choice>()
    val checked = mutableListOf<String>()

    init {
        choices.clear()
        choices.addAll(choiceList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return Holder(ValueDetailsCheckboxCell(parent.context))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val (_, ident, name) = choices[position]
        if (holder.itemView is ValueDetailsCheckboxCell) {
            val holder = (holder.itemView as ValueDetailsCheckboxCell)
            holder.setTextAndValue(name, ident, position != itemCount - 1)
            holder.setOnClickListener { _ ->
                holder.isChecked = !holder.isChecked
                if (holder.isChecked)
                    checked.add(ident)
                else
                    checked.remove(ident)
                listener.invoke(checked)
            }
            holder.setCheckBoxListener { _ ->
                if (holder.isChecked)
                    checked.add(ident)
                else
                    checked.remove(ident)
                listener.invoke(checked)
            }
        }
        val attrs = intArrayOf(android.R.attr.selectableItemBackground)
        val ta = c.obtainStyledAttributes(attrs)
        val drawableFromTheme = ta.getDrawable(0)
        val wrapper = LayerDrawable(arrayOf<Drawable>(ColorDrawable(Color.WHITE), drawableFromTheme))
        ta.recycle()
        holder.itemView.setBackgroundDrawable(wrapper)
    }

    override fun getItemCount(): Int {
        return choices.size
    }
}