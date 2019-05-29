package com.example.chatapp.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.chatapp.RegisterActivity
import com.example.chatapp.R
import com.example.chatapp.model.ChatMessage
import com.example.chatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.latest_message_row.view.*
import java.nio.file.FileVisitResult

class LatestMessagesActivity : AppCompatActivity() {

    companion object {
        const val TAG = "LatestMessageActivity"
        var currentUser: User? = null
    }

    var adapter = GroupAdapter<ViewHolder>()
    var latsetsMessagesMap = HashMap<String, ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)
        title = " "

        recycler_view_latest_messages.adapter = adapter

        fetchCurrentUser()
        verifyUserIsLoggedInAndStartActivity()

        listenForLatestMessages()
        displayLastMessage()
    }

    private fun listenForLatestMessages() {

        // Grab reference to the logged in user
        val reference = FirebaseDatabase.getInstance().getReference("/latest-messages/${FirebaseAuth.getInstance().uid}")
        reference.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return

                latsetsMessagesMap[p0.key ?: return] = chatMessage
                refreshRecycleViewMessages()
            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return

                latsetsMessagesMap[p0.key ?: return] = chatMessage
                refreshRecycleViewMessages()
            }
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }
            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })
    }

    private fun refreshRecycleViewMessages() {
        adapter.clear()

        latsetsMessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun displayLastMessage() {

        FirebaseDatabase.getInstance().getReference("/")
    }

    private fun fetchCurrentUser() {
        val reference = FirebaseDatabase.getInstance().getReference("/users/${FirebaseAuth.getInstance().uid}")
        reference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                //TODO: Remove this title once development is done, no need for user to see his name
                title = currentUser?.username
                Log.d(TAG, "Current user is ${currentUser?.username}")
            }
        })
    }

    private fun verifyUserIsLoggedInAndStartActivity() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

class CustomProgressBar {
    fun showLoader() {
        TODO("Make it! Does not have necessarily to be custom..")
    }
}

class LatestMessageRow(val chatMessage: ChatMessage): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.latest_message_texview_latest_message.text = chatMessage.text
    }

}
