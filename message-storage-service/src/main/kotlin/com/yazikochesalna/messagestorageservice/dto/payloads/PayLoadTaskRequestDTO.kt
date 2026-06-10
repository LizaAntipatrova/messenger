package com.yazikochesalna.messagestorageservice.dto.payloads

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.yazikochesalna.messagestorageservice.dto.MessagesJsonFormatDTO
import com.yazikochesalna.messagestorageservice.model.db.Attachment
import com.yazikochesalna.messagestorageservice.model.db.BaseMessage
import com.yazikochesalna.messagestorageservice.model.db.Message
import com.yazikochesalna.messagestorageservice.model.enums.MessageType
import lombok.NoArgsConstructor
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@NoArgsConstructor
data class PayLoadTaskRequestDTO(
    var requestId: Long,
    var initiatorId: Long,
    override var chatId: Long,
    var description: String,
    var payment: BigDecimal,
    var specializationName: String,
    var responseStatus: String,
) : PayLoadDTO, PayLoadConvertible {

    private data class StoredContent(
        val requestId: Long,
        val description: String,
        val payment: BigDecimal,
        val specializationName: String,
        val responseStatus: String,
    )

    override fun toMessage(
        messageId: UUID,
        type: MessageType,
        timestamp: LocalDateTime,
    ): Pair<Message, List<Attachment>> {
        val content = StoredContent(
            requestId = requestId,
            description = description,
            payment = payment,
            specializationName = specializationName,
            responseStatus = responseStatus,
        )
        val message = Message(
            id = messageId,
            type = type,
            sendTime = timestamp,
            senderId = initiatorId,
            chatId = chatId,
            text = mapper.writeValueAsString(content),
        )
        return message to emptyList()
    }

    override fun toMessageJsonFormatDTO(
        message: BaseMessage,
        attachments: List<Attachment>,
    ): MessagesJsonFormatDTO = MessagesJsonFormatDTO(
        messageId = message.id,
        type = message.type,
        timestamp = message.sendTime,
        payload = fromMessage(message),
    )

    companion object {
        private val mapper = ObjectMapper().registerModule(kotlinModule())

        fun fromMessage(message: BaseMessage): PayLoadTaskRequestDTO {
            val content = mapper.readValue<StoredContent>(message.text!!)
            return PayLoadTaskRequestDTO(
                requestId = content.requestId,
                initiatorId = message.senderId,
                chatId = message.chatId,
                description = content.description,
                payment = content.payment,
                specializationName = content.specializationName,
                responseStatus = content.responseStatus,
            )
        }
    }
}
