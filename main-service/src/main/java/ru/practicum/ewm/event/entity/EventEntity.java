package ru.practicum.ewm.event.entity;

import lombok.*;
import ru.practicum.ewm.category.entity.CategoryEntity;
import ru.practicum.ewm.event.enums.EventStatus;
import ru.practicum.ewm.user.entity.UserEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "events")
public class EventEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "annotation", nullable = false)
    private String annotation;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    private UserEntity initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private LocationEntity location;

    @Column(name = "paid", nullable = false)
    private Boolean paid;

    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;

    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "publication_date")
    private LocalDateTime publishedOn;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private EventStatus state;

    @Transient
    private Integer confirmedRequests;

    @Transient
    private Integer views;
}
