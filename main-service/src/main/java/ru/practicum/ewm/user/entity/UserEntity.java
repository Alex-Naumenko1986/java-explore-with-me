package ru.practicum.ewm.user.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "subscription_available", nullable = false)
    private Boolean subscriptionAvailable;

    @ElementCollection(targetClass = Integer.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "subscriptions", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "subscribed_on", nullable = false)
    private Set<Integer> subscribedOn;
}
