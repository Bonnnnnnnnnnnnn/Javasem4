package com.customer.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.customer.repository.ChatService;
import com.models.ChatMessage;
import com.models.ChatRoom;
import com.models.Employee;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/support")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public ChatMessage handleChat(@DestinationVariable Integer roomId, 
                                @Payload ChatMessage message) {
        return chatService.saveMessage(message);
    }
    
    @MessageMapping("/chat.accept/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public ChatRoom acceptChat(@DestinationVariable Integer roomId,
                             @Payload Integer employeeId) {
        return chatService.acceptChat(roomId, employeeId);
    }
    @PostMapping("/room/create")
    public ResponseEntity<?> createChatRoom(@RequestParam Integer orderId) {
        try {
           
            ChatRoom existingRoom = chatService.findChatRoomByOrderId(orderId);
            if (existingRoom != null) {
                return ResponseEntity.ok(existingRoom); 
            }
            
            ChatRoom newRoom = chatService.createChatRoom(orderId);
            return ResponseEntity.ok(newRoom);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to create chat room: " + e.getMessage());
        }
    }

    @GetMapping("/messages/{roomId}") 
    public ResponseEntity<?> getChatMessages(@PathVariable Integer roomId) {
        try {
            List<ChatMessage> messages = chatService.getChatMessages(roomId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to get messages");
        }
    }
    @MessageMapping("/chat/send/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessage handleMessage(@DestinationVariable String roomId, ChatMessage message) {
        try {
            message.setTimestamp(LocalDateTime.now());
            
            // Log để debug
            System.out.println("Received message: " + message.getMessage() + " for room: " + roomId);
            
            // Lưu tin nhắn vào database
            ChatMessage savedMessage = chatService.save(message);
            
            // Log sau khi lưu
            System.out.println("Saved message with ID: " + savedMessage.getId());
            
            return savedMessage;
        } catch (Exception e) {
            System.err.println("Error saving message: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    @PostMapping("/mark-read/{roomId}")
    @ResponseBody
    public ResponseEntity<?> markMessagesAsRead(@PathVariable int roomId, HttpServletRequest request) {
        try {
        	Employee emp = (Employee)  request.getSession().getAttribute("loggedInEmployee");
            if (emp == null) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            
            chatService.markMessagesAsRead(roomId, emp.getId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error marking messages as read");
        }
    }

  
    @GetMapping("/unread-count")
    @ResponseBody
    public ResponseEntity<?> getUnreadCount(HttpServletRequest request) {
        try {
        	Employee emp = (Employee)  request.getSession().getAttribute("loggedInEmployee");
            if (emp == null) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            
            int unreadCount = chatService.getUnreadMessageCount(emp.getId());
            return ResponseEntity.ok(unreadCount);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error getting unread count");
        }
    }

    // Đóng chat room
    @PostMapping("/close/{roomId}")
    @ResponseBody
    public ResponseEntity<?> closeChatRoom(@PathVariable int roomId,HttpServletRequest request) {
        try {
        	Employee emp = (Employee)  request.getSession().getAttribute("loggedInEmployee");
            if (emp == null) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            
            boolean success = chatService.closeChatRoom(roomId, emp.getId());
            return ResponseEntity.ok(success);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error closing chat room");
        }
    }
    @GetMapping("/room/{roomId}/last-message")
    public ResponseEntity<?> getLastMessage(@PathVariable int roomId) {
        try {
            String lastMessage = chatService.getLastMessage(roomId);
            return ResponseEntity.ok(lastMessage);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error getting last message");
        }
    }

    // Thêm endpoint kiểm tra trạng thái chat room
    @GetMapping("/room/{roomId}/status")
    public ResponseEntity<?> getRoomStatus(@PathVariable int roomId) {
        try {
            ChatRoom room = chatService.findChatRoomById(roomId);
            if (room == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(room.getStatus());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error getting room status");
        }
    }
}