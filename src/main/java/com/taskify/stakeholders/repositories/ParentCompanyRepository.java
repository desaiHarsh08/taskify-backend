package com.taskify.stakeholders.repositories;

import com.taskify.stakeholders.models.CustomerModel;
import com.taskify.stakeholders.models.ParentCompanyModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParentCompanyRepository extends JpaRepository<ParentCompanyModel, Long> {

    Page<ParentCompanyModel> findByEmail(Pageable pageable, String email);

    @Query("SELECT p FROM ParentCompanyModel p " +
            "WHERE :searchTxt IS NULL OR " +
            "LOWER(TRIM(p.state)) LIKE LOWER(CONCAT('%', :searchTxt, '%')) " +
            "OR LOWER(TRIM(p.city)) LIKE LOWER(CONCAT('%', :searchTxt, '%')) " +
            "OR LOWER(TRIM(p.name)) LIKE LOWER(CONCAT('%', :searchTxt, '%')) " +
            "OR LOWER(TRIM(p.pincode)) LIKE LOWER(CONCAT('%', :searchTxt, '%'))")
    Page<ParentCompanyModel> findBySearchTxt(
            Pageable pageable,
            @Param("searchTxt") String searchTxt
    );

    @Query("SELECT p FROM ParentCompanyModel p WHERE " +
            "(:name IS NULL OR :name = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:city IS NULL OR :city = '' OR LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
            "(:state IS NULL OR :state = '' OR LOWER(p.state) LIKE LOWER(CONCAT('%', :state, '%'))) AND " +
            "(:pincode IS NULL OR :pincode = '' OR LOWER(p.pincode) LIKE LOWER(CONCAT('%', :pincode, '%')))")
    Page<ParentCompanyModel> findByNameCityStatePincode(
            @Param("name") String name,
            @Param("city") String city,
            @Param("state") String state,
            @Param("pincode") String pincode,
            Pageable pageable);

}
