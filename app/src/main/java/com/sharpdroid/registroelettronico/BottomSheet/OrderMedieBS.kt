package com.sharpdroid.registroelettronico.BottomSheet

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sharpdroid.registroelettronico.BottomSheet.OrderMedieBS.OrderListener
import com.sharpdroid.registroelettronico.R
import kotlinx.android.synthetic.main.bottom_sheet_order_medie.view.*

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 * OrderMedieBS.newInstance(30).show(getSupportFragmentManager(), "dialog");
</pre> *
 *
 * You activity (or fragment) needs to implement [OrderListener].
 */
class OrderMedieBS : BottomSheetDialogFragment() {
    private val texts = intArrayOf(R.string.nome_bs, R.string.media_bs, R.string.nvoti_bs)
    private val images = intArrayOf(R.drawable.ic_title, R.drawable.ic_timeline, R.drawable.ic_timeline)
    private var mListener: OrderListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_list_order_medie, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = ItemAdapter(3, mListener!!)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val parent = parentFragment
        mListener = if (parent != null) {
            parent as OrderListener
        } else {
            context as OrderListener?
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface OrderListener {
        fun onItemClicked(position: Int)
    }


    private inner class ItemAdapter internal constructor(private val mItemCount: Int, val clickListener: OrderListener) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
        private inner class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.bottom_sheet_order_medie, parent, false)) {
            fun bind(text: String, image: Int) {
                itemView.text.text = text
                itemView.image.setImageResource(image)
                itemView.setOnClickListener {
                    itemView.postDelayed({
                        clickListener.onItemClicked(adapterPosition)
                        dismiss()
                    }, 200)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(getString(texts[position]), images[position])
        }

        override fun getItemCount(): Int {
            return mItemCount
        }
    }
}
