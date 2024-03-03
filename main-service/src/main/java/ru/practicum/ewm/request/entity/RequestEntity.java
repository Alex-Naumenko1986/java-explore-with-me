package ru.practicum.ewm.request.entity;

import lombok.*;
import ru.practicum.ewm.request.enums.RequestStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Entity
@Table(name = "requests")
public class RequestEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "requester_id", nullable = false)
    private Integer requester;
    @Column(name = "event_id", nullable = false)
    private Integer event;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;
}
