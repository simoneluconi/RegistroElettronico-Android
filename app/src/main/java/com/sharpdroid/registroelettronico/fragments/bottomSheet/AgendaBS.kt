package com.sharpdroid.registroelettronico.fragments.bottomSheet

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.entities.LocalAgenda
import com.sharpdroid.registroelettronico.database.entities.SuperAgenda
import kotlinx.android.synthetic.main.fragment_item_agenda_options.view.*

class AgendaBS : BottomSheetDialogFragment() {
    private val texts = intArrayOf(R.string.condividi_bs, R.string.archivia_bs)
    private val icons = intArrayOf(R.drawable.agenda_bsheet_share, R.drawable.agenda_bsheet_archive)
    private var mListener: Listener? = null
    private var event: Any? = null

    fun setEvent(event: Any) {
        this.event = event
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_item_list_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ItemAdapter(mListener!!)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val parent = parentFragment
        mListener = if (parent != null) {
            parent as Listener
        } else {
            context as Listener?
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface Listener {
        fun onBottomSheetItemClicked(position: Int, e: Any)
    }

    private inner class ItemAdapter(val clickListener: Listener) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bindData(text: String, color: Int, image: Int, tint: Int?) {
                itemView.text.text = text
                itemView.text.setTextColor(color)
                itemView.image.setImageResource(image)
                itemView.image.setColorFilter(tint ?: ContextCompat.getColor(activity.applicationContext, R.color.icon_on_white))
                itemView.setOnClickListener {
                    clickListener.onBottomSheetItemClicked(adapterPosition, event ?: throw NullPointerException("event must not be null"))
                    dismiss()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_item_agenda_options, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (event is SuperAgenda) {
                when (position) {
                    0 -> holder.bindData(if ((event as SuperAgenda).completed) "Non completato" else "Completato",
                            ContextCompat.getColor(context, R.color.greenmaterial_800),
                            if ((event as SuperAgenda).completed) R.drawable.agenda_uncomplete else R.drawable.agenda_complete, ContextCompat.getColor(context, R.color.greenmaterial_800))
                    1 -> holder.bindData(if ((event as SuperAgenda).test) "Rimuovi contrassegno" else "Contrassegna come compito",
                            ContextCompat.getColor(context, R.color.redmaterial),
                            if ((event as SuperAgenda).test) R.drawable.agenda_uncomplete else R.drawable.agenda_complete, ContextCompat.getColor(context, R.color.redmaterial))
                    else -> holder.bindData(getString(texts[position - 2]), Color.BLACK, icons[position - 2], null)
                }
            } else if (event is LocalAgenda) {
                when (position) {
                    0 -> holder.bindData(if ((event as LocalAgenda).completed_date != null) "Non completato" else "Completato",
                            ContextCompat.getColor(context, R.color.greenmaterial_800),
                            if ((event as LocalAgenda).completed_date != null) R.drawable.agenda_uncomplete else R.drawable.agenda_complete, ContextCompat.getColor(context, R.color.greenmaterial_800))
                    1 -> holder.bindData(if ((event as LocalAgenda).type == "verifica") "Rimuovi contrassegno" else "Contrassegna come compito",
                            ContextCompat.getColor(context, R.color.redmaterial),
                            if ((event as LocalAgenda).type == "verifica") R.drawable.agenda_uncomplete else R.drawable.agenda_complete, ContextCompat.getColor(context, R.color.redmaterial))
                    else -> holder.bindData(getString(texts[position - 2]), Color.BLACK, icons[position - 2], null)
                }
            }
        }

        override fun getItemCount(): Int {
            return mItemCount
        }

    }

    companion object {
        private val mItemCount = 4
    }
}
