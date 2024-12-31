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

@Repository
public interface CustomerRepository extends JpaRepository<CustomerModel, Long> {

    Page<CustomerModel> findByEmail(Pageable pageable, String email);

    @Query("SELECT COUNT(c) > 0 FROM CustomerModel c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    boolean existsByNameContainingIgnoreCase(@Param("name") String name);

    Page<CustomerModel> findByParentCompany(Pageable pageable, ParentCompanyModel parentCompany);

    @Query("SELECT c FROM CustomerModel c " +
            "WHERE LOWER(TRIM(c.state)) LIKE LOWER(CONCAT('%', :searchTxt, '%')) " +
            "OR LOWER(TRIM(c.city)) LIKE LOWER(CONCAT('%', :searchTxt, '%')) " +
            "OR LOWER(TRIM(c.name)) LIKE LOWER(CONCAT('%', :searchTxt, '%')) " +
            "OR LOWER(TRIM(c.pincode)) LIKE LOWER(CONCAT('%', :searchTxt, '%'))")
    Page<CustomerModel> findBySearchTxt(Pageable pageable, @Param("searchTxt") String searchTxt);

    @Query("SELECT c FROM CustomerModel c WHERE " +
            "(:email IS NULL OR :email = '' OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:city IS NULL OR :city = '' OR LOWER(c.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
            "(:state IS NULL OR :state = '' OR LOWER(c.state) LIKE LOWER(CONCAT('%', :state, '%')))")
    Page<CustomerModel> findByEmailCityOrState(
            @Param("email") String email,
            @Param("city") String city,
            @Param("state") String state, Pageable pageable);

    @Query("SELECT c FROM CustomerModel c WHERE " +
            "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:phone IS NULL OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :phone, '%'))) AND " +
            "(:pincode IS NULL OR LOWER(c.pincode) LIKE LOWER(CONCAT('%', :pincode, '%'))) AND " +
            "(:personOfContact IS NULL OR LOWER(c.personOfContact) LIKE LOWER(CONCAT('%', :personOfContact, '%')))")
    Page<CustomerModel> findByNamePhonePincodePersonOfContact(
            @Param("name") String customerName,
            @Param("phone") String phone,
            @Param("pincode") String pincode,
            @Param("personOfContact") String personOfContact,
            Pageable pageable);


    @Query("SELECT c FROM CustomerModel c WHERE c.id IN :ids")
    List<CustomerModel> findByIds(@Param("ids") List<Long> ids);


}
