package com.example.chatapp.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.chatapp.R
import com.example.chatapp.model.ChatMessage
import com.example.chatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    var user: User? = null
    companion object {
        val TAG = "ChatLog"
    }

    var adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        this.user = intent.getParcelableExtra<User>(NewMessageActivity.USER_NAME)
        title = this.user?.username

        recycler_view_chat_log.adapter = adapter

        listenForMessages()

        send_button_change_log.setOnClickListener {
            Log.d(TAG, edittext_chat_log.text.toString())
            performSendMessage()
        }
    }

    private fun listenForMessages() {
        val reference = FirebaseDatabase.getInstance().getReference("/messages")

        reference.addChildEventListener (object: ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return

                if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
                    adapter.add(ChatFromItem(chatMessage.text))
                } else {
                    adapter.add(ChatToItem(chatMessage.text))
                }
            }
            
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    private fun performSendMessage() {

        val message = edittext_chat_log.text.toString()
        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val id = reference.key ?: return
        val fromId = FirebaseAuth.getInstance().uid ?: return
        val toId = user?.uid ?: return

        val chatMessage = ChatMessage(id, message, fromId, toId, System.currentTimeMillis() / 1000)

        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Message saved to Firebase Database: ${reference.key}")
                edittext_chat_log.text.clear()
            }
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
