package com.example.carproject.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer memberId;

    private String name;
    private String phone;
    private String email;

    @Column(name = "login_id", unique = true)
    private String loginId;

    private String password;
    private LocalDateTime joinedAt;

    @Lob
    private String address;

    private Boolean isAddressPublic = false;

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }

    // ✅ Getter & Setter

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    // joinedAt은 @PrePersist로 자동 설정되므로 setter는 생략해도 됩니다.

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getIsAddressPublic() {
        return isAddressPublic;
    }

    public void setIsAddressPublic(Boolean isAddressPublic) {
        this.isAddressPublic = isAddressPublic;
    }
}
