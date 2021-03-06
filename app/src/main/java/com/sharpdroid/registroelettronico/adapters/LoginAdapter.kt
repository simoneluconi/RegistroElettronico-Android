package com.sharpdroid.registroelettronico.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.sharpdroid.registroelettronico.adapters.holders.Holder
import com.sharpdroid.registroelettronico.database.entities.Choice
import com.sharpdroid.registroelettronico.views.cells.ValueDetailsCheckboxCell
import java.util.*

class LoginAdapter(choiceList: List<Choice>, private val c: Context, private val listener: (checked: List<String>) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val choices = ArrayList<Choice>()
    private val checked = mutableListOf<String>()

    init {
        choices.clear()
        choices.addAll(choiceList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            Holder(ValueDetailsCheckboxCell(parent.context))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val choice = choices[position]
        if (holder.itemView is ValueDetailsCheckboxCell) {
            val checkboxCell = (holder.itemView as ValueDetailsCheckboxCell)
            checkboxCell.setTextAndValue(choice.name, choice.ident, position != itemCount - 1)
            checkboxCell.setOnClickListener { _ ->
                checkboxCell.isChecked = !checkboxCell.isChecked
                if (checkboxCell.isChecked)
                    checked.add(choice.ident)
                else
                    checked.remove(choice.ident)
                listener.invoke(checked)
            }
            checkboxCell.setCheckBoxListener(View.OnClickListener { _ ->
                if (checkboxCell.isChecked)
                    checked.add(choice.ident)
                else
                    checked.remove(choice.ident)
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

    override fun getItemCount() = choices.size
}