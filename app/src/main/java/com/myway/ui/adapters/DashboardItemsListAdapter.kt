package com.myway.ui.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.myway.R
import com.myway.firestore.FirestoreClass
import com.myway.models.Product
import com.myway.ui.activities.DashboardActivity
import com.myway.ui.activities.OwnPageActivity
import com.myway.ui.fragments.OwnPageProductFragment
import com.myway.utils.Constants
import com.myway.utils.GlideLoader
import kotlinx.android.synthetic.main.item_dashboard_layout.view.*


/**
 * A adapter class for dashboard items list.
 */
open class DashboardItemsListAdapter(
        private val context: Context,
        private var list: ArrayList<Product>
        ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // A global variable for OnClickListener interface.
    private var onClickListener: OnClickListener? = null
    private val TAG = DashboardItemsListAdapter::class.java.simpleName
    private val mFireStore = FirebaseFirestore.getInstance()
    private var userImage: String=""

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        return MyViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.item_dashboard_layout,
                        parent,
                        false
                )
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val model = list[position]



        if (holder is MyViewHolder) {

            GlideLoader(context).loadProductPicture(
                model.image,
                holder.itemView.iv_post_image
            )

            mFireStore.collection(Constants.USERS)
                .document(model.user_id)
                .get()
                .addOnSuccessListener { document ->
                    userImage = document.get("image").toString()

                    GlideLoader(context).loadUserPicture(
                        userImage,
                        holder.itemView.iv_post_user_image
                    )
                }

            holder.itemView.tv_post_item_caption.text = model.title
            holder.itemView.tv_dashboard_item_price.text = "$${model.price}"

            holder.itemView.iv_post_image.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
            if(context is DashboardActivity) {
                holder.itemView.iv_post_user_image.setOnClickListener {
                    if (onClickListener != null) {
                        val intent = Intent(context, OwnPageActivity::class.java)
                        intent.putExtra("product_owner",model.user_id);
                        context.startActivity(intent)
                    }
                }

            }
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A function for OnClickListener where the Interface is the expected parameter and assigned to the global variable.
     *
     * @param onClickListener
     */
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    /**
     * An interface for onclick items.
     */
    interface OnClickListener {

        fun onClick(position: Int, product: Product)
    }



    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}