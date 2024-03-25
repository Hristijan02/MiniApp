package si.uni_lj.fri.pbd.miniapp1

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.util.Base64


class RecyclerAdapter(private val memoList: List<MemoModel>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerAdapter.CardViewHolder?>() {


    inner class CardViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var itemImage: ImageView? = itemView?.findViewById(R.id.item_image)
        var itemTitle: TextView? = itemView?.findViewById(R.id.item_title)
        var itemDetail: TextView? = itemView?.findViewById(R.id.item_detail)

        init {
            itemView?.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val memo = memoList[position]
                    listener.onItemClick(position, memo)
                }

            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CardViewHolder {

        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.recycler_item_memo_model,
            viewGroup, false)
        return CardViewHolder(view)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, memo: MemoModel)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(viewHolder: CardViewHolder, i: Int) {

        val memo = memoList[i]
        viewHolder.itemTitle?.text = memo.title
        viewHolder.itemDetail?.text = memo.timestamp

        val tempImageBase64 = memo.imageBase64
        val imageBytes = Base64.decode(tempImageBase64, Base64.DEFAULT) // Decodes the Base64-encoded string into a byte array
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size) //  Decodes a byte array into a bitmap image.
        if(bitmap == null)
        viewHolder.itemImage?.setImageResource(R.drawable.placeholder)
        else
        viewHolder.itemImage?.setImageBitmap(bitmap)
    }

    override fun getItemCount(): Int {
        return memoList.size
    }

}