package com.tutorial.uprit.repository;

import com.tutorial.uprit.model.Connection;
import com.tutorial.uprit.model.ConnectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    /** Check if a connection exists in either direction */
    @Query("SELECT c FROM Connection c WHERE " +
            "(c.requester.id = :userA AND c.receiver.id = :userB) OR " +
            "(c.requester.id = :userB AND c.receiver.id = :userA)")
    Optional<Connection> findByUserPair(@Param("userA") Long userA, @Param("userB") Long userB);

    /** Pending requests I received */
    List<Connection> findByReceiverIdAndStatusOrderByCreatedAtDesc(Long receiverId, ConnectionStatus status);

    /** My accepted connections (either side) */
    @Query("SELECT c FROM Connection c WHERE c.status = 'ACCEPTED' AND " +
            "(c.requester.id = :userId OR c.receiver.id = :userId) " +
            "ORDER BY c.createdAt DESC")
    List<Connection> findAcceptedConnections(@Param("userId") Long userId);

    /** Count accepted connections */
    @Query("SELECT COUNT(c) FROM Connection c WHERE c.status = 'ACCEPTED' AND " +
            "(c.requester.id = :userId OR c.receiver.id = :userId)")
    long countAcceptedConnections(@Param("userId") Long userId);

    /** Check connection status between two users */
    @Query("SELECT c.status FROM Connection c WHERE " +
            "(c.requester.id = :userA AND c.receiver.id = :userB) OR " +
            "(c.requester.id = :userB AND c.receiver.id = :userA)")
    Optional<ConnectionStatus> findStatusByUserPair(@Param("userA") Long userA, @Param("userB") Long userB);
}
