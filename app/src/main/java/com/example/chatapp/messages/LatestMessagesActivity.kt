package com.example.chatapp.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.chatapp.RegisterActivity
import com.example.chatapp.R
import com.example.chatapp.model.ChatMessage
import com.example.chatapp.model.User
import com.example.chatapp.views.LatestMessageRow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*

class LatestMessagesActivity : AppCompatActivity() {

    companion object {
        const val TAG = "LatestMessageActivity"
        val USER_KEY = "USER_KEY"
        var currentUser: User? = null
    }

    var adapter = GroupAdapter<ViewHolder>()
    var latsetsMessagesMap = HashMap<String, ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)
        title = " "

        recycler_view_latest_messages.adapter = adapter
        recycler_view_latest_messages.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        fetchCurrentUser()
        verifyUserIsLoggedInAndStartActivity()

        listenForLatestMessages()
        displayLastMessage()

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this , ChatLogActivity::class.java)

            val row = item as LatestMessageRow
            intent.putExtra(NewMessageActivity.USER_NAME, row.chatPartnerUser)
            startActivity(intent)
        }
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

