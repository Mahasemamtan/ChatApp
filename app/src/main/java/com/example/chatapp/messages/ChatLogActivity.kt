package com.example.chatapp.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.FontsContract
import android.util.Log
import com.example.chatapp.R
import com.example.chatapp.model.ChatMessage
import com.example.chatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
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
        val fromId = FirebaseAuth.getInstance().uid
        val toId = user?.uid

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        reference.addChildEventListener (object: ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return

                if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
                    adapter.add(ChatFromItem(chatMessage.text, LatestMessagesActivity.currentUser ?: return))
                } else {
                    adapter.add(ChatToItem(chatMessage.text, user!!))
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

        val fromId = FirebaseAuth.getInstance().uid ?: return
        val toId = user?.uid ?: return

//        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key ?: return, message, fromId, toId, System.currentTimeMillis() / 1000)

        reference.setValue(chatMessage)
            .addOnSuccessListener {
                edittext_chat_log.text.clear()
                recycler_view_chat_log.scrollToPosition(adapter.itemCount - 1)
            }

        toReference.setValue(chatMessage)
    }
}

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
