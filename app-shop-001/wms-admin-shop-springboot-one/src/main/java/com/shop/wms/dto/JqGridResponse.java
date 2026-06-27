package com.shop.wms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JqGridResponse {
    private int page;
    private int total;
    private long records;

    @Builder.Default
    private List<JqGridRow> rows = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JqGridRow {
        private Long id;
        private List<Object> cell;
    }
}
