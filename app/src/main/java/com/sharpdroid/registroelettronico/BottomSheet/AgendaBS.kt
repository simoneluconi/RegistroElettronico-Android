package com.sharpdroid.registroelettronico.BottomSheet

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
import com.sharpdroid.registroelettronico.Databases.Entities.SuperAgenda
import com.sharpdroid.registroelettronico.R
import kotlinx.android.synthetic.main.fragment_item_agenda_options.view.*

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 * AgendaBS.newInstance(30).show(getSupportFragmentManager(), "dialog");
</pre> *
 *
 * You activity (or fragment) needs to implement [AgendaBS.Listener].
 */
class AgendaBS : BottomSheetDialogFragment() {
    private val texts = intArrayOf(R.string.condividi_bs, R.string.inserisci_calendario_bs, R.string.copia_bs, R.string.archivia_bs)
    private val icons = intArrayOf(R.drawable.agenda_bsheet_share, R.drawable.agenda_bsheet_calendar, R.drawable.agenda_bsheet_copy, R.drawable.agenda_bsheet_archive)
    private var mListener: Listener? = null
    private var event: SuperAgenda? = null

    fun setEvent(event: SuperAgenda) {
        this.event = event
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_item_list_options, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        val recyclerView = view as RecyclerView?
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ItemAdapter(mListener!!)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mListener = parent as Listener
        } else {
            mListener = context as Listener?
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface Listener {
        fun onBottomSheetItemClicked(position: Int, e: SuperAgenda)
    }

    private inner class ItemAdapter(val clickListener: Listener) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bindData(text: String, color: Int, image: Int) {
                itemView.text.text = text
                itemView.text.setTextColor(color)
                itemView.image.setImageResource(image)
                itemView.setOnClickListener {
                    clickListener.onBottomSheetItemClicked(adapterPosition, event!!)
                    dismiss()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_item_agenda_options, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (position != 0) {
                holder.bindData(getString(texts[position - 1]), Color.BLACK, icons[position - 1])
            } else {
                holder.bindData(if (event!!.completed) "Non completato" else "Completato",
                        ContextCompat.getColor(context, R.color.intro_blue_dark),
                        if (event!!.completed) R.drawable.agenda_uncomplete else R.drawable.agenda_complete)
            }
        }

        override fun getItemCount(): Int {
            return mItemCount
        }

    }

    companion object {
        private val mItemCount = 5
    }
}
