package com.shopeflier.app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.shopeflier.app.ChatActivity
import com.shopeflier.app.databinding.FragmentMessagesBinding
import com.shopeflier.app.models.Conversation
import com.shopeflier.app.utils.MessageManager
import com.shopeflier.app.utils.UserManager

class MessagesFragment : Fragment() {
    
    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!
    private lateinit var messageManager: MessageManager
    private lateinit var userManager: UserManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messageManager = MessageManager(requireContext())
        userManager = UserManager(requireContext())
        setupUI()
    }
    
    override fun onResume() {
        super.onResume()
        loadConversations()
    }
    
    private fun setupUI() {
        loadConversations()
    }
    
    private fun loadConversations() {
        val currentUser = userManager.getCurrentUser()
        if (currentUser == null) {
            showEmptyState("Please log in to view messages")
            return
        }
        
        // Add sample messages for demonstration
        messageManager.addSampleMessages(currentUser.email)
        
        val conversations = messageManager.getConversationsForUser(currentUser.email)
        
        if (conversations.isEmpty()) {
            showEmptyState("No messages yet\n\nStart browsing products and contact sellers!")
        } else {
            displayConversations(conversations)
        }
    }
    
    private fun showEmptyState(message: String) {
        // Clear any existing views first
        _binding?.let { binding ->
            binding.tvNoMessages.text = message
            binding.tvNoMessages.visibility = View.VISIBLE
            
            binding.tvNoMessages.setOnClickListener {
                showMessageFeatures()
            }
        }
    }
    
    private fun displayConversations(conversations: List<Conversation>) {
        binding.tvNoMessages.visibility = View.GONE
        
        // Create a container for conversations if it doesn't exist
        val conversationsContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        
        conversations.forEach { conversation ->
            val conversationView = createConversationView(conversation)
            conversationsContainer.addView(conversationView)
        }
        
        // Add the container to the parent layout
        val parentLayout = binding.root as? LinearLayout
        parentLayout?.let { parent ->
            // Remove existing conversation views
            for (i in parent.childCount - 1 downTo 0) {
                val child = parent.getChildAt(i)
                if (child is LinearLayout && child != binding.tvNoMessages.parent) {
                    parent.removeViewAt(i)
                }
            }
            parent.addView(conversationsContainer)
        }
    }
    
    private fun createConversationView(conversation: Conversation): View {
        val card = CardView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 8, 16, 8)
            }
            radius = 12f
            cardElevation = 4f
            setOnClickListener {
                openChat(conversation)
            }
        }
        
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(20, 16, 20, 16)
            gravity = android.view.Gravity.CENTER_VERTICAL
        }
        
        // Avatar placeholder
        val avatar = TextView(requireContext()).apply {
            text = conversation.otherUserName.take(1).uppercase()
            textSize = 20f
            setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark))
            gravity = android.view.Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(48, 48).apply {
                setMargins(0, 0, 16, 0)
            }
        }
        
        // Message content
        val contentLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        
        val nameText = TextView(requireContext()).apply {
            text = conversation.otherUserName
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
        }
        
        val productText = TextView(requireContext()).apply {
            text = if (conversation.productTitle != null) "About: ${conversation.productTitle}" else ""
            textSize = 12f
            setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark))
            visibility = if (conversation.productTitle != null) View.VISIBLE else View.GONE
        }
        
        val messageText = TextView(requireContext()).apply {
            text = conversation.lastMessage
            textSize = 14f
            setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            maxLines = 2
        }
        
        val timeText = TextView(requireContext()).apply {
            text = formatTime(conversation.lastMessageTime)
            textSize = 12f
            setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        }
        
        // Unread badge
        val unreadBadge = TextView(requireContext()).apply {
            text = if (conversation.unreadCount > 0) conversation.unreadCount.toString() else ""
            textSize = 12f
            setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
            gravity = android.view.Gravity.CENTER
            minWidth = 24
            minHeight = 24
            visibility = if (conversation.unreadCount > 0) View.VISIBLE else View.GONE
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 0, 0, 0)
            }
        }
        
        contentLayout.addView(nameText)
        if (conversation.productTitle != null) {
            contentLayout.addView(productText)
        }
        contentLayout.addView(messageText)
        contentLayout.addView(timeText)
        
        layout.addView(avatar)
        layout.addView(contentLayout)
        layout.addView(unreadBadge)
        
        card.addView(layout)
        return card
    }
    
    private fun openChat(conversation: Conversation) {
        val intent = Intent(requireContext(), ChatActivity::class.java).apply {
            putExtra("OTHER_USER_EMAIL", conversation.otherUserEmail)
            putExtra("OTHER_USER_NAME", conversation.otherUserName)
            conversation.productTitle?.let { putExtra("PRODUCT_TITLE", it) }
        }
        startActivity(intent)
    }
    
    private fun formatTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60000 -> "Just now"
            diff < 3600000 -> "${diff / 60000}m ago"
            diff < 86400000 -> "${diff / 3600000}h ago"
            diff < 604800000 -> "${diff / 86400000}d ago"
            else -> {
                val date = java.util.Date(timestamp)
                val format = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault())
                format.format(date)
            }
        }
    }
    
    private fun showMessageFeatures() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Start Messaging")
            .setMessage("Browse products and tap 'Chat with Seller' to start conversations!")
            .setPositiveButton("Browse Products") { _, _ ->
                (activity as? com.shopeflier.app.DashboardActivity)?.let { dashboardActivity ->
                    dashboardActivity.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                        com.shopeflier.app.R.id.bottom_navigation
                    )?.selectedItemId = com.shopeflier.app.R.id.nav_browse
                }
            }
            .setNegativeButton("OK", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
