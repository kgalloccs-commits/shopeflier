package com.shopeflier.app

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.shopeflier.app.databinding.ActivityChatBinding
import com.shopeflier.app.models.Message
import com.shopeflier.app.utils.MessageManager
import com.shopeflier.app.utils.UserManager

class ChatActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityChatBinding
    private lateinit var messageManager: MessageManager
    private lateinit var userManager: UserManager
    private var otherUserEmail: String = ""
    private var otherUserName: String = ""
    private var productId: String? = null
    private var productTitle: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        messageManager = MessageManager(this)
        userManager = UserManager(this)
        
        // Get data from intent
        otherUserEmail = intent.getStringExtra("OTHER_USER_EMAIL") ?: ""
        otherUserName = intent.getStringExtra("OTHER_USER_NAME") ?: ""
        productId = intent.getStringExtra("PRODUCT_ID")
        productTitle = intent.getStringExtra("PRODUCT_TITLE")
        
        setupUI()
        loadMessages()
    }
    
    private fun setupUI() {
        binding.tvChatTitle.text = otherUserName
        
        if (productTitle != null) {
            binding.tvProductTitle.text = "About: $productTitle"
            binding.tvProductTitle.visibility = TextView.VISIBLE
        } else {
            binding.tvProductTitle.visibility = TextView.GONE
        }
        
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.btnSend.setOnClickListener {
            sendMessage()
        }
        
        binding.etMessage.setOnEditorActionListener { _, _, _ ->
            sendMessage()
            true
        }
    }
    
    private fun loadMessages() {
        val currentUser = userManager.getCurrentUser()
        if (currentUser == null) {
            finish()
            return
        }
        
        val messages = messageManager.getMessagesForConversation(currentUser.email, otherUserEmail)
        displayMessages(messages, currentUser.email)
        
        // Mark messages as read
        messageManager.markMessagesAsRead(currentUser.email, otherUserEmail)
    }
    
    private fun displayMessages(messages: List<Message>, currentUserEmail: String) {
        binding.layoutMessages.removeAllViews()
        
        if (messages.isEmpty()) {
            val emptyView = TextView(this).apply {
                text = "Start the conversation!\nSay hello to ${otherUserName}"
                setPadding(32, 64, 32, 32)
                textSize = 16f
                textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                setTextColor(ContextCompat.getColor(this@ChatActivity, android.R.color.darker_gray))
            }
            binding.layoutMessages.addView(emptyView)
        } else {
            messages.forEach { message ->
                val messageView = createMessageView(message, message.senderEmail == currentUserEmail)
                binding.layoutMessages.addView(messageView)
            }
            
            // Scroll to bottom
            binding.scrollView.post {
                binding.scrollView.fullScroll(android.widget.ScrollView.FOCUS_DOWN)
            }
        }
    }
    
    private fun createMessageView(message: Message, isFromCurrentUser: Boolean): android.view.View {
        val card = CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(
                    if (isFromCurrentUser) 64 else 16,
                    8,
                    if (isFromCurrentUser) 16 else 64,
                    8
                )
            }
            radius = 16f
            cardElevation = 2f
            setCardBackgroundColor(
                ContextCompat.getColor(
                    this@ChatActivity,
                    if (isFromCurrentUser) android.R.color.holo_blue_light else android.R.color.white
                )
            )
        }
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 16, 24, 16)
        }
        
        val messageText = TextView(this).apply {
            text = message.message
            textSize = 16f
            setTextColor(
                ContextCompat.getColor(
                    this@ChatActivity,
                    if (isFromCurrentUser) android.R.color.white else android.R.color.black
                )
            )
        }
        
        val timeText = TextView(this).apply {
            text = formatTime(message.timestamp)
            textSize = 12f
            setTextColor(
                ContextCompat.getColor(
                    this@ChatActivity,
                    if (isFromCurrentUser) android.R.color.white else android.R.color.darker_gray
                )
            )
            alpha = 0.7f
            setPadding(0, 8, 0, 0)
        }
        
        layout.addView(messageText)
        layout.addView(timeText)
        card.addView(layout)
        
        return card
    }
    
    private fun sendMessage() {
        val messageText = binding.etMessage.text.toString().trim()
        if (messageText.isEmpty()) return
        
        val currentUser = userManager.getCurrentUser() ?: return
        
        messageManager.sendMessage(
            senderEmail = currentUser.email,
            senderName = currentUser.name,
            receiverEmail = otherUserEmail,
            productId = productId,
            productTitle = productTitle,
            messageText = messageText
        )
        
        binding.etMessage.text?.clear()
        loadMessages()
        
        Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show()
    }
    
    private fun formatTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60000 -> "Just now"
            diff < 3600000 -> "${diff / 60000}m ago"
            diff < 86400000 -> "${diff / 3600000}h ago"
            else -> {
                val date = java.util.Date(timestamp)
                val format = java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault())
                format.format(date)
            }
        }
    }
}
