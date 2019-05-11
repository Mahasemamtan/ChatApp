package com.example.chatapp.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.chatapp.R
import com.example.chatapp.model.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    var user: User? = null
    companion object {
        val TAG = "Chat Log"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        this.user = intent.getParcelableExtra<User>(NewMessageActivity.USER_NAME)
        title = this.user?.username

        setUpDummyData()

        send_button_change_log.setOnClickListener {
            Log.d(TAG, edittext_chat_log.text.toString())
            performSendMessage()
        }

    }

    private fun performSendMessage() {
        val message = edittext_chat_log.text.toString()
    }

    private fun setUpDummyData() {
        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(ChatFromItem("${user?.username} is sedning \nmessage.."))
        adapter.add(ChatToItem("Message to someone ho was so annyoing that message had to be so long"))
        adapter.add(ChatFromItem("${user?.username} is sedning message.."))
        adapter.add(ChatToItem("Message to someone ho was so annyoing that message had to be so long"))
        adapter.add(ChatFromItem("${user?.username} is sedning message.."))
        adapter.add(ChatToItem("Message to someone ho was so annyoing that message had to be so long"))

        recycler_view_chat_log.adapter = adapter
    }
}

class ChatFromItem (val text: String) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_chat_log.text = text
    }
}

class ChatToItem (val text: String) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_chat_log.text = text

    }
}
