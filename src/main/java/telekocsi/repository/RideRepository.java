package telekocsi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import telekocsi.model.Ride;
import telekocsi.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    @Query("SELECT r FROM Ride r WHERE " +
            "(:from IS NULL OR LOWER(r.startPlace) LIKE LOWER(CONCAT('%', :from, '%'))) AND " +
            "(:to IS NULL OR LOWER(r.endPlace) LIKE LOWER(CONCAT('%', :to, '%'))) AND " +
            "(:startDate IS NULL OR r.startTime >= :startDate) AND " +
            "(:endDate IS NULL OR r.startTime < :endDate)")
    List<Ride> searchRides(@Param("from") String from,
                           @Param("to") String to,
                           @Param("startDate") LocalDateTime startDate,
                           @Param("endDate") LocalDateTime endDate);

    boolean existsByDriverId(Long driverId);

    List<Ride> findByDriver(User driver);

    List<Ride> findByPassengersContaining(User passenger);
}