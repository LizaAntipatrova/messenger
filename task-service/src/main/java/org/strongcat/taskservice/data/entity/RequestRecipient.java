package org.strongcat.taskservice.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "request_recipient")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "specialist_id", nullable = false)
    private Specialist specialist;

    @Column(name = "cosine_similarity", nullable = false)
    private BigDecimal cosineSimilarity;

    @Column(name = "correction_coefficient", nullable = false)
    private BigDecimal correctionCoefficient;

    @Column(name = "final_score", nullable = false)
    private BigDecimal finalScore;

    @Column(name = "rank_position", nullable = false)
    private Integer rankPosition;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "response_status_id", nullable = false)
    private ResponseStatus responseStatus;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "external_message_id")
    private Long externalMessageId;

    @Column(name = "external_chat_id")
    private Long externalChatId;
}
