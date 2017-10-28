package com.sharpdroid.registroelettronico.Adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.sharpdroid.registroelettronico.Adapters.Holders.Holder
import com.sharpdroid.registroelettronico.Databases.Entities.Choice
import com.sharpdroid.registroelettronico.Views.Cells.ValueDetailsCheckboxCell
import java.util.*

class LoginAdapter(choiceList: List<Choice>, private val c: Context, private val listener: (checked: List<String>) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val choices = ArrayList<Choice>()
    private val checked = mutableListOf<String>()

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
            val checkboxCell = (holder.itemView as ValueDetailsCheckboxCell)
            checkboxCell.setTextAndValue(name, ident, position != itemCount - 1)
            checkboxCell.setOnClickListener { _ ->
                checkboxCell.isChecked = !checkboxCell.isChecked
                if (checkboxCell.isChecked)
                    checked.add(ident)
                else
                    checked.remove(ident)
                listener.invoke(checked)
            }
            checkboxCell.setCheckBoxListener(View.OnClickListener { _ ->
                if (checkboxCell.isChecked)
                    checked.add(ident)
                else
                    checked.remove(ident)
                listener.invoke(checked)
            })
        }
        val attrs = intArrayOf(android.R.attr.selectableItemBackground)
        val ta = c.obtainStyledAttributes(attrs)
        val drawableFromTheme = ta.getDrawable(0)
        val wrapper = LayerDrawable(arrayOf<Drawable>(ColorDrawable(Color.WHITE), drawableFromTheme))
        ta.recycle()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            holder.itemView.background = wrapper
        } else {
            @Suppress("DEPRECATION")
            holder.itemView.setBackgroundDrawable(wrapper)
        }
    }

    override fun getItemCount(): Int {
        return choices.size
    }
}