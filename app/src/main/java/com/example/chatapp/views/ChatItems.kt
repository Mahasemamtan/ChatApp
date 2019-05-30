package com.example.chatapp.views

import com.example.chatapp.R
import com.example.chatapp.model.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatFromItem (val text: String, val user: User) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_chat_log.text = text

        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageview_from_chat_log)
    }
}

class ChatToItem (val text: String, val user: User) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_chat_log.text = text

        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageview_to_chat_log)
    }
}
