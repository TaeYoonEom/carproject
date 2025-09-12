// com/example/carproject/service/CarServiceImpl.java
package com.example.carproject.service;

import com.example.carproject.dto.CarDto;
import com.example.carproject.dto.ComparableCarDto;
import com.example.carproject.dto.InsuranceDto;
import com.example.carproject.dto.SellerDto;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CarServiceImpl implements CarService {

    // 공용 이미지 경로 상수
    private static final String SAMPLE_FRONT   = "/img/car_image/car_sample_front.jpg";
    private static final String SAMPLE_BACK    = "/img/car_image/car_sample_back.jpg";
    private static final String SAMPLE_DRIVER  = "/img/car_image/car_sample_driver.jpg";
    private static final String SAMPLE_LEFT    = "/img/car_image/car_sample_left.jpg";
    private static final String SAMPLE_REAR    = "/img/car_image/car_sample_rear.jpg";
    private static final String SAMPLE_RIGHT   = "/img/car_image/car_sample_right.jpg";

    private boolean existsInStatic(String webPath) {
        return new ClassPathResource("static" + webPath).exists();
    }

    private String frontPath(Long carId) {
        return "/img/car_image/car_" + carId + "_front.jpg";
    }

    private Category categoryOf(Long carId) {
        int id = carId == null ? 0 : carId.intValue();
        if (1 <= id && id <= 15) return Category.KOREAN;
        if (16 <= id && id <= 30) return Category.EV;
        if (31 <= id && id <= 45) return Category.FOREIGN;
        return Category.UNKNOWN;
    }

    enum Category {
        KOREAN("국산차", "/korean"),
        EV("전기차", "/ev"),
        FOREIGN("수입차", "/foreign"),
        UNKNOWN("차량", "/");
        final String name;
        final String path;
        Category(String name, String path) { this.name = name; this.path = path; }
    }

    @Override
    public CarDto getCarDetail(Long carId) {
        // TODO 실제 DB 조회로 교체
        CarDto dto = new CarDto();
        dto.setCarId(carId);
        dto.setCarName("샘플 차량 " + carId);
        dto.setYear(2022);
        dto.setMileage(28000);
        dto.setPrice(19500000);
        dto.setFuelType("가솔린");
        dto.setTransmission("오토");
        dto.setDriveType("2WD");
        dto.setExteriorColor("화이트");
        dto.setInteriorColor("블랙");
        dto.setSaleLocation("서울");
        dto.setOwnershipStatus("소유중");
        dto.setSellerType("개인");
        dto.setCarNumber("12가3456");
        return dto;
    }

    @Override
    public String getMainImage(Long carId) {
        String front = frontPath(carId);
        return existsInStatic(front) ? front : SAMPLE_FRONT;
    }

    @Override
    public List<String> getAllImages(Long carId) {
        String main = getMainImage(carId); // 고유 앞면(또는 샘플 앞면)
        // 앞면 1장 + 공용 각도들(뒤/운전석/좌/후/우)
        return List.of(
                main,
                SAMPLE_LEFT,
                SAMPLE_RIGHT,
                SAMPLE_REAR,
                SAMPLE_DRIVER,
                SAMPLE_BACK
        );
    }

    @Override
    public List<String> getOptions(Long carId) {
        return List.of("내비게이션", "스마트키", "후방카메라");
    }

    @Override
    public Map<String, String> getInspectionMap(Long carId) {
        return Map.of("엔진오일", "정상", "브레이크", "정상", "타이어", "70% 이상");
    }

    @Override
    public String getInspectionImage(Long carId) {
        // 필요 시 차별화 가능. 지금은 null(문서 이미지 없음)
        return null;
    }

    @Override
    public InsuranceDto getInsurance(Long carId) {
        InsuranceDto dto = new InsuranceDto();
        dto.setAccidents(0);
        dto.setTotalLoss(0);
        dto.setFlood(0);
        dto.setPanels(0);
        dto.setCost(0);
        return dto;
    }

    @Override
    public List<ComparableCarDto> getComparables(CarDto car) {
        return Collections.emptyList();
    }

    @Override
    public SellerDto getSeller(Long carId) {
        SellerDto s = new SellerDto();
        s.setName("홍길동");
        s.setPhone("010-1234-5678");
        s.setStore("N카 강남지점");
        return s;
    }

    // 카테고리 노출용(컨트롤러에서 쓰기 쉽게 공개 메서드로)
    public String getCategoryName(Long carId) {
        return categoryOf(carId).name;
    }
    public String getCategoryPath(Long carId) {
        return categoryOf(carId).path;
    }
}
