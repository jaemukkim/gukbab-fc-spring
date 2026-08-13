package com.gukbabfc.uniform.dto;

import com.gukbabfc.uniform.entity.UniformSize;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원의 유니폼 사이즈, 등번호, 마킹 이름, 수량 입력값을 담습니다.
 */
@Getter
@Setter
public class UniformApplicationRequest {

    @NotNull(message = "사이즈를 선택해 주세요.")
    private UniformSize size;

    @NotNull(message = "등번호를 입력해 주세요.")
    @Min(value = 0, message = "등번호는 0 이상이어야 합니다.")
    @Max(value = 99, message = "등번호는 99 이하여야 합니다.")
    private Integer backNumber;

    @NotBlank(message = "마킹 이름을 입력해 주세요.")
    @Size(max = 20, message = "마킹 이름은 20자 이하로 입력해 주세요.")
    private String markingName;

    @NotNull(message = "수량을 입력해 주세요.")
    @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
    @Max(value = 10, message = "수량은 10개 이하여야 합니다.")
    private Integer quantity = 1;

    public static UniformApplicationRequest from(UniformApplicationView application) {
        UniformApplicationRequest request = new UniformApplicationRequest();
        request.setSize(application.size());
        request.setBackNumber(application.backNumber());
        request.setMarkingName(application.markingName());
        request.setQuantity(application.quantity());
        return request;
    }
}
